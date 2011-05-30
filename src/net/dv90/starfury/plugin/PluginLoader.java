package net.dv90.starfury.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import net.dv90.starfury.logging.*;

public class PluginLoader {

    public PluginLoader() {

    }

    public Plugin[] loadAll(File dir)
    {
        if( dir.exists() && !dir.isDirectory() )
            return new Plugin[] { };

        dir.mkdir();
        
        ArrayList<Plugin> plugins = new ArrayList<Plugin>();
        for( File pluginFile : dir.listFiles() )
        {
            if( !pluginFile.getName().endsWith(".jar") )
                continue;


        }

        return new Plugin[] { };
    }

    public Plugin load(File pluginFile)
    {
        return null;
    }

}
