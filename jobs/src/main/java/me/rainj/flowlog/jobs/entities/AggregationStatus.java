package me.rainj.flowlog.jobs.entities;

/**
 * Aggregation status, to identify whether log message is processed or not.
 */
public enum AggregationStatus {
    /**
     * Log message processed.
     */
    PROCESSED,
    /**
     * Log message not processed.
     */
    NONE
}
