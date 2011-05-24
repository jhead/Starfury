package net.dv90.starfury.logging;

import java.io.OutputStream;
import java.io.PrintStream;

public class ErrorLogger extends PrintStream {

	public ErrorLogger( OutputStream out) {
		super(out);
	}
	
	public void print( String msg ) {
		println( msg );
	}
	
	public void println( String msg ) {
		System.out.println( Logger.prepare( LogLevel.ERROR, msg ) );
	}

}
