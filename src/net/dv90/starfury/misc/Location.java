package net.dv90.starfury.misc;

public class Location {
    
    private float x = 0f;
    private float y = 0f;
    
    public Location( float x, float y ) {
        this.x = x;
        this.y = y;
    }

    public Location( int x, int y ) {
        this.x = (float)x;
        this.y = (float)y;
    }

    public Location( Location loc ) {
        this.x = loc.getX();
        this.y = loc.getY();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if( !( o instanceof Location ) )
            return false;

        Location loc = (Location)o;

        if( getX() == loc.getX() && getY() == loc.getY() )
            return true;
        else
            return false;
    }

    public float distanceTo(Location loc) {
        return (float)Math.sqrt( Math.pow( loc.getX() - getX(), 2 ) + Math.pow( loc.getY() - getY(), 2 ) );
    }


}
