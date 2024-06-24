package com.sweattypalms.skyblock.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
public class CommandArgs {

    /**
     * -- GETTER --
     * Gets the command sender
     *
     * @return
     */
    private final CommandSender sender;
    /**
     * -- GETTER --
     * Gets the original command object
     *
     * @return
     */
    private final org.bukkit.command.Command command;
    /**
     * -- GETTER --
     * Gets the label including sub command labels of this command
     *
     * @return Something like 'test.subcommand'
     */
    private final String label;
    /**
     * -- GETTER --
     * Gets all the arguments after the command's label. ie. if the command
     * label was test.subcommand and the arguments were subcommand foo foo, it
     * would only return 'foo foo' because 'subcommand' is part of the command
     *
     * @return
     */
    private final String[] args;

    public CommandArgs(final CommandSender sender, final org.bukkit.command.Command command, final String label, final String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
    }

    /**
     * Gets the argument at the specified index
     *
     * @param index The index to get
     * @return The string at the specified index
     */
    public String getArgs(final int index) {
        return args[index];
    }

    /**
     * Returns the length of the command arguments
     *
     * @return int length of args
     */
    public int length() {
        return args.length;
    }

    public boolean isPlayer() {
        return this.sender instanceof Player;
    }

    public Player getPlayer() {
        if (this.sender instanceof Player) {
            return (Player) this.sender;
        } else {
            return null;
        }
    }
}
