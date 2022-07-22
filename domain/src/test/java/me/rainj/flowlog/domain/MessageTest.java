/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package me.rainj.flowlog.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private Message message;
    private final ZonedDateTime reportTime = Instant.parse("2020-01-01T01:00:00Z").atZone(ZoneId.of("UTC"));

    @BeforeEach
    public void initialize() {
        this.message = Message.builder()
                .reportTime(reportTime)
                .srcApp("foo")
                .descApp("bar")
                .vpcId("vpc-0")
                .bytesTx(100)
                .bytesRx(200)
                .build();
    }

    @Test
    public void testAddMessageSuccessful() {
        Message other = Message.builder()
                .reportTime(reportTime)
                .srcApp("foo")
                .descApp("bar")
                .vpcId("vpc-0")
                .bytesTx(100)
                .bytesRx(200)
                .build();
        Message result = this.message.add(other);
        assertEquals("foo", result.getSrcApp());
        assertEquals("bar", result.getDescApp());
        assertEquals("vpc-0", result.getVpcId());
        assertEquals(200, result.getBytesTx());
        assertEquals(400, result.getBytesRx());
        assertEquals(reportTime, result.getReportTime());
    }

    @Test
    public void testAddMessageFailed() {
        ZonedDateTime anotherReportTime = Instant.parse("2020-02-02T02:00:00Z").atZone(ZoneId.of("UTC"));
        Message other = Message.builder()
                .reportTime(anotherReportTime)
                .srcApp("foo")
                .descApp("bar")
                .vpcId("vpc-0")
                .bytesTx(100)
                .bytesRx(200)
                .build();
        Message result = this.message.add(other);
        assertSame(this.message, result);
    }

    @Test
    public void testAddMessageFailed2() {
        Message result = this.message.add(null);
        assertSame(this.message, result);
    }

    @Test
    public void testMessageToString() {
        String result = this.message.toString();
        assertEquals("2020-01-01T01:00:00Z,foo,bar,vpc-0,100,200", result);
    }

    @Test
    public void testMessageFromString() {
        String source = "2020-01-01T01:00:00Z,foo,bar,vpc-0,100,200";
        Message result = Message.fromString(source);
        assertNotNull(result);
        assertEquals("foo", message.getSrcApp());
        assertEquals("bar", message.getDescApp());
        assertEquals("vpc-0", message.getVpcId());
        assertEquals(100, message.getBytesTx());
        assertEquals(200, message.getBytesRx());
        assertEquals(reportTime, message.getReportTime());
    }

    @Test
    public void testHashCode() {
        ZonedDateTime anotherReportTime = Instant.parse("2020-02-02T02:00:00Z").atZone(ZoneId.of("UTC"));
        Message otherMessage1 = Message.builder()
                .reportTime(reportTime)
                .srcApp("foo")
                .descApp("bar")
                .vpcId("vpc-0")
                .bytesTx(200)
                .bytesRx(400)
                .build();
        Message otherMessage2 = Message.builder()
                .reportTime(anotherReportTime)
                .srcApp("foo")
                .descApp("bar")
                .vpcId("vpc-0")
                .bytesTx(100)
                .bytesRx(200)
                .build();
        Message otherMessage3 = Message.builder()
                .reportTime(reportTime)
                .srcApp("foobar")
                .descApp("bar")
                .vpcId("vpc-0")
                .bytesTx(100)
                .bytesRx(200)
                .build();
        Message otherMessage4 = Message.builder()
                .reportTime(reportTime)
                .srcApp("biz")
                .descApp("bar")
                .vpcId("vpc-0")
                .bytesTx(100)
                .bytesRx(200)
                .build();
        Message otherMessage5 = Message.builder()
                .reportTime(reportTime)
                .srcApp("foo")
                .descApp("bar")
                .vpcId("vpc-1")
                .bytesTx(100)
                .bytesRx(200)
                .build();

        assertEquals(this.message.hashCode(), otherMessage1.hashCode());
        assertNotEquals(this.message.hashCode(), otherMessage2.hashCode());
        assertNotEquals(this.message.hashCode(), otherMessage3.hashCode());
        assertNotEquals(this.message.hashCode(), otherMessage4.hashCode());
        assertNotEquals(this.message.hashCode(), otherMessage5.hashCode());
    }

}
