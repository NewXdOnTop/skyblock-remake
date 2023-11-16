package com.sweattypalms.skyblock.core.mobs.regions.graveyard;

import com.sweattypalms.skyblock.core.mobs.builder.IRegionEntity;
import com.sweattypalms.skyblock.core.mobs.builder.ISkyblockMob;
import com.sweattypalms.skyblock.core.mobs.builder.SkyblockMob;
import com.sweattypalms.skyblock.core.regions.Regions;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityZombie;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class GraveyardZombie extends EntityZombie implements ISkyblockMob, IRegionEntity {

    public static final String ID = "graveyard_zombie";

    private final SkyblockMob skyblockMob;

    public GraveyardZombie(Location location, SkyblockMob skyblockMob) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.skyblockMob = skyblockMob;
        this.skyblockMob
                .setMaxHealth(100)
                .setDamage(20)
                .setCustomName("$cGraveyard Zombie")
                .setLevel(1)
        ;
    }

    @Override
    public Regions getRegion() {
        return Regions.GRAVEYARD;
    }


    @Override
    public SkyblockMob getSkyblockMob() {
        return skyblockMob;
    }

    @Override
    public EntityLiving getEntityInstance() {
        return this;
    }
}
