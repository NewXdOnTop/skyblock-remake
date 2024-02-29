package com.sweattypalms.skyblock.core.events.listeners;

import com.sweattypalms.skyblock.commands.CommandListener;
import io.netty.channel.*;
import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Used to read packets both IN and OUT.
 */
public class PacketReader implements Listener {

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        injectPlayer(e.getPlayer());
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {
        unInjectPlayer(e.getPlayer());
    }

    public void unInjectPlayer(Player p) {
        Channel channel = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(p.getName());
            return null;
        });
    }

    public void injectPlayer(Player p) {
        CommandListener commandListener = new CommandListener();
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);
                if (packet instanceof PacketPlayInTabComplete)
                    commandListener.onTabCompleteEvent((PacketPlayInTabComplete) packet, p);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                super.write(ctx, msg, promise);
            }

        };
        ChannelPipeline pipeline = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", p.getName(), channelDuplexHandler);
    }
}
