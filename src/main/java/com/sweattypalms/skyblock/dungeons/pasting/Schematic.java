package com.sweattypalms.skyblock.dungeons.pasting;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sweattypalms.skyblock.dungeons.generator.RoomShape;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Getter
@Setter
public class Schematic {

    String name;
    File schematicFile;
    RoomShape shape;
    Clipboard clipboard;
    int rotation;

    public Schematic(final String name, final File schematicFile, final RoomShape shape) {
        this.name = name;
        this.schematicFile = schematicFile;
        this.shape = shape;
        if (!schematicFile.exists()) {
            return;
        }
        BukkitWorld bukkitWorld = new BukkitWorld(Bukkit.getWorlds().get(0));
        final ClipboardFormat format = ClipboardFormat.findByFile(schematicFile);
        if (format == null) return;
        try {
            final ClipboardReader reader = format.getReader(new FileInputStream(schematicFile));
            this.clipboard = reader.read(bukkitWorld.getWorldData());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
