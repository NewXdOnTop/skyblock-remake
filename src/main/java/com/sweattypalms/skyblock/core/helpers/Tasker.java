package com.sweattypalms.skyblock.core.helpers;

import com.sweattypalms.skyblock.SkyBlock;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class Tasker {


    public void runTaskLater(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(SkyBlock.getInstance(), runnable, delay);
    }

    public void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(SkyBlock.getInstance(), runnable);
    }

    public void runTaskAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(SkyBlock.getInstance(), runnable);
    }
}
