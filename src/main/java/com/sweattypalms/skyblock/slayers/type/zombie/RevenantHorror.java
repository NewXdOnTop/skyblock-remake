package com.sweattypalms.skyblock.slayers.type.zombie;

import com.sweattypalms.skyblock.SkyBlock;
import com.sweattypalms.skyblock.api.Hologram;
import com.sweattypalms.skyblock.core.helpers.EntityHelper;
import com.sweattypalms.skyblock.core.items.builder.SkyblockItem;
import com.sweattypalms.skyblock.core.items.builder.SkyblockItemType;
import com.sweattypalms.skyblock.core.items.types.slayer.zombie.items.BeheadedHorror;
import com.sweattypalms.skyblock.core.mobs.builder.ISkyblockMob;
import com.sweattypalms.skyblock.core.mobs.builder.MobAttributes;
import com.sweattypalms.skyblock.core.mobs.builder.NameAttributes;
import com.sweattypalms.skyblock.core.mobs.builder.SkyblockMob;
import com.sweattypalms.skyblock.core.player.SkyblockPlayer;
import com.sweattypalms.skyblock.slayers.ISlayerMob;
import com.sweattypalms.skyblock.slayers.Slayer;
import com.sweattypalms.skyblock.slayers.SlayerTimer;
import com.sweattypalms.skyblock.slayers.events.SlayerFailEvent;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityZombie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public abstract class RevenantHorror extends EntityZombie implements ISkyblockMob, ISlayerMob {

    protected final SkyblockMob skyblockMob;
    protected final SlayerTimer slayerTimer;
    protected final long startTime;

    protected SkyblockPlayer ownerPlayer;
    private Hologram hologramName;
    protected int tier;

    public RevenantHorror(Location location, SkyblockMob skyblockMob) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.skyblockMob = skyblockMob;

        equipArmor();
        setStats();

        this.getSkyblockMob()
                .setNameAttribute(NameAttributes.FORMATTED, true)
                .setNameAttribute(NameAttributes.SHOW_LEVEL, false)
                .setAttribute(MobAttributes.SPEED, 200);

        this.slayerTimer = new SlayerTimer(this.skyblockMob);
        this.startTime = System.currentTimeMillis();
        double height = this.getBoundingBox().e - this.getBoundingBox().b;
        this.hologramName = new Hologram(skyblockMob.getNameAttribute(NameAttributes.CUSTOM_NAME), skyblockMob, height);
        this.hologramName.start();
    }

    public void equipArmor() {
        SkyblockItem beheadedHorror = SkyblockItem.get(BeheadedHorror.ID);
        EntityHelper.equipItem(this, SkyblockItemType.HELMET, beheadedHorror.toItemStack());

        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chestplate.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 7);
        EntityHelper.equipItem(this, SkyblockItemType.CHESTPLATE, chestplate);

        ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        leggings.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 7);
        EntityHelper.equipItem(this, SkyblockItemType.LEGGINGS, leggings);

        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        EntityHelper.equipItem(this, SkyblockItemType.BOOTS, boots);
    }


    long ticks = 0;

    @Override
    public void t_() {
        super.t_();
        ticks++;

        if (!valid())
            return;

        if (ticks % 20 == 0) {
            long timeLeft = Slayer.MAX_TIME - (System.currentTimeMillis() - getStartTime()) / 1000;
            if (timeLeft <= 0) {
                Bukkit.getScheduler().runTask(SkyBlock.getInstance(), () -> {
                    if (valid()) {
                        this.getSkyblockMob().getEntityInstance().setHealth(0);
                        SlayerFailEvent failEvent = new SlayerFailEvent(
                                this,
                                this.getOwnerPlayer(),
                                SlayerFailEvent.SlayerFailReason.TIME_PASSED
                        );
                        Bukkit.getPluginManager().callEvent(failEvent);
                    }
                });
            } else {
                slayerTimer.updateTimer(timeLeft);
            }
        }
        if (this.ownerPlayer == null) return;

        /*
         * Setting the target entity who spawned the boss.
         */
        ((Zombie) this.bukkitEntity).setTarget(this.ownerPlayer.getPlayer());

        /*
         * All the Revenant Horror Bosses starting from 1 has a default ability
         * called "Life Drain".
         * So Instead of adding on each Revenant Horror Boss, I will add it here.
         */
        if (ticks % 60 == 0) {
            double damageDealt = (double) this.skyblockMob.getAttribute(MobAttributes.DAMAGE) * 0.5;
            this.ownerPlayer.damage(damageDealt);
        }

        /*
         * Revenant Horror Bosses starting from Tier 2 has a new ability
         * called "AOE".
         */
        if (ticks % 20 == 0 && this.tier >= 2) {
            double damageDealt = this.skyblockMob.getAttribute(MobAttributes.DAMAGE);
            this.ownerPlayer.damage(damageDealt);
        }
    }

    private boolean valid() {
        return this.skyblockMob.getEntityInstance() != null && !this.skyblockMob.getEntityInstance().isDead();
    }

    @Override
    public SkyblockMob getSkyblockMob() {
        return this.skyblockMob;
    }

    @Override
    public EntityLiving getEntityInstance() {
        return this;
    }

    public abstract void setStats();

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public SkyblockPlayer getOwnerPlayer() {
        return this.ownerPlayer;
    }

    @Override
    public void setOwnerPlayer(SkyblockPlayer skyblockPlayer) {
        this.ownerPlayer = skyblockPlayer;
    }
}
