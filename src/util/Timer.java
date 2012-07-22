package util;

import java.util.Stack;

/**
 * Currently not Used in SSE !
 * 
 * @author Rui Wang
 *
 */
public class Timer {

    //static long timeBefore = 0, timeAfter = 0;
    static Stack<Long> timeBefore = new Stack<Long>();
    static long timeAfter = 0;

    public static void startTimer() {

        timeBefore.push(new Long(System.currentTimeMillis()));
        System.out.println("Time Before: " + timeBefore);

    }

    public static void endTimer() {

        timeAfter = System.currentTimeMillis();
        long startTime = ((Long) timeBefore.pop()).longValue();
        System.out.println("Time After: " + timeAfter);

        // Compute the difference by converting the milliseconds to seconds
        float timeDiff = (float) (timeAfter - startTime) / 1000;
        System.out.println("Time taken for Execution: " + timeDiff + " seconds");

    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        startTimer();
        System.out.println("xx");
        endTimer();
    }
}
