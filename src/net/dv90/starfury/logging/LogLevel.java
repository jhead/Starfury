package net.dv90.starfury.logging;

public enum LogLevel
{
    DEBUG( 0 ),
    INFO( 1 ),
    WARN( 2 ),
    ERROR( 3 ),
    CRITICAL( 4 ),
    FATAL( 5 );
    
    private int level;
    private LogLevel( int level ) {
    	this.level = level;
    }
    
    public int getLevel() {
    	return level;
    }
}
