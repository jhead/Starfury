package net.dv90.starfury.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BitConverter {
	/*
	 * Integer
	 */
	
	public static byte[] toBytes( Integer value ) {
		return ByteBuffer.allocate( 4 ).order( ByteOrder.LITTLE_ENDIAN ).putInt( value ).array();
	}
	
	public static Integer toInteger( byte[] data ) {
		return ByteBuffer.wrap( data ).order( ByteOrder.LITTLE_ENDIAN ).getInt();
	}
	
	public static Integer toInteger( byte[] data, int start, int length ) {
		if ( length != 4 )
			return null;
		
		byte[] copy = new byte[ length ];
		
		System.arraycopy( data, start, copy, 0, length );
		
		return toInteger( copy );
	}
	
	/*
	 * Float
	 */
	
	public static byte[] toBytes( Float value ) {
		return ByteBuffer.allocate( 4 ).order( ByteOrder.LITTLE_ENDIAN).putFloat( value ).array();
	}
	
	public static Float toFloat( byte[] data ) {
		return ByteBuffer.wrap( data ).order( ByteOrder.LITTLE_ENDIAN ).getFloat();
	}
	
	/*
	 * String
	 */
	public static String toHexString( byte[] data ) {		
		StringBuffer buffer = new StringBuffer();
		
		for ( byte byt : data ) {
			String hex = Integer.toHexString( ( int ) byt );
			
			if ( hex.length() == 1 )
				hex = "0" + hex;
			
			buffer.append( " " + hex );
		}
		
		return buffer.toString().trim().toUpperCase();
	}
	
	public static String toString( byte[] data ) {
		StringBuffer buffer = new StringBuffer();
		
		for ( byte byt : data ) {
			buffer.append( " " + Integer.toString( ( int ) byt ) );
		}
		
		return buffer.toString().trim();
	}
}
