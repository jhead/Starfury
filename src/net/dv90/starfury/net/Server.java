package net.dv90.starfury.net;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;

import net.dv90.starfury.logging.*;
import net.dv90.starfury.config.ServerConfig;

public class Server implements Runnable
{

    private ServerConfig config;
    private ServerSocket socket;
    private int bindPort;
    private InetSocketAddress bindAddress;
    private NetworkState state = NetworkState.Closed;

    public Server(ServerConfig config)
    {
        this.config = config;
        this.socket = null;
    }

    public void run()
    {
        state = NetworkState.Starting;
        
        parseBindAddress();
        bindSocket();

        Client client = null;
        Socket clientSocket = null;
        while( state == NetworkState.Running && socket.isBound() )
        {
            try
            {
                clientSocket = socket.accept();
                client = new Client(clientSocket);
            }
            catch(Exception ex)
            {
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
            Logger.log(LogLevel.INFO, "Server bound to " + bindAddress + ".");
            state = NetworkState.Running;
        }
        catch(Exception ex)
        {
            Logger.log(LogLevel.FATAL, "Server cannot bind to " + bindAddress + "." + ex);
            state = NetworkState.Closed;
        }
    }

    private void parseBindAddress()
    {
        String ip = config.getValue("server-ip", "0.0.0.0");
        int port = 31337;

        try
        {
            port = Integer.parseInt(config.getValue("server-port", "31337"));
        }
        catch(Exception ex)
        {
            Logger.log(LogLevel.ERROR, ex.toString());
        }

        bindAddress = new InetSocketAddress(ip, port);
    }

}
