package com.sweattypalms.skyblock.core.mobs.builder;

import com.sweattypalms.skyblock.core.helpers.MozangStuff;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityTypes;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MobManager {

    public static Map<String, Class<? extends ISkyblockMob>> MOBS_LIST = new HashMap<>();

    public static void init() {
        Reflections reflections = new Reflections("com.sweattypalms.skyblock");

        for (Class<? extends ISkyblockMob> clazz : reflections.getSubTypesOf(ISkyblockMob.class)) {
            try {
                if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) continue;

                String id = clazz.getDeclaredField("ID").get(null).toString();
                MozangStuff.addToMaps(clazz, id, MozangStuff.getMobID(clazz.getSuperclass()));
                MOBS_LIST.put(id, clazz);
            } catch (Exception e) {
                System.out.println("Failed to load mob: " + clazz.getName());
                System.out.println("Are you missing the `ID` field?");
            }
        }
    }


    public static SkyblockMob getInstance(String id) throws IllegalArgumentException {
        Class<? extends ISkyblockMob> clazz = MOBS_LIST.get(id);
        if (clazz == null) {
            throw new IllegalArgumentException("Unknown mob id: " + id);
        }

        try {
            return new SkyblockMob(id, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown mob id: " + id);
        }
    }
}