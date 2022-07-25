package me.rainj.flowlog.jobs.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.rainj.flowlog.domain.AggregationLevel;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    private String aggLevel;

    private Instant reportTime;

    private UUID uid;

    private String srcApp;

    private String descApp;

    private String vpcId;

    private Integer bytesTx;

    private Integer bytesRx;

    private String status;


    public static Message fromMessage(me.rainj.flowlog.domain.Message message) {
        return Message.builder()
                .aggLevel(message.getAggLevel().name())
                .reportTime(message.getReportTime().toInstant())
                .uid(UUID.randomUUID())
                .srcApp(message.getSrcApp())
                .descApp(message.getDescApp())
                .vpcId(message.getVpcId())
                .bytesTx(message.getBytesTx())
                .bytesRx(message.getBytesRx())
                .status(AggregationStatus.NONE.name())
                .build();
    }

    public me.rainj.flowlog.domain.Message toMessage() {
        return me.rainj.flowlog.domain.Message.builder()
                .aggLevel(AggregationLevel.valueOf(this.aggLevel))
                .reportTime(this.reportTime.atZone(ZoneId.of("UTC")))
                .srcApp(this.srcApp)
                .descApp(this.descApp)
                .vpcId(this.vpcId)
                .bytesRx(this.bytesRx)
                .bytesTx(this.bytesTx)
                .build();
    }

    public static Message.MessageBuilder from(Message message) {
        return Message.builder()
                .aggLevel(message.getAggLevel())
                .reportTime(message.getReportTime())
                .uid(message.getUid())
                .srcApp(message.getSrcApp())
                .descApp(message.getDescApp())
                .vpcId(message.getVpcId())
                .bytesTx(message.getBytesTx())
                .bytesRx(message.getBytesRx())
                .status(message.getStatus());
    }
}
