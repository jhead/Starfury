package net.dv90.starfury.inventory;

import java.util.HashMap;

public enum ItemType {
    Empty( "" );

    private String name;

    private static final HashMap< String, ItemType > lookupMap = new HashMap< String, ItemType >();

    static {
        for ( ItemType type : ItemType.values() ) {
                lookupMap.put( type.name, type );
        }
    }

    private ItemType( String name )
    {
        this.name = name;
    }

    public static ItemType lookup( String name ) {
	return lookupMap.get( name );
    }

    public String getName() {
        return name;
    }

}
