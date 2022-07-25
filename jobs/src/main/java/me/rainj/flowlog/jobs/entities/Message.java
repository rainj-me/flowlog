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

/**
 * Log message entity.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    /**
     * Aggregation level.
     */
    private String aggLevel;

    /**
     * Log message report time.
     */
    private Instant reportTime;

    /**
     * Log message uuid.
     */
    private UUID uid;

    /**
     * Source application of the log message.
     */
    private String srcApp;

    /**
     * Destination application of the log message.
     */
    private String descApp;

    /**
     * Vpc id of the log message.
     */
    private String vpcId;

    /**
     * Byte transmitted from source application to destination application, exclude from hash code.
     */
    private Integer bytesTx;

    /**
     * Byte transmitted from destination application to source application, exclude from hash code.
     */
    private Integer bytesRx;

    /**
     * The log message processing status.
     */
    private String status;

    /**
     * Build log message entity from domain.
     * @param message the log message domain.
     * @return log message entity.
     */
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

    /**
     * Build log message domain from log message entity.
     * @return log message domain.
     */
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

    /**
     * Create log message builder from exiting log message.
     * @param message the source message.
     * @return the target message builder.
     */
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
