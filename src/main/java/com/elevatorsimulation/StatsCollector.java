package com.elevatorsimulation;

import java.util.LongSummaryStatistics;
import java.time.Duration;
import java.time.Instant;

/**
 * StatsCollector is a wrapper for the LongSummaryStatistics class.
 * StatsCollector enables collecting time statistics on completed Request
 * objects, specifically how long a Request took to go from creation to being
 * satisfied. It also provides a pretty format for outputting the statistics.
 *
 * @author Michael Zigment
 * @version 1.0
 */
public class StatsCollector {
    private final LongSummaryStatistics summaryStatistics;

    /**
     * Class constructor.
     */
    public StatsCollector() {
        summaryStatistics = new LongSummaryStatistics();
    }

    /**
     * Calculates and saves off the time it took for a Request to be satisfied. The
     * time is saved off for statistics calculations.
     *
     * @param request the request that was completed and will have its time
     *                calculated and saved off
     */
    public synchronized void addCompletedRequest(Request request) {
        Instant timeNow = Instant.now();
        Duration duration = Duration.between(request.getTimeCreated(), timeNow);
        summaryStatistics.accept(duration.toMillis()); // Duration in milliseconds
    }

    /**
     * Returns a human readable string that represents the informative contents of
     * this stats collector. The format is different than other classes in this
     * package because it is used to provide output to the user. Time is output in
     * seconds. The representation is subject to change, but the following may be
     * regarded as typical:
     * 
     * " Total Requests Completed: 9"
     * " Fastest Completion Time: 0.195 seconds"
     * " Slowest Completion Time: 12.9 seconds"
     * " Average Completion Time: 5.753888888888889 seconds"
     *
     * @return a string representation of the object
     */
    @Override
    public synchronized String toString() {
        return "  Total Requests Completed:  " + summaryStatistics.getCount()
                + "\n  Fastest Completion Time:   "
                + ((summaryStatistics.getCount() > 0) ? ((summaryStatistics.getMin() * 0.001) + " seconds") : "N/A")
                + "\n  Slowest Completion Time:   "
                + ((summaryStatistics.getCount() > 0) ? ((summaryStatistics.getMax() * 0.001) + " seconds") : "N/A")
                + "\n  Average Completion Time:   "
                + ((summaryStatistics.getCount() > 0) ? ((summaryStatistics.getAverage() * 0.001) + " seconds") : "N/A")
                + '\n';
    }
}
