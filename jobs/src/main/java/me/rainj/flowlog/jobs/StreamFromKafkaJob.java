package me.rainj.flowlog.jobs;

import java.util.*;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import me.rainj.flowlog.jobs.entities.Message;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.Durations;
import scala.Tuple2;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;


public final class StreamFromKafkaJob {

    private static final String KAFKA_BOOTSTRAP_SERVER_IP = "127.0.0.1";
    private static final String KAFKA_BOOTSTRAP_SERVER_PORT = "9092";
    private static final String KAKFA_TOPIC = "flowlog-events";
    private static final String APP_NAME = "flowlog-spark";

    public static void main(String[] args) throws Exception {

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName(APP_NAME);
        sparkConf.set("spark.cassandra.connection.host", KAFKA_BOOTSTRAP_SERVER_IP);
        JavaStreamingContext streamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(60));

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%s", KAFKA_BOOTSTRAP_SERVER_IP, KAFKA_BOOTSTRAP_SERVER_PORT));
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, APP_NAME);
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(
                streamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(Collections.singletonList(KAKFA_TOPIC), kafkaParams));

        JavaDStream<String> lines = messages.map(ConsumerRecord::value);
        lines.map(me.rainj.flowlog.domain.Message::fromString)
                .mapToPair(msg -> new Tuple2<>(msg.hashCode(), msg))
                .reduceByKeyAndWindow(me.rainj.flowlog.domain.Message::add, Durations.seconds(60), Durations.seconds(60))
                .map((msg) -> Message.fromMessage(msg._2))
                .foreachRDD((rdd) -> {
                    CassandraJavaUtil.javaFunctions(rdd)
                            .writerBuilder("flowlog", "flowlog",
                                    mapToRow(Message.class,
                                            Pair.of("reportTime", "report_time"),
                                            Pair.of("id", "id"),
                                            Pair.of("srcApp", "src_app"),
                                            Pair.of("descApp", "desc_app"),
                                            Pair.of("vpcId", "vpc_id"),
                                            Pair.of("bytesTx", "bytes_tx"),
                                            Pair.of("bytesRx", "bytes_rx")
                                    )).saveToCassandra();
                });
        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
