package me.rainj.flowlog.service.entities;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import me.rainj.flowlog.domain.AggregationLevel;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

/**
 * Data object of the log message.
 */
@Table("message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    /**
     * Log message aggregation level, partition key.
     */
    @PrimaryKeyColumn(name = "agg_level", type = PrimaryKeyType.PARTITIONED)
    private String aggLevel;

    /**
     * Log message date, clustering key.
     */
    @PrimaryKeyColumn(name = "report_time", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant reportTime;

    /**
     * Log message uuid to avoid update the exiting log message, clustering key.
     */
    @PrimaryKeyColumn(name = "uid", type = PrimaryKeyType.CLUSTERED)
    private UUID uid;

    /**
     * Source application
     */
    @Column("src_app")
    private String srcApp;

    /**
     * Destination application
     */
    @Column("desc_app")
    private String descApp;

    /**
     * Vpc id
     */
    @Column("vpc_id")
    private String vpcId;

    /**
     * Byte transmitted from source application to destination application
     */
    @Column("bytes_tx")
    private Integer bytesTx;

    /**
     * Byte transmitted from destination application to source application
     */
    @Column("bytes_rx")
    private Integer bytesRx;

    /**
     * Convert the data object Message to domain object Message
     *
     * @return domain object message
     */
    public me.rainj.flowlog.domain.Message toMessage() {
        return me.rainj.flowlog.domain.Message.builder()
                .aggLevel(AggregationLevel.valueOf(this.aggLevel))
                .srcApp(this.srcApp)
                .descApp(this.descApp)
                .vpcId(this.vpcId)
                .bytesTx(this.bytesTx)
                .bytesRx(this.bytesRx)
                .reportTime(this.reportTime.atZone(ZoneId.of("UTC")))
                .build();
    }
}
