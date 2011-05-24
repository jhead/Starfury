package net.dv90.starfury.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.dv90.starfury.util.BitConverter;

public class Packet {
	private Protocol protocol;
	private byte[] buffer = new byte[ 0 ];
	private int size = 0;
	
	public Packet( Protocol protocol ) {
		this.protocol = protocol;
	}
	
	public Protocol getProtocol() {
		return protocol;
	}
	
	public void setCapacity( int capacity ) {
		if ( capacity < 0 )
			return;
		
		byte[] newBuffer = new byte[ capacity ];

                System.arraycopy( buffer, 0, newBuffer, 0, ( buffer.length > capacity ? capacity : buffer.length ) );

		buffer = newBuffer;
	}
	
	public void append( byte[] data ) {
		if ( data.length + size > buffer.length )
			setCapacity( data.length + size );
		
		System.arraycopy( data, 0, buffer, size, data.length );
		size += data.length;
	}
	
	public void append( byte data ) {
		append( new byte[] { data } );
	}
	
	public byte[] getData() {
		if ( size > buffer.length )
			setCapacity( size );
		
		return buffer;
	}
	
	public byte[] create() {
		if ( buffer.length > size )
			setCapacity( size );
		
		byte[] data = new byte[ size + 5 ];

		System.arraycopy( BitConverter.toBytes( size + 1 ), 0, data, 0, 4 );
		System.arraycopy( BitConverter.toBytes( protocol.getID() ), 0, data, 4, 1 );
		System.arraycopy( ByteBuffer.wrap( buffer ).order( ByteOrder.LITTLE_ENDIAN ).array(), 0, data, 5, buffer.length );
		
		return data;
	}
}
