package net.dv90.starfury.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import net.dv90.starfury.logging.*;

public class ServerConfig
{

    private Properties Props;

    private ServerConfig(Properties props)
    {
        Props = props;
    }

    public static ServerConfig load()
    {
        FileInputStream fis = null;
        Properties loadedProps = new Properties();

        try {
            fis = new FileInputStream("server.properties");
            loadedProps.load(fis);
            fis.close();
        } catch (Exception ex) {
            Logger.log(LogLevel.ERROR, "Unable to load configuration file." + ex);
        } finally {
            try {
                fis.close();
            } catch (Exception ex) { }
        }

        return new ServerConfig(loadedProps);
    }

    public void save()
    {
        try {
            Props.store(new FileOutputStream("server.properties"), "");
        } catch (IOException ex) {
            Logger.log(LogLevel.ERROR, "Unable to save configuration file. " + ex);
        }
    }

    public Object getValue(String key)
    {
        return Props.get(key);
    }

    public void setValue(String key, Object value)
    {
        Props.put(key, value);
    }

}
