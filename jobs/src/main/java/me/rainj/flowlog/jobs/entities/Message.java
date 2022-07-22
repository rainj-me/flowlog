package me.rainj.flowlog.jobs.entities;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class Message {

    private Instant reportTime;

    private UUID id;

    private String srcApp;

    private String descApp;

    private String vpcId;

    private Integer bytesTx;

    private Integer bytesRx;

    public static Message fromMessage(me.rainj.flowlog.domain.Message message) {
        return Message.builder()
                .reportTime(message.getReportTime().toInstant())
                .id(UUID.randomUUID())
                .srcApp(message.getSrcApp())
                .descApp(message.getDescApp())
                .vpcId(message.getVpcId())
                .bytesTx(message.getBytesTx())
                .bytesRx(message.getBytesRx())
                .build();
    }
}
