package com.sweattypalms.skyblock.core.mobs.builder.dragons.loot;

import com.sweattypalms.skyblock.SkyBlock;
import com.sweattypalms.skyblock.core.helpers.PlaceholderFormatter;
import com.sweattypalms.skyblock.core.items.builder.SkyblockItem;
import com.sweattypalms.skyblock.core.player.SkyblockPlayer;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class DragonDropItemEntity extends EntityItem implements IDragonLoot {

    private final SkyblockItem dropItem;
    private final SkyblockPlayer dropOwner;

    private final Location location;
    private final BukkitTask tick;

    private EntityArmorStand nameTag;

    public DragonDropItemEntity(Player owner, Location location, SkyblockItem dropItem) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.setPosition(location.getX(), location.getY(), location.getZ());

        this.setItemStack(CraftItemStack.asNMSCopy(dropItem.toItemStack()));
        this.setCustomNameVisible(false);
        this.setCustomName(("drop-item"));


        this.dropItem = dropItem;
        this.location = location;
        this.dropOwner = SkyblockPlayer.getSkyblockPlayer(owner);

        // send packet to the owner

        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(this, this.getId());
        ((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);

        PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(this.getId(), this.getDataWatcher(), true);
        ((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packetPlayOutEntityMetadata);

        this.spawnNametag();

        this.tick = new BukkitRunnable() {
            @Override
            public void run() {
                DragonDropItemEntity.this.t_();
            }
        }.runTaskTimer(SkyBlock.getInstance(), 0L, 20L);
    }

    private void spawnNametag() {
        ItemStack item = dropItem.toItemStack();
        EntityPlayer entityPlayer = ((CraftPlayer) getDropOwner().getPlayer()).getHandle();
        EntityArmorStand entityArmorStand = new EntityArmorStand(((CraftWorld) getDropOwner().getPlayer().getWorld()).getHandle());
        entityArmorStand.setCustomName((item.getItemMeta().getDisplayName() + ((item.getAmount() > 1) ? " §8" + item.getAmount() + "x" : "")));
        entityArmorStand.setCustomNameVisible(true);

        entityArmorStand.setInvisible(true);
        entityArmorStand.setLocation(location.getX(), location.getY() - 1.1, location.getZ(), 0, 0);

        PacketPlayOutSpawnEntityLiving standPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
        entityPlayer.playerConnection.sendPacket(standPacket);
        PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
        entityPlayer.playerConnection.sendPacket(metaPacket);

        this.nameTag = entityArmorStand;
    }

    @Override
    public SkyblockItem getDropItem() {
        return dropItem;
    }

    @Override
    public SkyblockPlayer getDropOwner() {
        return dropOwner;
    }

    @Override
    public void t_() {
        if (!this.isAlive()) {
            this.tick.cancel();
            return;
        }

        if (this.location.distanceSquared(this.getDropOwner().getPlayer().getLocation()) > 4) return;

        this.getDropOwner().getInventoryManager().addItem(this.dropItem);


        String displayName = this.getDropOwner().getPlayer().getDisplayName();
        String message = PlaceholderFormatter.format(displayName + "$e has obtained " + this.dropItem.f_getDisplayName() + ChatColor.RESET + " $e!");

        this.location.getWorld().getPlayers().forEach(_player -> _player.sendMessage(message));

        this.dead = true;
        this.tick.cancel();

        this.destroyEntity(this.getId());
        this.destroyEntity(this.nameTag.getId());
    }

    private void destroyEntity(int id) {
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(id);
        ((CraftPlayer) this.getDropOwner().getPlayer()).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
    }
}
