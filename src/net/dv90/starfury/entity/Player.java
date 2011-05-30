package net.dv90.starfury.entity;

import java.awt.Color;
import java.util.HashMap;

import net.dv90.starfury.net.Client;
import net.dv90.starfury.net.Server;
import net.dv90.starfury.misc.Location;
import net.dv90.starfury.inventory.PlayerInventory;

public class Player extends Entity {
    
    private Client client;
    private String name = null;
    private HashMap< PlayerColor, Color > colors = new HashMap< Player.PlayerColor, Color >();
    private HashMap< PlayerStat, Integer > stats = new HashMap< PlayerStat, Integer >();
    private PlayerInventory inventory;
    private Location location;
    private int hairStyle = 0;
    private boolean pvpEnabled = false;

    public Player( Client client ) {
            this.client = client;
            this.inventory = new PlayerInventory();
            this.location = new Location( client.getServer().getSpawnLocation() );
    }

    public Client getClient() {
            return client;
    }

    public Server getServer() {
        return client.getServer();
    }

    public String getName() {
            return name;
    }
    
    public boolean getPvpState()
    {
        return pvpEnabled;
    }
    
    public void setPvpState(boolean state)
    {
        pvpEnabled = state;
    }

    public void setName( String name ) {
            this.name = name;
    }

    public Color getColor( PlayerColor part ) {
            if ( part == null )
                    return null;

            return colors.get( part );
    }

    public void setColor( PlayerColor part, Color color ) {
            if ( part == null || color == null )
                    return;

            colors.put( part, color );
    }

    public Integer getStat( PlayerStat stat ) {
            if ( stat == null )
                    return null;

            return stats.get( stat );
    }

    public void setStat( PlayerStat stat, Integer value ) {
            if ( stat == null || value == null )
                    return;

            stats.put( stat, value );
    }

    public Integer getHairstyle() {
            return hairStyle;
    }

    public void setHairstyle( Integer value ) {
            hairStyle = value;
    }

    public PlayerInventory getInventory()
    {
        return inventory;
    }
    
    public Location getLocation()
    {
        return location;
    }
    
    public void setLocation( Location loc )
    {
        location = loc;
    }

    public enum PlayerColor {
            Hair,
            Skin,
            Eye,
            Shirt,
            Undershirt,
            Pants,
            Shoe
    }

    public enum PlayerStat {
            Health,
            MaxHealth,
            Mana,
            MaxMana
    }
}
