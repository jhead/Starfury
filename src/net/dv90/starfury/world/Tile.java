package net.dv90.starfury.world;

import java.util.HashMap;

public class Tile {
	private Type type;
	
	public Tile() {
		this( Type.Air );
	}
	
	public Tile( Type type ) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType( Type type ) {
		if ( type == null )
			type = Type.Air;
		
		this.type = type;
	}
	
	public enum Type {
		Air( 0 ),
		Dirt( 1 );
		
		private static HashMap< Integer, Type > lookupMap = new HashMap< Integer, Type >();
		
		static {
			for ( Type type : Type.values() ) {
				lookupMap.put( type.id, type );
			}
		}
		
		public static Type lookup( int id ) {
			return lookupMap.get( id );
		}
		
		private int id;
		private Type( int id ) {
			this.id = id;
		}
		
		public int getID() {
			return id;
		}
	}
}
