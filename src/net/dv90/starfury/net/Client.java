package net.dv90.starfury.net;

import java.awt.Color;
import java.awt.Point;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.dv90.starfury.entity.Player;
import net.dv90.starfury.entity.Player.PlayerColor;
import net.dv90.starfury.entity.Player.PlayerStat;
import net.dv90.starfury.inventory.ItemStack;
import net.dv90.starfury.inventory.ItemType;
import net.dv90.starfury.inventory.PlayerInventory;
import net.dv90.starfury.logging.*;

import net.dv90.starfury.util.BitConverter;
import net.dv90.starfury.util.MathUtil;
import net.dv90.starfury.world.World;

public class Client extends Thread {

    private Server server;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private int clientId;

    private NetworkState state = NetworkState.Closed;
    private boolean authenticated = false;
    private Player player;

    public Client( Server server, Socket socket, int clientId ) {
            state = NetworkState.Starting;

            this.server = server;
            this.socket = socket;
            this.clientId = clientId;

            player = new Player(this);
            authenticated = ( server.usingPassword() ? false : true );

            try {
                    in = socket.getInputStream();
                    out = socket.getOutputStream();

                    start();
            } catch ( Exception e ) {
                    e.printStackTrace();

                    state = NetworkState.Error;
            }

            Logger.log(LogLevel.INFO, "Client connected [" + toString() + "]");
    }

    public Server getServer() {
        return server;
    }

    public Integer getClientID() {
    	return clientId;
    }

    public Player getPlayer() {
        return player;
    }
    
    public void write( byte[] data ) {
            try {
                out.write( data );
                out.flush();
            } catch ( Exception e ) {
                e.printStackTrace();
            }
    }
	
    @Override
    public void run() {
            state = NetworkState.Running;

            Packet packet;
            Protocol proto;
            byte[] header, data;
            int length, id;
            
            while ( state == NetworkState.Running && ! socket.isInputShutdown() ) {
                    try {
                            header = new byte[ 4 ];
                            if ( in.read( header ) != 4 ) {
                            	if ( socket.isClosed() )
                            		break;
                            	
                                throw new Exception( "Malformed packet header" );
                            }

                            length = BitConverter.toInteger( header ) - 1;
                            length = ( length <= 1024 ? length : 1024 );
                            // Malformed packets cause java to run out of memory if we don't cap the length

                            id = in.read();
                            proto = Protocol.lookup( id );
                            
                            if ( proto == null )
                            	throw new Exception( "Unrecognized packet with ID " + id );
                            
                            packet = new Packet( proto );
                            
                            data = new byte[ length ];
                            if ( in.read( data ) != length ) {
                            	if ( socket.isClosed() )
                            		break;
                            	
                                throw new Exception( "Insufficient bytes available to fill packet length" );
                            }
                            
                            packet.append( data );
                            handlePacket( packet );
                    } catch ( Exception  e ) {
                            if ( state == NetworkState.Running ) {
                                    e.printStackTrace();

                                    state = NetworkState.Error;
                            }

                            break;
                    }
            }

            
            disconnect();
            Logger.log(LogLevel.INFO, "Client disconnected [" + this.toString() + "]");
    }
	
    public void disconnect() {
    	disconnect( "Connection closed by server." );
    }
    
    public void disconnect( String reason ) {
            if ( state != NetworkState.Running && state != NetworkState.Error )
                    return;

            server.removeClient(this);
            
            state = NetworkState.Closing;
            
            if ( !socket.isOutputShutdown() ) {
            	Packet packet = new Packet( Protocol.Disconnect );
            	packet.append( reason.getBytes() );
            	
            	write( packet.create() );
            }
            
            try {
                    socket.close();

                    state = NetworkState.Closed;
            } catch ( Exception e ) {
                    e.printStackTrace();

                    state = NetworkState.Error;
            }
    }

