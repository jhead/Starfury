package net.dv90.starfury.world;

import java.io.File;

import net.dv90.starfury.logging.*;
import net.dv90.starfury.util.MathUtil;

public class WorldManager {
    
    public static World load(String worldName) throws Exception
    {
        worldName = worldName.trim();
        if( worldName == null || worldName.length() == 0 )
            return null;

        File worldFile = new File("worlds/" + worldName + ".wld");
        new File("worlds/").mkdir();

        if( !worldFile.exists() )
            return WorldManager.generate(worldName);

        // TODO

        Logger.log(LogLevel.INFO, "Loaded world '" + worldName + "'.");
        return null;
    }

    public static World generate(String worldName) throws Exception
    {
        File worldFile = new File("worlds/" + worldName + ".wld");
        worldFile.createNewFile();
        
        // TODO

        Logger.log(LogLevel.INFO, "Generated new world '" + worldName + "'.");
        return null;
    }

}
