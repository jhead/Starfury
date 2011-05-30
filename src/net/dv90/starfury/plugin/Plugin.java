package net.dv90.starfury.plugin;

import net.dv90.starfury.net.Server;

public interface Plugin {
    
    public void onEnable();

    public void onDisable();

    public boolean isEnabled();

    public void setEnabled();

    public Server getServer();

}
