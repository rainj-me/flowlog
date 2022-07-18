package me.rainj.flowlog.service.entities;

import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Table("flowlog")
@Data
public class Message {
    @PrimaryKeyColumn(name = "hour", type = PrimaryKeyType.PARTITIONED)
    private int hour;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
    private UUID id;

    @Column("src_app")
    private String srcApp;

    @Column("desc_app")
    private String descApp;

    @Column("vpc_id")
    private String vpcId;

    @Column("bytes_tx")
    private Integer bytesTx;

    @Column("bytes_rx")
    private Integer bytesRx;

    public me.rainj.flowlog.domain.Message toMessage() {
        return me.rainj.flowlog.domain.Message.builder()
        .srcApp(this.srcApp)
        .descApp(this.descApp)
        .vpcId(this.vpcId)
        .bytesTx(this.bytesTx)
        .bytesRx(this.bytesRx)
        .hour(this.hour)
        .build();
    }
}
