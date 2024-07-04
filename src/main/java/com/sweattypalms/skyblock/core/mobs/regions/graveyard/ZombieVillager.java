package com.sweattypalms.skyblock.core.mobs.regions.graveyard;

import com.sweattypalms.skyblock.core.helpers.EntityHelper;
import com.sweattypalms.skyblock.core.helpers.XMaterial;
import com.sweattypalms.skyblock.core.mobs.builder.IRegionEntity;
import com.sweattypalms.skyblock.core.mobs.builder.ISkyblockMob;
import com.sweattypalms.skyblock.core.regions.Regions;
import com.sweattypalms.skyblock.core.mobs.builder.SkyblockMob;

import net.minecraft.server.v1_8_R3.EntityZombie;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import net.minecraft.server.v1_8_R3.EntityLiving;

public class ZombieVillager extends EntityZombie implements ISkyblockMob, IRegionEntity {

    public static final String ID = "zombie_villager";

    private final SkyblockMob skyblockMob;

    public ZombieVillager(Location location, SkyblockMob skyblockMob) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.skyblockMob = skyblockMob;
        this.skyblockMob
                .setMaxHealth(120)
                .setDamage(24)
                .setCustomName("$cZombie Villager")
                .setLevel(1)
        ;
        this.setVillager(true);
        EntityHelper.equipAllArmor(this, XMaterial.LEATHER);
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
