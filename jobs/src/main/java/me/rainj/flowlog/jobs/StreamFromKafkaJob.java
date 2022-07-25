package me.rainj.flowlog.jobs;

import java.util.*;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import me.rainj.flowlog.jobs.entities.Message;
import me.rainj.flowlog.jobs.utils.Constant;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.Durations;
import scala.Tuple2;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;

public final class StreamFromKafkaJob {

    private static final String APP_NAME = "flowlog-spark";
    private static final String TABLE_NAME = "message";
    private static final String KAKFA_TOPIC = "flowlog-events";

    public static void main(String[] args) throws Exception {

        Duration duration = Durations.seconds(60);
        SparkConf sparkConf = new SparkConf()
                .setAppName(APP_NAME)
                .set(Constant.CASSANDRA_CONNECTION_KEY, Constant.CASSANDRA_CONNECTION_HOST);

        JavaStreamingContext context = new JavaStreamingContext(sparkConf, duration);

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                String.format("%s:%s", Constant.KAFKA_BOOTSTRAP_SERVER_IP, Constant.KAFKA_BOOTSTRAP_SERVER_PORT));
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, APP_NAME);
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(
                context,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(Collections.singletonList(KAKFA_TOPIC), kafkaParams));

        JavaDStream<String> lines = messages.map(ConsumerRecord::value);
        lines.map(me.rainj.flowlog.domain.Message::fromString)
                .mapToPair(msg -> new Tuple2<>(msg.hashCode(), msg))
                .reduceByKeyAndWindow(me.rainj.flowlog.domain.Message::add, duration, duration)
                .map((msg) -> Message.fromMessage(msg._2))
                .foreachRDD((rdd) -> {
                    CassandraJavaUtil.javaFunctions(rdd)
                            .writerBuilder(Constant.KEYSPACE_NAME, TABLE_NAME, mapToRow(Message.class))
                            .saveToCassandra();
                });

        context.start();
        context.awaitTermination();
    }
}
