package net.dv90.starfury.net;

import java.util.HashMap;

public enum Protocol {
	Connect( 1 ),
	Disconnect( 2 ),
	RequestPlayerData( 3 ),
	PlayerData( 4 ),
	InventoryData( 5 ),
	RequestWorldData( 6 );
	
	private static HashMap< Integer, Protocol > lookupMap = new HashMap<Integer, Protocol>();
	
	static {
		for ( Protocol proto : Protocol.values() ) {
			lookupMap.put( proto.id, proto );
		}
	}
	
	public static Protocol lookup( int id ) {
		return lookupMap.get( id );
	}
	
	private int id;
	
	private Protocol( int id ) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
}
