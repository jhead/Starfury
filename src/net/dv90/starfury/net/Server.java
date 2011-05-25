package net.dv90.starfury.net;

import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import net.dv90.starfury.misc.Location;
import net.dv90.starfury.logging.*;
import net.dv90.starfury.config.ServerConfig;
import net.dv90.starfury.entity.Player;
import net.dv90.starfury.world.World;
import net.dv90.starfury.world.WorldManager;

public class Server implements Runnable
{

    private ServerConfig config;
    private ServerSocket socket;
    private NetworkState state = NetworkState.Closed;

    private World world;
    private ArrayList< Client > clients;
    private Location spawnLocation;

    // Config
    private int bindPort;
    private InetSocketAddress bindAddress;
    private String serverPassword = "";
    private String clientVersion = "";
    private String worldName = "";
    private Integer maxPlayers;

    public Server(ServerConfig config)
    {
        this.config = config;
        this.socket = null;
        this.clients = new ArrayList< Client >();
        this.spawnLocation = new Location(0, 0);
        this.world = null;

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
        worldName = config.getValue( "world-name", "" ).trim();
        serverPassword = config.getValue("server-password", "").trim();
    }

    private void loadWorld() throws Exception
    {
        if( worldName.length() == 0 || worldName == null )
            throw new Exception("No world specified.");
        else
            this.world = WorldManager.load(worldName);
    }

    public void run()
    {
        try {
            loadWorld();
        } catch( Exception ex ) {
            Logger.log( LogLevel.FATAL, ex.toString() );
            return;
        }
        
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

    public ArrayList<Player> getPlayers()
    {
        ArrayList<Player> players = new ArrayList<Player>();

        for( Client c : clients )
        {
            players.add(c.getPlayer());
        }

        return players;
    }

    public Location getSpawnLocation() {
        return new Location(spawnLocation);
    }

    public void setSpawnLocation( Location loc ) {
        spawnLocation = new Location(loc);
    }
    
    public int getPlayerCount()
    {
        return clients.size();
    }

}
