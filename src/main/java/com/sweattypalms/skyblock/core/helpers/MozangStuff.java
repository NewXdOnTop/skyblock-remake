package com.sweattypalms.skyblock.core.helpers;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.sweattypalms.skyblock.core.helpers.nms.Reflections;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class MozangStuff {

    public static ItemStack getHeadItemStack(String texture) {
        UUID uuid = UUID.randomUUID();
        GameProfile gameProfile = new GameProfile(uuid, null);
        gameProfile.getProperties().put("textures", new Property("textures", texture));
        ItemStack item = new ItemStack(XMaterial.SKELETON_WALL_SKULL.parseMaterial(), 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
        skullMeta.spigot().setUnbreakable(true);
        Arrays.stream(ItemFlag.values()).toList().forEach(skullMeta::addItemFlags);
        item.setItemMeta(skullMeta);
        return item;
    }

    public static void setAI(Entity bukkitEntity, boolean ai) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        nmsEntity.c(tag);
        tag.setInt("NoAI", (ai ? 0 : 1));
        nmsEntity.f(tag);
    }

    public static void noHit(Entity bukkitEntity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        nmsEntity.c(tag);
        tag.setInt("Invulnerable", 1);
        nmsEntity.f(tag);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void addToMaps(Class clazz, String name, int id) {
      //  registerEntity(name, id, clazz);
        ((Map) getPrivateField("c", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(name, clazz);
        ((Map) getPrivateField("d", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(clazz, name);
        ((Map) getPrivateField("e", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(id, clazz);
        ((Map) getPrivateField("f", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(clazz, id);
        ((Map) getPrivateField("g", net.minecraft.server.v1_8_R3.EntityTypes.class, null)).put(name, id);
    }

    private static Object getPrivateField(String fieldName, @SuppressWarnings("rawtypes") Class clazz, Object object) {
        Field field;
        Object o = null;

        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return o;
    }

    @SuppressWarnings("rawtypes")
    public static int getMobID(Class<?> clazz) {

        Class<?> entityTypes = Reflections.getNMSClass("entity", "EntityTypes");
        if (entityTypes == null) return 0;
        Integer var1 = (Integer) ((Map) getPrivateField("f", entityTypes, null)).get(clazz);
        return var1 == null ? 0 : var1;
    }

    private static void registerEntity(String name, int id, Class<?> clazz) {
        try {
            List<Map<?, ?>> dataMap = new ArrayList<>();
            Class<?> entityTypes = Reflections.getNMSClass("entity", "EntityTypes");
            for (Field f : entityTypes.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMap.add((Map<?, ?>) f.get(null));
                }
            }
            if (dataMap.get(2).containsKey(id)) {
                dataMap.get(0).remove(name);
                dataMap.get(2).remove(id);
            }
            Method method = entityTypes.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, clazz, name, id);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
