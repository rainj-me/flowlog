package me.rainj.flowlog.jobs.aggregators;

import lombok.RequiredArgsConstructor;
import me.rainj.flowlog.domain.AggregationLevel;
import me.rainj.flowlog.exceptions.FlowlogException;
import me.rainj.flowlog.jobs.entities.AggregationStatus;
import me.rainj.flowlog.jobs.entities.Message;
import me.rainj.flowlog.jobs.utils.Constant;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;

/**
 * Aggregate log messages by reading and writing to the cassandra table.
 */
@RequiredArgsConstructor
public class LogMessageAggregator {

    /**
     * Spark job name.
     */
    private final String jobName;

    /**
     * Log message table name.
     */
    private final String tableName;

    /**
     * Aggregate log message by aggregation level.
     *
     * @param targetLevel target aggregation level (e.g. FIVE_MINUTE)
     */
    public void aggregate(AggregationLevel targetLevel) {
        Instant endReportTime = targetLevel.truncateTo(Instant.now());
        Instant startReportTime = endReportTime.minusSeconds(targetLevel.getSeconds());
        System.err.println(jobName + " process messages, from:" + startReportTime + ", to:" + endReportTime);
        SparkConf config = new SparkConf()
                .setAppName(this.jobName)
                .set(Constant.CASSANDRA_CONNECTION_KEY, Constant.CASSANDRA_CONNECTION_HOST);

        try (JavaSparkContext context = new JavaSparkContext(config)) {
            // Filter the records
            List<Message> messages = javaFunctions(context)
                    .cassandraTable(Constant.KEYSPACE_NAME, this.tableName, mapRowTo(Message.class))
                    .where("agg_level=? and report_time>=? and report_time<?",
                            targetLevel.previous().name(), startReportTime, endReportTime)
                    .filter(msg -> AggregationStatus.NONE.name().equals(msg.getStatus()))
                    .collect();

            // All message are processed, silently return.
            if (messages.size() <= 0) {
                System.err.println(jobName + " no message found, from: " + startReportTime + " to: " + endReportTime);
                return;
            }
            List<Message> aggregatedMessage = context.parallelize(messages)
                    .map(msg -> {
                        me.rainj.flowlog.domain.Message message = msg.toMessage();
                        message.setReportTime(targetLevel.truncateTo(message.getReportTime()));
                        message.setAggLevel(targetLevel);
                        return message;
                    })
                    .mapToPair(msg -> new Tuple2<>(msg.hashCode(), msg))
                    .reduceByKey(me.rainj.flowlog.domain.Message::add)
                    .map((msg) -> Message.fromMessage(msg._2)).collect();

            // Update the process message to processed status
            List<Message> processedMessages = messages.stream().map(message ->
                    Message.from(message).status(AggregationStatus.PROCESSED.name()).build()
            ).collect(Collectors.toList());

            List<Message> pendingPersistMessages = new ArrayList<>();
            pendingPersistMessages.addAll(aggregatedMessage);
            pendingPersistMessages.addAll(processedMessages);

            // Persist the aggregated messages.
            // Potential data lose if the aggregation job interrupt here after the previous statement
            // persisting successfully.
            javaFunctions(context.parallelize(pendingPersistMessages))
                    .writerBuilder(Constant.KEYSPACE_NAME, this.tableName, mapToRow(Message.class))
                    .saveToCassandra();
            System.err.println(jobName + " process messages finished, totally processed: "
                    + pendingPersistMessages.size() + " messages");
        } catch (Exception e) {
            System.err.println(jobName + " process messages failed, error message: " + e.getMessage());
            throw new FlowlogException(e.getMessage());
        }
    }
}