    public void handlePacket(Packet packet) throws Exception
    {
        Logger.log(LogLevel.DEBUG, "Received " + packet.getProtocol().toString() + " packet. [" + this.toString() + "]");
        Packet response = null;
        
        if ( !authenticated && packet.getProtocol() != Protocol.ConnectRequest && packet.getProtocol() != Protocol.PasswordResponse ) {
        	disconnect( "Illegal packet received." );
        	
        	return;
        }
        
        int index = 0;
        byte[] data = packet.getData();
        
    	switch( packet.getProtocol() )
        {
            case ConnectRequest:
            	if ( !new String( data ).equals( server.getClientVersion() ) ) {
            		disconnect( "Incorrect client version." );
            		
            		break;
            	}
            	
                if( server.usingPassword() )
                {
                    response = new Packet( Protocol.PasswordRequest );
                } else {
                    response = new Packet( Protocol.RequestPlayerData );
                    response.append( BitConverter.toBytes( clientId ) );
                }
                
                break;
            
            case PlayerData:            	
            	Integer clientID = ( int ) data[ index ];
            	index++;
            	
            	if ( clientID != this.clientId ) {
            		disconnect( "Client ID mismatch." );
            		
            		break;
            	}
            	
            	player.setHairstyle( ( int ) data[ index + 1 ] );
            	index++;
            	
            	for ( PlayerColor part : PlayerColor.values() ) {
            		int r = ( int ) data[ index ];
            		r = MathUtil.clamp(r, 0, 255);
            		
            		int g = ( int ) data[ index + 1 ];
            		g = MathUtil.clamp(g, 0, 255);
            		
            		int b = ( int ) data[ index + 2 ];
            		b = MathUtil.clamp(b, 0, 255);
            		
            		index += 3;
            		
            		Color color = new Color( r, g, b );
            		player.setColor( part, color );
            	}
            	
            	byte[] name = new byte[ data.length - index ];
            	System.arraycopy( data, index, name, 0, name.length );
            	
            	player.setName( new String( name ) );            	
            	break;
            	
            case PlayerHealthUpdate:
            	if ( ( int ) data[ index ] != this.clientId ) {
            		disconnect( "Client ID mismatch." );
            		
            		break;
            	}
            	index++;
            	
            	player.setStat( PlayerStat.Health, BitConverter.toInteger( data, index, 2 ) );
            	index += 2;
            	
            	player.setStat( PlayerStat.MaxHealth, BitConverter.toInteger( data, index, 2 ) );
            	
            	break;
            	
            case PlayerManaUpdate:
            	if ( ( int ) data[ index ] != this.clientId ) {
            		disconnect( "Client ID mismatch." );
            		
            		break;
            	}
            	index++;
            	
            	player.setStat( PlayerStat.Mana, BitConverter.toInteger( data, index, 2 ) );
            	index += 2;
            	
            	player.setStat( PlayerStat.MaxMana, BitConverter.toInteger( data, index, 2 ) );
            	
            	break;
            	
            case InventoryData:
                if ( ( int ) data[ index ] != this.clientId ) {
            		disconnect( "Client ID mismatch." );

            		break;
            	}
                
            	PlayerInventory inv = player.getInventory();

                int slot = data[1];
                int amount = data[2];
                String itemName;
                
                byte[] bn = new byte[data.length - 3];
                System.arraycopy( data, 3, bn, 0, bn.length );
                itemName = new String(bn);

                inv.setSlot( slot, new ItemStack( ItemType.lookup(itemName), amount ) );

            	break;

            case RequestWorldData:
            	response = new Packet( Protocol.WorldData );
            	World world = server.getWorld();
            	
            	response.append( BitConverter.toBytes( ( int ) world.getTime() ) );
            	response.append( BitConverter.toBytes( world.isDay() ? 1 : 0 )[ 0 ] );
            	response.append( BitConverter.toBytes( world.getMoonPhase().getState() )[ 0 ] );
            	response.append( BitConverter.toBytes( world.isBloodmoon() ? 1 : 0 )[ 0 ] );
            	
            	response.append( BitConverter.toBytes( world.getWidth() ) );
            	response.append( BitConverter.toBytes( 60 ) );
            	
            	Point spawn = world.getSpawn();
            	response.append( BitConverter.toBytes( spawn.x ) );
            	response.append( BitConverter.toBytes( spawn.y ) );
            	
            	response.append( BitConverter.toBytes( world.getDirtLayer() ) );
            	response.append( BitConverter.toBytes( world.getRockLayer() ) );
            	
            	response.append( BitConverter.toBytes( 0 ) ); // World ID
            	response.append( world.getWorldName().getBytes() );
            	
                break;
                
            case PasswordResponse:
            	if ( server.usingPassword() ) {
            		String password = new String( packet.getData() );
            		
            		if ( password == null || !password.equals( server.getPassword() ) ) {
            			disconnect( "Incorrect password." );
            		} else {
            			authenticated = true;
            			
            			response = new Packet( Protocol.RequestPlayerData );
            			response.append( BitConverter.toBytes( clientId ) );
            		}
            		
                	break;
            	}
            	
            default:
            	disconnect( "Illegal packet received." );
            	
                break;
        }
        
        if(response != null)
        {
            Logger.log(LogLevel.DEBUG, "Sent " + response.getProtocol().toString() + " packet. [" + this.toString() + "]");
            
            write( response.create() );
        }
    }

    @Override
    public String toString()
    {
    	StringBuffer buffer = new StringBuffer();
    	
    	if ( player.getName() != null )
    		buffer.append( player.getName() + "@" );
    	
    	buffer.append( socket.getRemoteSocketAddress().toString().substring( 1 ) );
    	
        return buffer.toString();
    }
}
