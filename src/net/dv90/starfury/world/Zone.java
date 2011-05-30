package net.dv90.starfury.world;

import java.awt.Point;
import java.util.HashMap;

import net.dv90.starfury.world.Tile.Type;

public class Zone {
	private HashMap< Point, Tile > tiles = new HashMap< Point, Tile >();
	private World world;
	private Point coord;
	
	
	public Zone( World world, Point coord ) {
		this.coord = coord;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Point getCoord() {
		return coord;
	}
	
	public Tile getTile( Point coord ) {
		if ( coord == null )
			return null;

                // System.out.println(coord.getX() + " " + coord.getY());
                return tiles.get(coord);

                // System.out.println(coord.getX() + " " + coord.getY());
                /*
		for( Point p : tiles.keySet() )
                {
                    // System.out.println("(" + p.getX() + "," + p.getY() + ")");
                    if( coord.equals(p) ) //Math.round(p.getX()) == Math.round(coord.getX()) && Math.round(p.getY()) == Math.round(coord.getY()) )
                        return tiles.get(p);
                }*/

                // return null;
	}

        public HashMap< Point, Tile > getTiles() {
            return new HashMap< Point, Tile >( tiles );
        }
	
	public void setTile( Point coord, Tile tile ) {
		if ( coord == null )
			return;
		
		if ( tile == null )
			tile = new Tile( Type.Air );
		
		tiles.put( coord, tile );
	}
}
