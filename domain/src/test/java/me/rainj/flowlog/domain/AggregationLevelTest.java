package me.rainj.flowlog.domain;

import me.rainj.flowlog.exceptions.FlowlogException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AggregationLevelTest {
    private final ZoneId UTC = ZoneId.of("UTC");

    @Test
    public void testTruncateToInstant() {
        assertEquals(Instant.parse("2020-01-01T01:00:00Z"),
                AggregationLevel.ONE_MINUTE.truncateTo(Instant.parse("2020-01-01T01:00:02Z")));
        assertEquals(Instant.parse("2020-01-01T01:00:00Z"),
                AggregationLevel.FIVE_MINUTES.truncateTo(Instant.parse("2020-01-01T01:04:30Z")));
        assertEquals(Instant.parse("2020-01-01T01:00:00Z"),
                AggregationLevel.ONE_HOUR.truncateTo(Instant.parse("2020-01-01T01:41:30Z")));
        assertEquals(Instant.parse("2020-01-01T00:00:00Z"),
                AggregationLevel.ONE_DAY.truncateTo(Instant.parse("2020-01-01T11:41:30Z")));
    }

    @Test
    public void testTruncateToZoneDateTime() {
        assertEquals(Instant.parse("2020-01-01T01:00:00Z").atZone(UTC),
                AggregationLevel.ONE_MINUTE.truncateTo(Instant.parse("2020-01-01T01:00:02Z").atZone(UTC)));
        assertEquals(Instant.parse("2020-01-01T01:00:00Z").atZone(UTC),
                AggregationLevel.FIVE_MINUTES.truncateTo(Instant.parse("2020-01-01T01:04:30Z").atZone(UTC)));
        assertEquals(Instant.parse("2020-01-01T01:00:00Z").atZone(UTC),
                AggregationLevel.ONE_HOUR.truncateTo(Instant.parse("2020-01-01T01:41:30Z").atZone(UTC)));
        assertEquals(Instant.parse("2020-01-01T00:00:00Z").atZone(UTC),
                AggregationLevel.ONE_DAY.truncateTo(Instant.parse("2020-01-01T11:41:30Z").atZone(UTC)));
    }

    @Test
    public void testPreviousLeve() {
        assertEquals(AggregationLevel.ONE_MINUTE, AggregationLevel.FIVE_MINUTES.previous());
        assertEquals(AggregationLevel.FIVE_MINUTES, AggregationLevel.ONE_HOUR.previous());
        assertEquals(AggregationLevel.ONE_HOUR, AggregationLevel.ONE_DAY.previous());
        assertThrows(FlowlogException.class, AggregationLevel.ONE_MINUTE::previous);
    }
}
