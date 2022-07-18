package me.rainj.flowlog.spark.entities;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Message {

    private int hour;

    private UUID id;

    private String srcApp;

    private String descApp;

    private String vpcId;

    private Integer bytesTx;


    private Integer bytesRx;

    public static Message fromMessage(me.rainj.flowlog.domain.Message message) {
        return Message.builder()
                .hour(message.getHour())
                .id(UUID.randomUUID())
                .srcApp(message.getSrcApp())
                .descApp(message.getDescApp())
                .vpcId(message.getVpcId())
                .bytesTx(message.getBytesTx())
                .bytesRx(message.getBytesRx())
                .build();
    }
}
