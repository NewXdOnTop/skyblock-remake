package com.sweattypalms.skyblock.commands.handlers;

import com.cryptomorin.xseries.XMaterial;
import com.sweattypalms.skyblock.commands.Command;
import com.sweattypalms.skyblock.commands.CommandArgs;
import com.sweattypalms.skyblock.core.player.SkyblockPlayer;
import com.sweattypalms.skyblock.dungeons.generator.DungeonGenerator;
import com.sweattypalms.skyblock.dungeons.physical.DungeonMap;
import com.sweattypalms.skyblock.dungeons.physical.DungeonMapRenderer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.Random;

public class DungeonCommands {

    @Command(name = "dungeon", description = "Dungeon command", op = true, inGameOnly = true, runSync = false)
    public void dungeonCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getSkyblockPlayer(player);

        skyblockPlayer.sendMessage("$cGenerating dungeon map");

        DungeonGenerator generator = new DungeonGenerator();
        generator.generate(new Random().nextLong());

        DungeonMap dungeonMap = new DungeonMap(generator);

        World world = player.getWorld();

        player.getInventory().addItem(dungeonMap.getItem(world));

        skyblockPlayer.sendMessage("$aDungeon map generated");
    }
}
