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
		
		return tiles.get( coord );
	}

        public Tile[] getTiles() {
            return tiles.values().toArray(new Tile[] { });
        }
	
	public void setTile( Point coord, Tile tile ) {
		if ( coord == null )
			return;
		
		if ( tile == null )
			tile = new Tile( Type.Air );
		
		tiles.put( coord, tile );
	}
}
