
import net.dv90.starfury.net.Server;
import net.dv90.starfury.config.ServerConfig;
import net.dv90.starfury.logging.*;

public class Main
{
    public static void main(String[] args)
    {
    	System.setErr( Logger.getPrintStream() );
    	
        Logger.log(LogLevel.INFO, "Starting up Starfury...");

        new Server(ServerConfig.load()).run();
    }
}
