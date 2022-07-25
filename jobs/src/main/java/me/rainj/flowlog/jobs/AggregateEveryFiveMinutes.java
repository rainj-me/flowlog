package me.rainj.flowlog.jobs;

import me.rainj.flowlog.domain.AggregationLevel;
import me.rainj.flowlog.jobs.aggregators.LogMessageAggregator;

/**
 * Aggregate log message every five minutes.
 */
public class AggregateEveryFiveMinutes {
    /**
     * Job name.
     */
    private static final String JOB_NAME = "flowlog-aggregate-every-five-minutes";

    /**
     * Log message table name.
     */
    private static final String TABLE_NAME = "message";

    public static void main(String[] args) {
        new LogMessageAggregator(JOB_NAME, TABLE_NAME).aggregate(AggregationLevel.FIVE_MINUTES);
    }
}
