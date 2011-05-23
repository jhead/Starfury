package net.dv90.starfury.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BitConverter {
	/*
	 * Integer
	 */
	
	public static byte[] toBytes( Integer value ) {
		return ByteBuffer.allocate( 4 ).putInt( value ).array();
	}
	
	public static Integer toInteger( byte[] data ) {
		return ByteBuffer.wrap( data ).order( ByteOrder.LITTLE_ENDIAN ).getInt();
	}
	
	/*
	 * Float
	 */
	
	public static byte[] toBytes( Float value ) {
		return ByteBuffer.allocate( 4 ).putFloat( value ).array();
	}
	
	public static Float toFloat( byte[] data ) {
		return ByteBuffer.wrap( data ).order( ByteOrder.LITTLE_ENDIAN ).getFloat();
	}
}
