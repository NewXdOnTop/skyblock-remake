package com.sweattypalms.skyblock.core.helpers.nms;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * Reflections class for handling NMS and CraftBukkit classes.
 * @author Shlok
 */
public class Reflections {

    public static final String VERSION;

    static {
        String found = null;
        for (Package pack : Package.getPackages()) {
            String name = pack.getName();
            if (name.startsWith("org.bukkit.craftbukkit.v")) {
                found = pack.getName().split("\\.")[3];
                try {
                    Class.forName("org.bukkit.craftbukkit." + found + ".entity.CraftPlayer");
                    break;
                } catch (ClassNotFoundException e) {
                    found = null;
                }
            }
        }
        if (found == null) //Couldn't find nms implementation.
            throw new IllegalArgumentException("Failed to parse server version. Could not find any package starting with name: 'org.bukkit.craftbukkit.v'");
        VERSION = found;
    }

    public static final int VER = Integer.parseInt(VERSION.substring(1).split("_")[1]);

    public static final String CRAFTBUKKIT = "org.bukkit.craftbukkit." + VERSION + '.';

    public static final String NMS = v(17, "net.minecraft.").orElse("net.minecraft.server." + VERSION + '.');

    private static final MethodHandle PLAYER_CONNECTION;

    private static final MethodHandle GET_HANDLE;

    private static final MethodHandle SEND_PACKET;

    static {
        Class<?> entityPlayer = getNMSClass("server.level", "EntityPlayer");
        Class<?> craftPlayer = getCraftClass("entity.CraftPlayer");
        Class<?> playerConnection = getNMSClass("server.network", "PlayerConnection");
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle sendPacket = null, getHandle = null, connection = null;
        try {
            connection = lookup.findGetter(entityPlayer,
                    v(17, "b").orElse("playerConnection"), playerConnection);
            getHandle = lookup.findVirtual(craftPlayer, "getHandle", MethodType.methodType(entityPlayer));
            sendPacket = lookup.findVirtual(playerConnection,
                    v(18, "a").orElse("sendPacket"),
                    MethodType.methodType(void.class, getNMSClass("network.protocol", "Packet")));
        } catch (NoSuchMethodException|NoSuchFieldException|IllegalAccessException ex) {
            ex.printStackTrace();
        }
        PLAYER_CONNECTION = connection;
        SEND_PACKET = sendPacket;
        GET_HANDLE = getHandle;
    }

    public static <T> VersionHandler<T> v(int version, T handle) {
        return new VersionHandler<>(version, handle);
    }

    public static <T> CallableVersionHandler<T> v(int version, Callable<T> handle) {
        return new CallableVersionHandler<>(version, handle);
    }

    public static boolean supports(int version) {
        return (VER >= version);
    }

    /**
     * Get the NMS class with the new package name.
     * @param newPackage - Class package in new version
     * @param name - Class name in legacy version
     * @return The NMS Class
     */
    @Nullable
    public static Class<?> getNMSClass(@NotNull String newPackage, @NotNull String name) {
        if (supports(17))
            name = newPackage + '.' + name;
        return getNMSClass(name);
    }

    /**
     *
     * @param name - Class name in the current version
     * @return The NMS Class if exist or else null
     */
    @Nullable
    public static Class<?> getNMSClass(@NotNull String name) {
        try {
            return Class.forName(NMS + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * This method is used to send packets to the player asynchronously.
     * @param player - To whom we should send the packets.
     * @param packets - The packets which we should send.
     * @return CompletableFuture<Void> - The future of the task.
     */
    @NotNull
    public static CompletableFuture<Void> sendPacket(@NotNull Player player, @NotNull Object... packets) {
        return CompletableFuture.runAsync(() -> sendPacketSync(player, packets))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    public static void sendPacketSync(@NotNull Player player, @NotNull Object... packets) {
        try {
            Object handle = GET_HANDLE.invoke(player);
            Object connection = PLAYER_CONNECTION.invoke(handle);
            if (connection != null)
                for (Object packet : packets)
                    SEND_PACKET.invoke(connection, packet);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * Get the handle of the player.
     * @param player - The player whose handle we want.
     * @return The handle of the player.
     */
    @Nullable
    public static Object getHandle(@NotNull Player player) {
        Objects.requireNonNull(player, "Cannot get handle of null player");
        try {
            return GET_HANDLE.invoke(player);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    /**
     * Get the connection of the player.
     * @param player - The player whose connection we want.
     * @return The connection of the player.
     */
    @Nullable
    public static Object getConnection(@NotNull Player player) {
        Objects.requireNonNull(player, "Cannot get connection of null player");
        try {
            Object handle = GET_HANDLE.invoke(player);
            return PLAYER_CONNECTION.invoke(handle);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    /**
     * Get the CraftBukkit class in the current version.
     * @param name - Class name in the current version
     * @return The CraftBukkit Class if exist or else null
     */
    @Nullable
    public static Class<?> getCraftClass(@NotNull String name) {
        try {
            return Class.forName(CRAFTBUKKIT + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Class<?> getArrayClass(String clazz, boolean nms) {
        clazz = "[L" + (nms ? NMS : CRAFTBUKKIT) + clazz + ';';
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Class<?> toArrayClass(Class<?> clazz) {
        try {
            return Class.forName("[L" + clazz.getName() + ';');
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final class VersionHandler<T> {
        private int version;

        private T handle;

        private VersionHandler(int version, T handle) {
            if (Reflections.supports(version)) {
                this.version = version;
                this.handle = handle;
            }
        }

        public VersionHandler<T> v(int version, T handle) {
            if (version == this.version)
                throw new IllegalArgumentException("Cannot have duplicate version handles for version: " + version);
            if (version > this.version && Reflections.supports(version)) {
                this.version = version;
                this.handle = handle;
            }
            return this;
        }

        public T orElse(T handle) {
            return (this.version == 0) ? handle : this.handle;
        }
    }

    public static final class CallableVersionHandler<T> {
        private int version;

        private Callable<T> handle;

        private CallableVersionHandler(int version, Callable<T> handle) {
            if (Reflections.supports(version)) {
                this.version = version;
                this.handle = handle;
            }
        }

        public CallableVersionHandler<T> v(int version, Callable<T> handle) {
            if (version == this.version)
                throw new IllegalArgumentException("Cannot have duplicate version handles for version: " + version);
            if (version > this.version && Reflections.supports(version)) {
                this.version = version;
                this.handle = handle;
            }
            return this;
        }

        public T orElse(Callable<T> handle) {
            try {
                return ((this.version == 0) ? handle : this.handle).call();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
