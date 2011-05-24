package net.dv90.starfury.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import net.dv90.starfury.logging.*;

import net.dv90.starfury.util.BitConverter;

public class Client extends Thread {
    
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    private NetworkState state = NetworkState.Closed;

    public Client( Socket socket ) {
            state = NetworkState.Starting;

            this.socket = socket;

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

            while ( state == NetworkState.Running && ! socket.isInputShutdown() ) {
                    try {
                            byte[] header = new byte[ 4 ];
                            if ( in.read( header ) != 4 ) {
                                throw new Exception( "Malformed packet header" );
                            }

                            int length = BitConverter.toInteger( header );
                            length = ( length <= 1024 ? length : 1024 );
                            // Malformed packets cause java to run out of memory if we don't cap the length

                            byte[] data = new byte[ length ];
                            if ( in.read( data ) != length ) {
                                throw new Exception( "Insufficient bytes available to fill packet length" );
                            }

                            // TODO: Whatever is done with the data.

                    } catch ( Exception  e ) {
                            if ( state == NetworkState.Running ) {
                                    e.printStackTrace();

                                    state = NetworkState.Error;
                            }

                            break;
                    }
            }

            disconnect();
            Logger.log(LogLevel.INFO, "Client disconnected [" + toString() + "]");
    }
	
    public void disconnect()
    {
            if ( state != NetworkState.Running && state != NetworkState.Error )
                    return;

            state = NetworkState.Closing;

            try {
                    socket.close();

                    state = NetworkState.Closed;
            } catch ( Exception e ) {
                    e.printStackTrace();

                    state = NetworkState.Error;
            }
    }

    @Override
    public String toString()
    {
        // TODO: Add username if user has logged in.
        return socket.getRemoteSocketAddress().toString();
    }
}
