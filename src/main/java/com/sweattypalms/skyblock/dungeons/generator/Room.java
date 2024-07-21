package com.sweattypalms.skyblock.dungeons.generator;

import com.sweattypalms.skyblock.api.Point;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Room {

    private final Point coordinates;
    @Setter
    private RoomType type;
    @Setter
    private RoomShape shape;

    @Setter
    private Set<Room> connectedRooms = new HashSet<>();


    public Room(Point coordinates, RoomType type) {
        this.coordinates = coordinates;
        this.type = type;
    }

    public void addConnection(Room room) {
        this.connectedRooms.add(room);
    }
    public void removeConnection(Room room) {
        this.connectedRooms.remove(room);
    }

    @Override
    public String toString() {
        return "Room{" +
                "point=" + coordinates +
                ", type=" + type +
                '}';
    }
}
