package com.sweattypalms.skyblock.core.events.listeners;

import com.sweattypalms.skyblock.SkyBlock;
import com.sweattypalms.skyblock.api.sequence.Sequence;
import com.sweattypalms.skyblock.api.sequence.SequenceAction;
import com.sweattypalms.skyblock.core.enchants.EnchantManager;
import com.sweattypalms.skyblock.core.events.def.SkyblockDeathEvent;
import com.sweattypalms.skyblock.core.events.def.SkyblockInteractEvent;
import com.sweattypalms.skyblock.core.events.def.SkyblockMobDamagePlayerEvent;
import com.sweattypalms.skyblock.core.events.def.SkyblockPlayerDamageEntityEvent;
import com.sweattypalms.skyblock.core.helpers.PlaceholderFormatter;
import com.sweattypalms.skyblock.core.items.builder.SkyblockItem;
import com.sweattypalms.skyblock.core.items.builder.SkyblockItemType;
import com.sweattypalms.skyblock.core.items.builder.abilities.TriggerType;
import com.sweattypalms.skyblock.core.items.builder.armor.IHeadHelmet;
import com.sweattypalms.skyblock.core.items.builder.item.IShortBow;
import com.sweattypalms.skyblock.core.mobs.builder.ISkyblockMob;
import com.sweattypalms.skyblock.core.mobs.builder.dragons.DragonManager;
import com.sweattypalms.skyblock.core.mobs.builder.dragons.IEndDragon;
import com.sweattypalms.skyblock.core.mobs.builder.dragons.loot.IDragonLoot;
import com.sweattypalms.skyblock.core.player.SkyblockPlayer;
import com.sweattypalms.skyblock.core.player.sub.stats.Stats;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragonPart;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

public class UtilityListener implements Listener {

    @NotNull
    private static SkyblockDeathEvent getSkyblockDeathEvent(LivingEntity livingEntity) {
        SkyblockDeathEvent.DeathCause reason;
        EntityDamageEvent lastDamageCause = livingEntity.getLastDamageCause();
        if (lastDamageCause == null) {
            reason = SkyblockDeathEvent.DeathCause.OTHER;
        } else {
            reason = SkyblockDeathEvent.DeathCause.getCause(lastDamageCause.getCause());
        }
        SkyblockDeathEvent _event;
        if (reason == SkyblockDeathEvent.DeathCause.ENTITY) {
            _event = new SkyblockDeathEvent(livingEntity.getKiller(), livingEntity);
        } else {
            _event = new SkyblockDeathEvent(livingEntity, reason);
        }
        return _event;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String message = "$eWelcome to $cSkyblock$e!";
        message = PlaceholderFormatter.format(message);
        event.getPlayer().sendMessage(message);

        event.setJoinMessage(null);

        if (SkyblockPlayer.getSkyblockPlayer(event.getPlayer()) != null) return;
        new SkyblockPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void interactEventForwarder(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getSkyblockPlayer(player);
        TriggerType triggerType = TriggerType.getTriggerType(event.getAction());
        if (triggerType == null || triggerType == TriggerType.NONE) return;

        SkyblockInteractEvent skyblockInteractEvent = new SkyblockInteractEvent(skyblockPlayer, triggerType);

        if (event.getClickedBlock() != null) {
            skyblockInteractEvent.setInteractedBlock(event.getClickedBlock());
        }


        SkyBlock.getInstance().getServer().getPluginManager().callEvent(skyblockInteractEvent);

        if (skyblockInteractEvent.isCancelled()) event.setCancelled(true);
    }

    @EventHandler
    public void equipHelmetThroughEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInHand();
        SkyblockItem skyblockItem = SkyblockItem.fromItemStack(itemInHand);

        if (skyblockItem == null) return;

        if (skyblockItem.getItemType() != SkyblockItemType.HELMET || !(skyblockItem instanceof IHeadHelmet)) return;

        if (player.getInventory().getHelmet() == null || player.getInventory().getHelmet().getType() == Material.AIR) {
            player.getInventory().setHelmet(itemInHand);
            player.getInventory().setItemInHand(null);
        }
    }

