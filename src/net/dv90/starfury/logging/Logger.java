package net.dv90.starfury.logging;

import java.io.PrintStream;
import java.util.Calendar;

public class Logger
{
    private static LogLevel currentLevel = LogLevel.DEBUG;
    private static ErrorLogger errorLogger = new ErrorLogger( System.out );
    private static Calendar cal;

    public static String getTimestamp()
    {
        cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
    }
    
    public static void log( LogLevel level, String msg ) {
    	if ( level.getLevel() >= currentLevel.getLevel() )
    		System.out.println( Logger.getTimestamp() + "[" + level + "] " + msg );
    }
    
    public static void setLevel( LogLevel level ) {
    	if ( level == null )
    		return;
    	
    	currentLevel = level;
    }
    
    public static String prepare( LogLevel level, String msg ) {
    	return Logger.getTimestamp() + "[" + level + "] " + msg;
    }
    
    public static PrintStream getPrintStream() {
    	return errorLogger;
    }

}
