package net.dv90.starfury.util;

import java.util.Random;

public class MathUtil {

    private static final Random rand = new Random();

    public static int randomInt() {
        return rand.nextInt();
    }

    public static float randomFloat() {
        return rand.nextFloat();
    }

    public static double randomDouble()
    {
        return rand.nextDouble();
    }

    public static long randomLong() {
        return rand.nextLong();
    }

    public static int clamp( int value, int min, int max ) {
        if( value < min )
            return min;

        if( value > max )
            return max;

        return value;
    }

    public static float clamp( float value, float min, float max ) {
        if( value < min )
            return min;

        if( value > max )
            return max;

        return value;
    }

    public static double clamp( double value, double min, double max ) {
        if( value < min )
            return min;

        if( value > max )
            return max;

        return value;
    }

    public static long clamp( long value, long min, long max ) {
        if( value < min )
            return min;

        if( value > max )
            return max;

        return value;
    }

}
