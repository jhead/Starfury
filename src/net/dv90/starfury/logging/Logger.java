package net.dv90.starfury.logging;

import java.util.Calendar;

public class Logger
{
	
    private static Calendar cal;

    public static String getTimestamp()
    {
        cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
    }

    public static void log(LogLevel level, String msg)
    {
        System.out.println(Logger.getTimestamp() + " [" + level + "] " + msg);
    }

}
