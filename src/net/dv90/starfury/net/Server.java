package net.dv90.starfury.net;

import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import net.dv90.starfury.logging.*;
import net.dv90.starfury.config.ServerConfig;

public class Server implements Runnable
{

    private ServerConfig config;
    private ServerSocket socket;
    private NetworkState state = NetworkState.Closed;
    private ArrayList< Client > clients;

    // Config
    private int bindPort;
    private InetSocketAddress bindAddress;
    private String serverPassword = "";
    private String clientVersion = "";
    private Integer maxPlayers;

    public Server(ServerConfig config)
    {
        this.config = config;
        this.socket = null;
        this.clients = new ArrayList< Client >();

        setupConfig();
    }

    private void setupConfig()
    {
        String ip = config.getValue( "server-ip", "127.0.0.1" );
        bindPort = (int) config.getValue( "server-port", 7777 );
        bindAddress = new InetSocketAddress( ip, bindPort );
        
        Logger.setLevel( LogLevel.valueOf( config.getValue( "log-level", "DEBUG" ) ) );

        maxPlayers = (int) config.getValue( "max-players", 8 );
        clientVersion = "Terraria" + config.getValue( "client-version", "0" ).trim();
        
        serverPassword = config.getValue("server-password", "").trim();
    }

    public void run()
    {
        state = NetworkState.Starting;
        
        bindSocket();

        Client client = null;
        Socket clientSocket = null;
        while( state == NetworkState.Running && socket.isBound() )
        {
            try
            {
                clientSocket = socket.accept();
                
                Integer clientID = getNextClientID();
                
                if ( clientID == null ) {
                    if ( !clientSocket.isOutputShutdown() ) {
                            Packet packet = new Packet( Protocol.Disconnect );
                            packet.append( "Server is full.".getBytes() );

                            OutputStream os = clientSocket.getOutputStream();
                            os.write( packet.create() );
                            os.flush();
                    }

                    clientSocket.close();
                } else {
                    client = new Client(this, clientSocket, clientID );
                    
                    clients.add( client );
                }
            } catch(Exception ex) {
            	if ( state == NetworkState.Running )
            		ex.printStackTrace();
            	
                state = NetworkState.Error;
            }
        }

        config.save();
    }

    private void bindSocket()
    {
        try
        {
            socket = new ServerSocket();
            socket.bind(bindAddress, bindPort );
            Logger.log(LogLevel.INFO, "Server bound to " + bindAddress.toString().substring( 1 ) + ".");
            state = NetworkState.Running;
        }
        catch(Exception ex)
        {
            Logger.log(LogLevel.FATAL, "Server cannot bind to " + bindAddress.toString().substring( 1 ) + ".\n" + ex);
            state = NetworkState.Closed;
        }
    }

    public String getPassword()
    {
        return serverPassword;
    }

    public boolean usingPassword()
    {
        return ( serverPassword.length() > 0 );
    }
    
    public String getClientVersion() {
    	return clientVersion;
    }

    public void removeClient( Client client )
    {
        clients.remove(client);
    }

    public ArrayList< Client > getClients()
    {
        return new ArrayList< Client >( clients );
    }
    
    public Integer getNextClientID() {
    	if ( clients.size() >= maxPlayers )
    		return null;
    	
Index: for ( int i = 0; i < maxPlayers; i++ ) {
    		for ( Client client : getClients() ) {
    			if ( client.getClientID() == i )
    				continue Index;
    		}
    		
    		return i;
    	}
    	
    	return null;
    }

    /*
    public ArrayList<Player> getPlayers()
    {
        
    }
    */
    
    public int getPlayerCount()
    {
        return clients.size();
    }

}