    /**
     * For forwarding the EntityDeathEvent to the SkyblockDeathEvent
     *
     * @param event EntityDeathEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onDeathBukkit(EntityDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        event.setDroppedExp(0);
        event.getDrops().clear();
        if (livingEntity instanceof Player player) {
            SkyblockDeathEvent _event = new SkyblockDeathEvent(player, SkyblockDeathEvent.DeathCause.OTHER);
            Bukkit.getPluginManager().callEvent(_event);
            return;
        }

        EntityLiving entityLiving = ((CraftLivingEntity) event.getEntity()).getHandle();
        if (!(entityLiving instanceof ISkyblockMob skyblockMob)) return;

        SkyblockDeathEvent _event = getSkyblockDeathEvent(livingEntity);

        SkyBlock.getInstance().getServer().getPluginManager().callEvent(_event);
    }

    @EventHandler
    public void playerDeathBukkit(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.setDroppedExp(0);
        new Sequence().add(
                new SequenceAction(() -> {
                    event.getEntity().spigot().respawn();
                    event.getEntity().updateInventory();
                }, 1)
        ).start();
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBowPull(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getSkyblockPlayer(player);

        double bowPull = event.getForce();
        skyblockPlayer.getStatsManager().setMaxStat(Stats.BOW_PULL, bowPull);

        if (skyblockPlayer.getInventoryManager().getSkyblockItemInHand() instanceof IShortBow) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void projectileHitEvent(ProjectileHitEvent event) {
        List<LivingEntity> nearby = event.getEntity().getNearbyEntities(1, 1, 1).stream().map(entity -> entity instanceof LivingEntity ? (LivingEntity) entity : null).filter(entity -> !(entity instanceof ArmorStand)).toList();
        if (nearby.size() == 0) return;
        LivingEntity en = nearby.get(0);
        if (event.getEntity() instanceof FallingBlock || en instanceof FallingBlock) return;
        if (event.getEntity().getShooter() == null) return;

        LivingEntity shooter = (LivingEntity) event.getEntity().getShooter();

        if (shooter instanceof Player && en instanceof Player) {
            return;
        }

        EntityLiving entityLiving = ((CraftLivingEntity) shooter).getHandle();
        if (entityLiving instanceof ISkyblockMob skyblockMob && en instanceof Player player) {
            SkyblockMobDamagePlayerEvent skyblockMobDamagePlayerEvent = new SkyblockMobDamagePlayerEvent(
                    player,
                    shooter
            );
            Bukkit.getPluginManager().callEvent(skyblockMobDamagePlayerEvent);
            return;
        }

        LivingEntity hitEntity = en instanceof EnderDragonPart ? ((EnderDragonPart) en).getParent() : (LivingEntity) en;
        if (hitEntity == null) return;
        if (shooter instanceof Player player && ((CraftLivingEntity) hitEntity).getHandle() instanceof ISkyblockMob skyblockMob) {
            SkyblockPlayerDamageEntityEvent skyblockPlayerDamageEntityEvent = new SkyblockPlayerDamageEntityEvent(
                    hitEntity,
                    player,
                    SkyblockPlayerDamageEntityEvent.DamageType.ARROW
            );
            Bukkit.getPluginManager().callEvent(skyblockPlayerDamageEntityEvent);
            return;
        }


    }

    @EventHandler
    public void pickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();

        if (((CraftItem) event.getItem()).getHandle() instanceof IDragonLoot dragonLoot) {
            player.sendMessage(ChatColor.RED + "You cannot pick up this item! (owner: " + dragonLoot.getDropOwner().getPlayer().getName() + ")");
            event.setCancelled(true);
        }

        if (itemStack.getType() == Material.ARROW) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void endermanTeleport(EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof Enderman)) return;

        event.setCancelled(true);
    }



    /* -------------------- WORLD MANAGEMENT -------------------- */

    @EventHandler
    public void placeBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event) {
        if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
        EnchantManager.run(event.getPlayer(), event);
    }

    @EventHandler
    public void onPhysicalInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFallingBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            event.setCancelled(true);
            event.getEntity().remove();
            event.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void enderDragonDamage(SkyblockPlayerDamageEntityEvent event) {
        if (event.getSkyblockMob() == null) return;
        if (event.getSkyblockMob().getEntityInstance() == null) return;
        if (!(((CraftLivingEntity) event.getSkyblockMob().getEntityInstance()).getHandle() instanceof IEndDragon enderDragon))
            return;

        DragonManager.getInstance().addPlayerDamage(event.getPlayer().getUniqueId(), event.getDamage());
    }

}


