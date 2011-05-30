package net.dv90.starfury.world;

import java.awt.Point;
import java.util.HashMap;
import net.dv90.starfury.world.Tile.Type;

public class World {
    
    public static int getSectionX(int x) {
        return x / 200;
    }

    public static int getSectionY(int y) {
        return y / 150;
    }

    // Temporary

    private int maxWidth = 1024;
    private int maxHeight = 1024;
    private int dirtLayer = 40;
    private int rockLayer = 20;
    private boolean dayTime = true;
    private long time = 54001L;
    private MoonPhase phase = MoonPhase.Four;
    private boolean bloodMoon = false;
    private String worldName = null;
    private HashMap<Point, Zone> zones = new HashMap<Point, Zone>();
    private Point spawn;

    public World(String worldName) {
        this.worldName = worldName;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getWidth() {
        return maxWidth;
    }

    public int getHeight() {
        return maxHeight;
    }

    public Zone getZone(Point point) {
        return zones.get(point);
    }

    public void setZone(Point point, Zone zone) {
        if (point == null || zone == null) {
            return;
        }

        zones.put(point, zone);
    }

    public Point getSpawn() {
        return spawn;
    }

    public void setSpawn(Point point) {
        if (point == null) {
            return;
        }

        spawn = point;
    }

    public int getDirtLayer() {
        return dirtLayer;
    }

    public int getRockLayer() {
        return rockLayer;
    }

    public Tile getTile(int x, int y) {
        Tile t;
        
        for( Zone z : zones.values() )
        {
            t = z.getTile(new Point(x, y));
            if( t != null )
                return t;
        }

        return null;
    }

    public boolean isDay() {
        return dayTime;
    }

    public boolean isBloodmoon() {
        return bloodMoon;
    }

    public long getTime() {
        return time;
    }

    public MoonPhase getMoonPhase() {
        return phase;
    }

    public boolean isTileFrameImportant(Type type) {
        // TODO
        return false;
    }

    public enum MoonPhase {

        One(0),
        Two(1),
        Three(2),
        Four(3),
        Five(4),
        Six(5),
        Seven(6),
        Eight(7);
        private static HashMap<Integer, MoonPhase> lookupMap = new HashMap<Integer, World.MoonPhase>();

        static {
            for (MoonPhase phase : MoonPhase.values()) {
                lookupMap.put(phase.state, phase);
            }
        }

        public static MoonPhase getPhase(int value) {
            return lookupMap.get(value);
        }

        public static MoonPhase getNextPhase(MoonPhase phase) {
            if (phase == Eight) {
                return One;
            }

            return getPhase(phase.state + 1);
        }
        private int state;

        private MoonPhase(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }
}
