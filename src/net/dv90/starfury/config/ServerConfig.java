package net.dv90.starfury.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.dv90.starfury.logging.*;

public class ServerConfig
{

    private Properties props;

    private ServerConfig(Properties props)
    {
        this.props = props;
    }

    public static ServerConfig load()
    {
        File f = new File("server.properties");
        FileInputStream fis = null;
        Properties loadedProps = new Properties();

        try
        {
            // Make sure it exists
            f.createNewFile();

            // Now load it
            fis = new FileInputStream(f);
            loadedProps.load(fis);
            fis.close();
        }
        catch ( Exception ex )
        {
            Logger.log(LogLevel.ERROR, "Unable to load configuration file. " + ex);
        }
        finally
        {
            try
            {
                fis.close();
            }
            catch ( Exception ex )
            {
                Logger.log(LogLevel.ERROR, ex.toString());
            }
        }

        Logger.log(LogLevel.INFO, "Loaded configuration.");
        return new ServerConfig(loadedProps);
    }

    public void save()
    {
        try {
            props.store(new FileOutputStream("server.properties"), "");
        } catch ( IOException ex ) {
            Logger.log(LogLevel.ERROR, "Unable to save configuration file. " + ex);
        }
    }

    public String getValue(String key, String defaultValue)
    {
       return props.getProperty(key, defaultValue);
    }

    public void setValue(String key, Object value)
    {
        props.put(key, value);
    }

}
