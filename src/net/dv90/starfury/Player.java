package net.dv90.starfury;

import java.awt.Color;
import java.util.HashMap;

import net.dv90.starfury.net.Client;

public class Player {
	private Client client;
	private String name = "Unnamed";
	private HashMap< PlayerColor, Color > colors = new HashMap< Player.PlayerColor, Color >();
	private HashMap< PlayerStat, Integer > stats = new HashMap< PlayerStat, Integer >();
	private int hairStyle = 0;
	
	
	public Player( Client client ) {
		this.client = client;
	}
	
	public Client getClient() {
		return client;
	}
	
	public String getName() {
		return name;
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
