package me.rainj.flowlog.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {

    public static final String DELIM = ",";

    private Integer hour;

    @JsonProperty("src_app")
    private String srcApp;

    @JsonProperty("desc_app")
    private String descApp;

    @JsonProperty("vpc_id")
    private String vpcId;

    @EqualsAndHashCode.Exclude
    @JsonProperty("bytes_tx")
    private Integer bytesTx;

    @EqualsAndHashCode.Exclude
    @JsonProperty("bytes_rx")
    private Integer bytesRx;

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

    public String toString() {
        return new StringBuilder()
                .append(this.hour).append(DELIM)
                .append(this.srcApp).append(DELIM)
                .append(this.descApp).append(DELIM)
                .append(this.vpcId).append(DELIM)
                .append(this.bytesTx).append(DELIM)
                .append(this.bytesRx).toString();
    }

    public static Message fromString(String str) {
        String[] tokens = str.split(DELIM);
        return Message.builder()
                .hour(Integer.parseInt(tokens[0]))
                .srcApp(tokens[1])
                .descApp(tokens[2])
                .vpcId(tokens[3])
                .bytesTx(Integer.parseInt(tokens[4]))
                .bytesRx(Integer.parseInt(tokens[5]))
                .build();
    }

}
