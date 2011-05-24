package net.dv90.starfury.net;

import java.util.HashMap;

public enum Protocol {
	ConnectRequest( 1 ),
	Disconnect( 2 ),
	RequestPlayerData( 3 ),
	PlayerData( 4 ),
	InventoryData( 5 ),
	RequestWorldData( 6 ),
    WorldData( 7 ),
    SetSpawn( 8 ),
    PlayerHealthUpdate( 16 ),
    PasswordRequest ( 37 ),
    PasswordResponse ( 38 ),
    PlayerManaUpdate( 42 );
	
	private static final HashMap< Integer, Protocol > lookupMap = new HashMap<Integer, Protocol>();
	
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
