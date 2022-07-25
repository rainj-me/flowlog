package me.rainj.flowlog.jobs.entities;

import me.rainj.flowlog.domain.AggregationLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MessageTest {

    private Message entityMessage;
    private me.rainj.flowlog.domain.Message domainMessage;
    private final Instant reportTime = Instant.parse("2020-01-01T01:00:00Z");

    @BeforeEach
    public void initialize() {
        entityMessage = Message.builder()
                .aggLevel(AggregationLevel.ONE_MINUTE.name())
                .reportTime(reportTime)
                .uid(UUID.randomUUID())
                .srcApp("foo")
                .descApp("bar")
                .vpcId("vpc-0")
                .bytesTx(100)
                .bytesRx(200)
                .status(AggregationStatus.NONE.name())
                .build();
        domainMessage = me.rainj.flowlog.domain.Message.builder()
                .aggLevel(AggregationLevel.ONE_MINUTE)
                .reportTime(reportTime.atZone(ZoneId.of("UTC")))
                .srcApp("foo")
                .descApp("bar")
                .vpcId("vpc-0")
                .bytesTx(100)
                .bytesRx(200)
                .build();
    }

    @Test
    public void testFromMessage() {
        Message entity = Message.fromMessage(domainMessage);
        assertNotNull(entity);
        assertNotNull(entity.getUid());
        assertEquals(AggregationLevel.ONE_MINUTE.name(), entity.getAggLevel());
        assertEquals(reportTime, entity.getReportTime());
        assertEquals("foo", entity.getSrcApp());
        assertEquals("bar", entity.getDescApp());
        assertEquals("vpc-0", entity.getVpcId());
        assertEquals(100, entity.getBytesTx());
        assertEquals(200, entity.getBytesRx());
        assertEquals(AggregationStatus.NONE.name(), entity.getStatus());
    }

    @Test
    public void testToMessage() {
        me.rainj.flowlog.domain.Message message = entityMessage.toMessage();
        assertNotNull(message);
        assertEquals(AggregationLevel.ONE_MINUTE, message.getAggLevel());
        assertEquals("foo", message.getSrcApp());
        assertEquals("bar", message.getDescApp());
        assertEquals("vpc-0", message.getVpcId());
        assertEquals(100, message.getBytesTx());
        assertEquals(200, message.getBytesRx());
        assertEquals(reportTime.atZone(ZoneId.of("UTC")), message.getReportTime());
    }

    @Test
    public void testFromAnotherMessage() {
        Message entity = Message.from(entityMessage).build();
        assertNotNull(entity);
        assertNotNull(entity.getUid());
        assertEquals(AggregationLevel.ONE_MINUTE.name(), entity.getAggLevel());
        assertEquals(reportTime, entity.getReportTime());
        assertEquals("foo", entity.getSrcApp());
        assertEquals("bar", entity.getDescApp());
        assertEquals("vpc-0", entity.getVpcId());
        assertEquals(100, entity.getBytesTx());
        assertEquals(200, entity.getBytesRx());
        assertEquals(AggregationStatus.NONE.name(), entity.getStatus());
    }
}
