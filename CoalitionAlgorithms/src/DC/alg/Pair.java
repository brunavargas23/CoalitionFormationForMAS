package DC.alg;

/**
 * An auxiliary class to represent a pair (time, value). This is used to
 * store the interim results of an algorithm. 
 */
public class Pair {
    /**
     * The time in milliseconds
     */
    public long time;
    /**
     * The value of the best CS found at the specified time
     */
    public double value;

    @Override
    public String toString() {
        return "("+time+","+value+")";
    }
}
