package com.sweattypalms.skyblock.core.mobs.regions.test;

import com.sweattypalms.skyblock.core.mobs.builder.ISkyblockMob;
import com.sweattypalms.skyblock.core.mobs.builder.MobAttributes;
import com.sweattypalms.skyblock.core.mobs.builder.SkyblockMob;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;


public class TestEntity extends EntityZombie implements ISkyblockMob {
    public static final String ID = "test_entity";

    private final SkyblockMob skyblockMob;

    public TestEntity(Location location, SkyblockMob skyblockMob) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.skyblockMob = skyblockMob;
        this.skyblockMob
                .setMaxHealth(100_000_000)
                .setCustomName("$cFlorida man")
                .setLevel(999)
//                .setLoot(new MobLoot().addConfirmedDrop(SkyblockItem.get()))
                .setAttribute(MobAttributes.COMBAT_XP, 50d);
        ;
        skyblockMob.setAttribute(MobAttributes.AI_ENABLED, false);
        this.initPathfinder();
    }

    @Override
    public SkyblockMob getSkyblockMob() {
        return skyblockMob;
    }

    @Override
    public EntityLiving getEntityInstance() {
        return this;
    }


    protected void initPathfinder() {
        //look at player
        this.goalSelector.a(0, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));    // Follow player
    }

    @Override
    public void setHealth(float f) {
        super.setHealth(f);
        damageEntityDebug();
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        boolean b = super.damageEntity(damagesource, f);
        damageEntityDebug();
        return b;
    }

    private void damageEntityDebug() {
//        System.out.println("damageEntity called");
        if (this.getHealth() <= 0.0F) {
            killEntityDebug();
        }
    }

    private void killEntityDebug() {
        System.out.println("killEntityDebug called");
    }

    public void goTo(Location location) {
        NavigationAbstract navigationabstract = this.getNavigation();
        navigationabstract.a(
                location.getX(),
                location.getY(),
                location.getZ(),
                1.0D);
    }
}
