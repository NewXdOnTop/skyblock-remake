package com.sweattypalms.skyblock.core.world;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;

public class WorldManager {

    static double tickTime = 0;

    public static void tick() {
        tickTime++;

        if (tickTime % 40 == 0) { // Every 2 seconds
            cleanupArrow();
        }
    }

    private static void cleanupArrow() {
        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(Arrow.class).forEach(arrow -> {
            if (arrow.isOnGround()) {
                arrow.remove();
            }
        }));
    }

    public static void init() {
//later
    }
}
