package com.sweattypalms.skyblock.commands;

import com.sweattypalms.skyblock.SkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandRegistry {
    private static CommandRegistry instance;
    private final Map<String, MethodContainer> commands = new HashMap<>();
    private static final SimpleCommandMap simpleCommandMap;

    static {
        try {
            final Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMap.setAccessible(true);
            simpleCommandMap = (SimpleCommandMap) commandMap.get(Bukkit.getServer());
            commandMap.setAccessible(false);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CommandRegistry() {
        instance = this;
    }

    public static CommandRegistry getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Cannot call getInstance() before the plugin has been enabled!");
        }
        return instance;
    }

    public int getCommandsAmt() {
        return this.commands.size();
    }

    public void registerAll() {
        //Reflections reflections = new Reflections("com.sweattypalms.skyblock.commands.handlers");
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("com.sweattypalms.skyblock.commands.handlers"))
                .setScanners(Scanners.MethodsAnnotated));

        Set<Method> methods = reflections.getMethodsAnnotatedWith(Command.class);

        for (Method method : methods) {
            Object instance;
            try {
                instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                register(instance);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        for (MethodContainer container : commands.values()) {
            registerCommandWithBukkit(container);
        }
    }

    private void register(Object obj) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command commandInfo = method.getAnnotation(Command.class);
                commands.put(commandInfo.name().toLowerCase(), new MethodContainer(obj, method));

                for (String alias : commandInfo.aliases()) {
                    commands.put(alias.toLowerCase(), new MethodContainer(obj, method));
                }
            }
        }
        // Run tab completer afterwards because the command may not be registered before.
        for (Method method : obj.getClass().getDeclaredMethods()) {
            // Register tab completer methods
            if (method.isAnnotationPresent(TabCompleter.class)) {
                TabCompleter completerInfo = method.getAnnotation(TabCompleter.class);
                MethodContainer container = commands.get(completerInfo.command().toLowerCase());

                if (container != null) {
                    container.tabCompleterMethod = method;
                } else {
                    // Handle case where there's a completer for a non-existent command
                    System.out.println(ChatColor.YELLOW + "[Warning] TabCompleter for non-existent command: " + completerInfo.command());
                }
            }
        }
    }

    /**
     * Okay so this is a better method than the old one
     * because it doesn't require any event and the command gets registered as a bukkit command.
     * Registers the command with Bukkit.
     * @param container The container containing the command method.
     */
    private void registerCommandWithBukkit(final MethodContainer container) {
        final Command cmdInfo = container.commandMethod.getAnnotation(Command.class);
        try {
            simpleCommandMap.register("skyblock", new CommandManagerExecutor(cmdInfo));
        } catch (Exception e) {
            System.out.printf("Failed to register command %s%n", container.commandMethod.getName());
            throw new RuntimeException(e);
        }
    }

    public boolean executeCommand(final CommandArgs sbCommandArgs) {
        final String command = sbCommandArgs.getCommand().getName();
        final Player player = sbCommandArgs.getPlayer();
        final MethodContainer container = this.commands.get(command.toLowerCase());
        if (container == null) return false;

        final Command cmdInfo = container.commandMethod.getAnnotation(Command.class);
        final boolean runSync = cmdInfo.runSync();
        if (cmdInfo.inGameOnly() && !sbCommandArgs.isPlayer()) {
            sbCommandArgs.getSender().sendMessage(ChatColor.RED + "This command can only be executed in-game.");
            return true;
        }
        if (cmdInfo.op() && player != null && !player.isOp() && cmdInfo.inGameOnly()) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (!cmdInfo.permission().isEmpty() && player != null && !player.hasPermission(cmdInfo.permission())) {
            //If the player doesn't have the required permission!
            player.sendMessage(cmdInfo.noPerm());
            return true;
        }
        // Trying to get commandMethod with args of (CommandArgs sbCommandArgs)
        try {
            if (runSync) {
                container.commandMethod.invoke(container.instance, sbCommandArgs);
            } else {
                //Running the command async because performance matters.
                Bukkit.getScheduler().runTaskAsynchronously(SkyBlock.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            container.commandMethod.invoke(container.instance, sbCommandArgs);
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public List<String> handleTabCompletion(final CommandArgs sbCommandArgs) {
        final String command = sbCommandArgs.getCommand().getName();
        final MethodContainer container = this.commands.get(command.toLowerCase());
        if (container != null && container.tabCompleterMethod != null) {
            try {
                return (List<String>) container.tabCompleterMethod.invoke(container.instance, sbCommandArgs);
            } catch (Exception e) {
                // Handle the exception gracefully, possibly log it for debugging
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    public static class MethodContainer {
        public Method commandMethod;
        public Method tabCompleterMethod;
        public Object instance;

        public MethodContainer(Object instance, Method commandMethod) {
            this.instance = instance;
            this.commandMethod = commandMethod;
        }
    }

    public class CommandManagerExecutor extends BukkitCommand {
        private final String command;

        /**
         * Constructor for the command manager executor.
         *
         * @param cmd annotation {@link Command} which contains the useful information about the command.
         */
        public CommandManagerExecutor(final Command cmd) {
            super(cmd.name());
            this.command = cmd.name();
            this.setDescription(cmd.description());
            this.setUsage(cmd.usage());
            this.setPermission(cmd.permission());
            this.setPermissionMessage(cmd.noPerm());
            this.setName(command);
            if (cmd.aliases().length > 0) this.setAliases(Arrays.asList(cmd.aliases()));

        }

        @Override
        public boolean execute(final CommandSender sender, final String label, final String[] args) {
            return this.onCommand(sender, this, label, args);
        }


        public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command cmd, final String label, final String[] args) {
            if (cmd.getName().equalsIgnoreCase(this.command)) {
                final CommandArgs sbCommandArgs = new CommandArgs(sender, this, label, args);
                if (CommandRegistry.this.executeCommand(sbCommandArgs)) return true;
            }
            return true;
        }

        @Override
        public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException {
            final CommandArgs sbCommandArgs = new CommandArgs(sender, this, alias, args);
            final List<String> completions = CommandRegistry.this.handleTabCompletion(sbCommandArgs);
            final List<String> result = new ArrayList<>(completions);
            for (final String a : completions) {
                try {
                    if (a.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                        result.clear();
                        result.add(a);
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
            return result;
        }

    }
}
