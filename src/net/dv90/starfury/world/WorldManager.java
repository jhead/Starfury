package net.dv90.starfury.world;

import java.awt.Point;
import java.io.File;

import net.dv90.starfury.logging.*;
import net.dv90.starfury.world.Tile.Type;

public class WorldManager {
    private static File folder = new File( "Worlds" );
	
	static {
    	if ( !folder.exists() )
    		folder.mkdir();
    }
    
    public static World load(String worldName) throws Exception
    {
        worldName = worldName.trim();
        if( worldName == null || worldName.length() == 0 )
            return null;

        File worldFile = new File( folder, worldName + ".wld" );
        
        if( !worldFile.exists() )
            return WorldManager.generate(worldName);

        // TODO

        Logger.log(LogLevel.INFO, "Loaded world '" + worldName + "'.");
        return loadWorldDebugging( worldName );
    }

    public static World generate(String worldName) throws Exception
    {
        //File worldFile = new File("worlds/" + worldName + ".wld");
        //worldFile.createNewFile();
        
        World world = new World( worldName );
        generateDebugWorld( world );
        
        Logger.log(LogLevel.INFO, "Generated new world '" + worldName + "'.");
        return world;
    }
    
    public static void save( World world ) {
    	
    }
    
    /*
     * Debugging purposes.
     */
    
    public static void generateDebugWorld( World world ) {
        int zoneWidth = 250;
        int zoneHeight = 100;
        int count = 0;

    	for ( int zoneX = -3; zoneX < 3; zoneX++ ) {
    		for ( int zoneY = -5; zoneY < 5; zoneY++ ) {
    			Zone zone = new Zone( world, new Point( zoneX, zoneY ) );
    			
    			for ( int x = (zoneX * zoneWidth) - (zoneWidth / 2) ; x < (zoneX * zoneWidth) + (zoneWidth / 2); x++ ) {
    	    		for ( int y = (zoneY * zoneHeight) - (zoneHeight / 2); y < (zoneY * zoneHeight) + (zoneHeight / 2); y++ ) {
    	    			Point point = new Point( x, y );
                                // System.out.println("(" + x + "," + y + ")");
                                
    	    			if ( y <= 20 ) {
    	    				zone.setTile( point, new Tile( Type.Dirt ) );
    	    			}

                                count++;
    	    		}
    	    	}
    			
    			world.setZone( zone.getCoord(), zone );
    		}
    	}
    	
    	Logger.log(LogLevel.INFO, "Created " + count + " tiles.");
    	
    	world.setSpawn( new Point( 30, 23 ) );
    }

    public static void saveWorldDebugging( World world ) {
    	
    }
    
    public static World loadWorldDebugging( String worldName ) {
    	return null;
    }
}
