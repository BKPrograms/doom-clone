// How time will be managed
public class Time {

    // We are working nano seconds so this represents one second:
    public static final long SECOND = 1000000000L;
    private static double delta;

    public static long getTime(){
        return System.nanoTime();
    }

    public static double getDelta(){
        return delta;
    }

    public static void setDelta(double newdelt){
        Time.delta = newdelt;
    }
}
