package net.dv90.starfury.plugin;

import net.dv90.starfury.net.Server;

@SuppressWarnings("unused")
public abstract class BasePlugin implements Plugin {
    
	private String pluginName = "";
    private String pluginAuthor = "";
    private String pluginVersion = "";
    
    private boolean enabled = false;
    private Server server = null;
    private PluginLoader loader = null;

    public BasePlugin() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled() {
        enabled = !enabled;

        if(enabled)
            onEnable();
        else
            onDisable();
    }

    public Server getServer() {
        return server;
    }

}
