package com.sweattypalms.skyblock.core.mobs.builder;


import net.minecraft.server.v1_8_R3.EntityLiving;

public interface ISkyblockMob {
    SkyblockMob getSkyblockMob();
    EntityLiving getEntityInstance();
}
