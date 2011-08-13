package net.dv90.starfury.net;

import java.util.HashMap;

public enum Protocol {

	ConnectRequest( 0x01 ),
	Disconnect( 0x02 ),
	RequestPlayerData( 0x03 ),
	PlayerData( 0x04 ),
	InventoryData( 0x05 ),
	RequestWorldData( 0x06 ),
    WorldData( 0x07 ),
    TileBlockRequest( 0x08 ),
        TileLoading ( 0x09 ),
        TileSection ( 0x0A ),
        TileConfirmed ( 0x0B ),
        Spawn ( 0x0C ),
        PlayerUpdateOne ( 0x0D ),
        PlayerUpdateTwo ( 0x0E ),
        PlayerHealthUpdate( 0x10 ),
        ManipulateTile ( 0x11 ),
        ItemInfo ( 0x15 ),
        ItemOwnerInfo ( 0x16 ),
        NpcInfo ( 0x17 ),
        Message ( 0x19 ),
        Projectile ( 0x1B ),
        PvpMode ( 0x1E ),
        ZoneInfo ( 0x24 ),
        PasswordRequest ( 0x25 ),
        PasswordResponse ( 0x26 ),
        NpcTalk ( 0x28 ),
        PlayerManaUpdate( 0x2A ),
        PvpTeam ( 0x2D ),
        SendSpawn ( 0x31 );
	
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
