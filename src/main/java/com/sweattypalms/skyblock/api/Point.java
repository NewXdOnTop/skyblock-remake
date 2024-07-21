package com.sweattypalms.skyblock.api;

import lombok.Getter;

@Getter
public class Point {
    int x;
    int y;

    public Point(int x, int z) {
        this.x = x;
        this.y = z;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Point point && x == point.x && y == point.y;
    }

    @Override
    public String toString() {
        return "(" + y + ", " + x + ")";
    }

    public Point add(Point nextPoint) {
        return new Point(x + nextPoint.x, y + nextPoint.y);
    }

    public double distance(Point other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }
}