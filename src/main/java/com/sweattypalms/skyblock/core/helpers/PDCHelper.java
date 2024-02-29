package com.sweattypalms.skyblock.core.helpers;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PDCHelper {

    public static NBTItem getNBTItem(ItemStack item) {
        return new NBTItem(item);
    }

    public static boolean hasInt(ItemStack item, String key) {
        if (!isNotNull(item)) return false;
        NBTItem nbt = getNBTItem(item);
        NBTCompound cp = getDefaultCompound(nbt);
        return cp.getInteger(key) != null;
    }

    public static boolean hasString(ItemStack item, String key) {
        if (!isNotNull(item)) return false;
        NBTItem nbt = getNBTItem(item);
        NBTCompound cp = getDefaultCompound(nbt);
        return item.hasItemMeta() && cp.getString(key) != null;
    }

    public static boolean hasDouble(ItemStack item, String key) {
        if (!isNotNull(item)) return false;
        NBTItem nbt = getNBTItem(item);
        NBTCompound cp = getDefaultCompound(nbt);
        return item.hasItemMeta() && cp.getDouble(key) != null;
    }

    public static Integer getInt(ItemStack item, String key) {
        if (!isNotNull(item)) return 0;
        NBTItem nbt = getNBTItem(item);
        NBTCompound cp = getDefaultCompound(nbt);
        return hasInt(item, key) ? cp.getInteger(key)
                : 0;
    }

    public static String getString(ItemStack item, String key) {
        if (!isNotNull(item)) return "";
        NBTItem nbt = getNBTItem(item);
        NBTCompound cp = getDefaultCompound(nbt);
        return hasString(item, key) ? cp.getString(key) : "";
    }

    public static Double getDouble(ItemStack item, String key) {
        if (!isNotNull(item)) return 0.0;
        NBTItem nbt = getNBTItem(item);
        NBTCompound cp = getDefaultCompound(nbt);
        return hasDouble(item, key) ?
                cp.getDouble(key)
                : 0.0;
    }

    public static <T> void set(ItemStack item, String key, T value) {
        if (isNotNull(item)) return;
        NBTItem nbt = getNBTItem(item);
        NBTCompound cp = getDefaultCompound(nbt);
        if (value instanceof Integer)
            cp.setInteger(key, (int) value);
        else if (value instanceof String)
            cp.setString(key, (String) value);
        else if (value instanceof Double)
            cp.setDouble(key, (double) value);

        ItemStack newItem = nbt.getItem();
        ItemMeta newMeta = newItem.getItemMeta();
        item.setItemMeta(newMeta);
    }

    public static void setInt(ItemStack item, String key, Integer value) {
        NBTItem nbt = getNBTItem(item);
        NBTCompound cp = getDefaultCompound(nbt);
        cp.setInteger(key, value);
        ItemStack newItem = nbt.getItem();
        ItemMeta newMeta = newItem.getItemMeta();
        item.setItemMeta(newMeta);
    }

    public static void setString(ItemStack item, String key, String value) {
        if (isNotNull(item)) return;
        NBTItem nbt = getNBTItem(item);
        NBTCompound cp = getDefaultCompound(nbt);
        cp.setString(key, value);
        ItemStack newItem = nbt.getItem();
        ItemMeta newMeta = newItem.getItemMeta();
        item.setItemMeta(newMeta);
    }

    public static void setDouble(ItemStack item, String key, Double value) {
        if (isNotNull(item)) return;
        NBTItem nbt = getNBTItem(item);
        NBTCompound cp = getDefaultCompound(nbt);
        cp.setDouble(key, value);
        ItemStack newItem = nbt.getItem();
        ItemMeta newMeta = newItem.getItemMeta();
        item.setItemMeta(newMeta);
    }

    public static <T> T getOrDefault(ItemStack item, String key, T def) {
        if (def instanceof Double && hasDouble(item, key))
            return (T) getDouble(item, key);
        if (def instanceof String && hasString(item, key))
            return (T) getString(item, key);
        if (def instanceof Integer && hasInt(item, key))
            return (T) getInt(item, key);

        return def;
    }

    public static NBTCompound getDefaultCompound(NBTItem nbt) {
        return nbt.getOrCreateCompound("ExtraAttributes");
    }

    private static boolean isNotNull(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }
}