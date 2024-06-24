package com.sweattypalms.skyblock.commands.handlers;

import com.sweattypalms.skyblock.commands.Command;
import com.sweattypalms.skyblock.commands.CommandArgs;
import com.sweattypalms.skyblock.core.items.builder.SkyblockItem;
import com.sweattypalms.skyblock.core.player.SkyblockPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UtilCommands {

    @Command(name = "gms", description = "Change gamemode to survival", op = true)
    public void gmsCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        if (player == null) return;
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage(ChatColor.RED + "Your gamemode has been updated to survival!");
    }

    @Command(name = "gmc", description = "Change gamemode to creative", op = true)
    public void gmcCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        if (player == null) return;
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage(ChatColor.RED + "Your gamemode has been updated to creative!");
    }

    @Command(name = "gmss", description = "Change gamemode to spectator", op = true)
    public void gmssCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        if (player == null) return;
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(ChatColor.RED + "Your gamemode has been updated to spectator!");
    }

    @Command(name = "fix_inventory", description = "Fix inventory")
    public void fixInventoryCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        if (player == null) return;
        ItemStack[] items = player.getInventory().getContents();

        for (ItemStack item : items) {
            SkyblockItem.updateItemStack(SkyblockPlayer.getSkyblockPlayer(player), item);
        }

        player.updateInventory();
        player.sendMessage(ChatColor.RED + "Your inventory has been updated!");
    }

}
