package net.dv90.starfury.net;

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
    private ArrayList<Client> clients;

    // Config
    private int bindPort;
    private InetSocketAddress bindAddress;
    private String serverPassword = "";

    public Server(ServerConfig config)
    {
        this.config = config;
        this.socket = null;
        this.clients = new ArrayList<Client>();

        setupConfig();
    }

    private void setupConfig()
    {
        String ip = config.getValue("server-ip", "0.0.0.0");
        int port = 7777;

        try
        {
            port = Integer.parseInt(config.getValue("server-port", "7777"));
        } catch (Exception ex) {
            Logger.log(LogLevel.ERROR, ex.toString());
        }

        bindAddress = new InetSocketAddress(ip, port);

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
                client = new Client(this, clientSocket, clients.size() + 1);
                clients.add(client);
            } catch(Exception ex) {
                Logger.log(LogLevel.ERROR, ex.toString());
            }
        }

        config.save();
    }

    private void bindSocket()
    {
        try
        {
            socket = new ServerSocket();
            socket.bind(bindAddress);
            Logger.log(LogLevel.INFO, "Server bound to " + bindAddress.toString().substring(1) + ".");
            state = NetworkState.Running;
        }
        catch(Exception ex)
        {
            Logger.log(LogLevel.FATAL, "Server cannot bind to " + bindAddress.toString().substring(1) + "." + ex);
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

    public void removeClient(Client client)
    {
        clients.remove(client);
    }

    public ArrayList<Client> getClients()
    {
        return new ArrayList<Client>(clients);
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
