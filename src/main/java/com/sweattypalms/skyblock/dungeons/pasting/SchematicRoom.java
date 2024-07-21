package com.sweattypalms.skyblock.dungeons.pasting;

import com.sk89q.worldedit.BlockVector;
import com.sweattypalms.skyblock.dungeons.generator.DungeonRoom;
import com.sweattypalms.skyblock.dungeons.generator.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchematicRoom {

    public Schematic schematic;
    public BlockVector schematicLoc;
    public List<DungeonRoom.RelativeDirection> entrances;
    public Map<BlockVector, DungeonRoom.RelativeDirection> doors;
    public Room dungeonRoom;
    boolean isRotated;
    int finalRotation;


    public SchematicRoom(final Schematic schematic, final BlockVector schematicLoc) {
        this(schematic, schematicLoc, new ArrayList<>());
    }

    public SchematicRoom(final Schematic schematic, final BlockVector schematicLoc, final List<DungeonRoom.RelativeDirection> entrances) {
        this.doors = new HashMap<>();
        this.isRotated = false;
        this.schematic = schematic;
        this.schematicLoc = schematicLoc;
        this.entrances = entrances;
    }

    @Override
    public String toString() {
        return "SchematicRoom{dungeonRoom=" + this.dungeonRoom + "schematicLoc=" + this.schematicLoc;
    }

    @Override
    public int hashCode() {
        return (int) (this.schematicLoc.getX() + this.schematicLoc.getY() * 2 + this.schematicLoc.getZ() * 3);
    }
}
