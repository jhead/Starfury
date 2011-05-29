package net.dv90.starfury.world;

import java.util.HashMap;

public class Tile {
    private static int[] important = {3, 5, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 24, 26, 27, 28, 29, 31, 33, 34, 35, 36, 42, 50, 55, 61, 71, 72, 73, 74, 77, 78, 79};

    public static boolean isImportant(Tile tile) {
        for (int i = 0; i < important.length; i++) {
            if (important[i] == tile.getType().getID()) {
                return true;
            }
        }
        return false;
    }
    public static class TileFlags {

        public static final byte ACTIVE = 1,
                LIGHT = 2,
                WALL = 4,
                LIQUID = 8;
    }
    private Type type;
    private boolean active;
    private short frameX, frameY;
    private byte wall;
    private boolean lava;
    private byte liquid;

    public Tile() {
        this(Type.Air);
    }

    public Tile(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public short getFrameX() {
        return frameX;
    }

    public short getFrameY() {
        return frameY;
    }

    public boolean isLava() {
        return lava;
    }

    public byte getLiquid() {
        return liquid;
    }

    public byte getWall() {
        return wall;
    }

    public void setType(Type type) {
        if (type == null) {
            type = Type.Air;
        }

        this.type = type;
    }

    public enum Type {

        Air(0),
        Dirt(1);
        private static HashMap<Integer, Type> lookupMap = new HashMap<Integer, Type>();

        static {
            for (Type type : Type.values()) {
                lookupMap.put(type.id, type);
            }
        }

        public static Type lookup(int id) {
            return lookupMap.get(id);
        }
        private int id;

        private Type(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }
    }
}
