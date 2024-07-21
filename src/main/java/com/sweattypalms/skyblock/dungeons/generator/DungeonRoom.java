package com.sweattypalms.skyblock.dungeons.generator;

import com.sweattypalms.skyblock.dungeons.pasting.Schematic;

import java.util.List;

public class DungeonRoom {

    private final List<RelativeDirection> entrances;
    private final Schematic schematic;

    public DungeonRoom(final Schematic schematic, final RelativeDirection... directions) {
        this.schematic = schematic;
        this.entrances = List.of(directions);
    }

    public List<RelativeDirection> getEntrances() {
        return this.entrances;
    }

    public Schematic getSchematic() {
        return this.schematic;
    }

    public enum RelativeDirection
    {
        NORTH,
        EAST,
        SOUTH,
        WEST;
    }
}
