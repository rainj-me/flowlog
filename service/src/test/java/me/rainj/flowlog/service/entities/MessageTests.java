package me.rainj.flowlog.service.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class MessageTests {

    private Message message;

    @BeforeEach
    public void initialize() {
        this.message = Message.builder()
                .id(UUID.randomUUID()).srcApp("foo")
                .descApp("bar").vpcId("vpc-0")
                .bytesRx(100).bytesTx(200).hour(1)
                .build();
    }

    @Test
    public void testToMessageDomainObject() {
        me.rainj.flowlog.domain.Message message = this.message.toMessage();
        assertEquals("foo", message.getSrcApp());
        assertEquals("bar", message.getDescApp());
        assertEquals("vpc-0", message.getVpcId());
        assertEquals(100, message.getBytesRx());
        assertEquals(200, message.getBytesTx());
        assertEquals(1, message.getHour());
    }

}
