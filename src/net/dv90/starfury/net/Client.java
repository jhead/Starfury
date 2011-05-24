package net.dv90.starfury.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import net.dv90.starfury.logging.*;

import net.dv90.starfury.util.BitConverter;

public class Client extends Thread {

    private Server server;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private int clientId;

    private NetworkState state = NetworkState.Closed;

    public Client( Server server, Socket socket, int clientId ) {
            state = NetworkState.Starting;

            this.server = server;
            this.socket = socket;
            this.clientId = clientId;

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

    public Integer getClientID() {
    	return clientId;
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

                            data = new byte[ length ];
                            if ( in.read( data ) != length ) {
                            	if ( socket.isClosed() )
                            		break;
                            	
                                throw new Exception( "Insufficient bytes available to fill packet length" );
                            }

                            // TODO: Whatever is done with the data.
                            proto = Protocol.lookup(id);
                            
                            if ( proto == null )
                            	throw new Exception( "Unrecognized packet with ID " + id );
                            
                            packet = new Packet(proto);
                            packet.append( data );
                            
                            handlePacket(packet);

                    } catch ( Exception  e ) {
                            if ( state == NetworkState.Running ) {
                                    e.printStackTrace();

                                    state = NetworkState.Error;
                            }

                            break;
                    }
            }

            server.removeClient(this);
            disconnect();
            Logger.log(LogLevel.INFO, "Client disconnected [" + this.toString() + "]");
    }
	
    public void disconnect() {
    	disconnect( "Connection closed by server." );
    }
    
    public void disconnect( String reason )
    {
            if ( state != NetworkState.Running && state != NetworkState.Error )
                    return;

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

    // I doubt this is how we want to go about doing this, I'm just doing this for the sake of testing
    // These packets should work but I think the byte order is wrong.
    public void handlePacket(Packet packet) throws Exception
    {
        Logger.log(LogLevel.DEBUG, "Received " + packet.getProtocol().toString() + " packet. [" + this.toString() + "]");
        Packet response = null;
        
        switch( packet.getProtocol() )
        {
        	// Received when a client first connects
            case ConnectRequest:
            	if ( !new String( packet.getData() ).equals( server.getClientVersion() ) ) {
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
                
            case PasswordResponse:
            	if ( server.usingPassword() ) {
            		String password = new String( packet.getData() );
            		
            		if ( password == null || !password.equals( server.getPassword() ) ) {
            			disconnect( "Incorrect password." );
            		} else {
            			response = new Packet( Protocol.RequestPlayerData );
            			response.append( BitConverter.toBytes( clientId ) );
            		}
            	}
            	break;
            	
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
        // TODO: Add username if user has logged in.
        return socket.getRemoteSocketAddress().toString();
    }
}
