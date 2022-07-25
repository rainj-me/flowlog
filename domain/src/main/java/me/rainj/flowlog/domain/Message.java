package me.rainj.flowlog.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Message domain use by flowlog service and spark job.
 */
@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    /**
     * Delimiter, used by serialize message to string and deserialize message from string.
     */
    public static final String DELIM = ",";

    @JsonProperty("agg_level")
    private AggregationLevel aggLevel;

    /**
     * log report time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("report_time")
    private ZonedDateTime reportTime;

    /**
     * Source application
     */
    @JsonProperty("src_app")
    private String srcApp;

    /**
     * Destination application
     */
    @JsonProperty("desc_app")
    private String descApp;

    /**
     * Vpc id
     */
    @JsonProperty("vpc_id")
    private String vpcId;

    /**
     * Byte transmitted from source application to destination application, exclude from hash code.
     */
    @EqualsAndHashCode.Exclude
    @JsonProperty("bytes_tx")
    private Integer bytesTx;

    /**
     * Byte transmitted from destination application to source application, exclude from hash code.
     */
    @EqualsAndHashCode.Exclude
    @JsonProperty("bytes_rx")
    private Integer bytesRx;

    /**
     * Aggregate the message with same hash code.
     *
     * @param other the other message.
     * @return the aggregate message.
     */
    public Message add(Message other) {
        if (null == other)
            return this;
        if (this.hashCode() != other.hashCode())
            return this;
        int bytesRx = this.getBytesRx() == null ? 0 : this.getBytesRx();
        bytesRx += other.getBytesRx() == null ? 0 : other.getBytesRx();
        int bytesTx = this.getBytesTx() == null ? 0 : this.getBytesTx();
        bytesTx += other.getBytesTx() == null ? 0 : other.getBytesTx();
        this.setBytesRx(bytesRx);
        this.setBytesTx(bytesTx);
        return this;
    }

    /**
     * Serialize message to string.
     *
     * @return Serialized string.
     */
    public String toString() {
        return this.aggLevel.name() + DELIM +
                this.reportTime.toInstant().toString() + DELIM +
                this.srcApp + DELIM +
                this.descApp + DELIM +
                this.vpcId + DELIM +
                this.bytesTx + DELIM +
                this.bytesRx;
    }

    /**
     * Deserialize message from string.
     *
     * @param source The serialized message string.
     * @return Message.
     */
    public static Message fromString(String source) {
        String[] tokens = source.split(DELIM);
        return Message.builder()
                .aggLevel(AggregationLevel.valueOf(tokens[0]))
                .reportTime(Instant.parse(tokens[1]).atZone(ZoneId.of("UTC")))
                .srcApp(tokens[2])
                .descApp(tokens[3])
                .vpcId(tokens[4])
                .bytesTx(Integer.parseInt(tokens[5]))
                .bytesRx(Integer.parseInt(tokens[6]))
                .build();
    }

}
