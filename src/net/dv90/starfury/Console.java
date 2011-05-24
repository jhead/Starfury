package net.dv90.starfury;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;


import net.dv90.starfury.net.Server;
import net.dv90.starfury.config.ServerConfig;
import net.dv90.starfury.logging.*;

public class Console extends Thread
{
	public static void main(String[] args)
    {
    	getConsole().start();
    }
	
	private static Console instance = new Console();
	
	public static Console getConsole() {
		return instance;
	}
	
	private BufferedReader in;
	private Server server;
	
	private Console() {
		printFileContents( new File( "server.splash" ) );
		
		System.setErr( Logger.getPrintStream() );
		
		in = new BufferedReader( new InputStreamReader( System.in ) );
		
		server = new Server( ServerConfig.load() );
		new Thread( server ).start();
	}
	
	public void printFileContents( File file ) {
		if ( file == null || !file.exists() )
			return;
		try {
			FileReader freader = new FileReader( file );
			BufferedReader reader = new BufferedReader( freader );
			
			String line = null;
			while ( ( line = reader.readLine() ) != null ) {
				System.out.println( line );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			String line = null;
			while ( ( line = in.readLine() ) != null ) {
				System.out.println( "Command Support not yet implemented, '" + line + "' is unrecognized" );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
