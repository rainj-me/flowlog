package me.rainj.flowlog.service.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.rainj.flowlog.domain.AggregationLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public class MessageTests {

    private Message message;

    @BeforeEach
    public void initialize() {
        Instant reportTime = Instant.parse("2020-01-01T01:00:00Z");
        this.message = Message.builder()
                .aggLevel("ONE_MINUTE")
                .uid(UUID.randomUUID()).srcApp("foo")
                .descApp("bar").vpcId("vpc-0")
                .bytesRx(100).bytesTx(200).reportTime(reportTime)
                .build();
    }

    @Test
    public void testToMessageDomainObject() {
        Instant reportTime = Instant.parse("2020-01-01T01:00:00Z");
        me.rainj.flowlog.domain.Message message = this.message.toMessage();
        assertEquals(AggregationLevel.ONE_MINUTE, message.getAggLevel());
        assertEquals("foo", message.getSrcApp());
        assertEquals("bar", message.getDescApp());
        assertEquals("vpc-0", message.getVpcId());
        assertEquals(100, message.getBytesRx());
        assertEquals(200, message.getBytesTx());
        assertEquals(reportTime.atZone(ZoneId.of("UTC")), message.getReportTime());
    }

}
