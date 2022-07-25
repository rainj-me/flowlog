package me.rainj.flowlog.jobs.aggregators;

import lombok.RequiredArgsConstructor;
import me.rainj.flowlog.domain.AggregationLevel;
import me.rainj.flowlog.exceptions.FlowlogException;
import me.rainj.flowlog.jobs.entities.AggregationStatus;
import me.rainj.flowlog.jobs.entities.Message;
import me.rainj.flowlog.jobs.utils.Constant;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.time.Instant;

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
     * Aggregate log message.
     * @param targetLevel target aggregation level (e.g. FIVE_MINUTE)
     */
    public void aggregate(AggregationLevel targetLevel) {
        Instant endReportTime = targetLevel.truncateTo(Instant.now());
        Instant startReportTime = endReportTime.minusSeconds(targetLevel.getSeconds());
        SparkConf config = new SparkConf()
                .setAppName(this.jobName)
                .set(Constant.CASSANDRA_CONNECTION_KEY, Constant.CASSANDRA_CONNECTION_HOST);

        try (JavaSparkContext context = new JavaSparkContext(config)) {
            JavaRDD<Message> messages = javaFunctions(context)
                    .cassandraTable(Constant.KEYSPACE_NAME, this.tableName, mapRowTo(Message.class))
                    .where("agg_level=? and report_time>=? and report_time<?",
                            targetLevel.previous().name(), startReportTime, endReportTime);
            // Filter the record and update the message the processed.
            messages = messages.filter(msg -> AggregationStatus.NONE.name().equals(msg.getStatus()))
                    .map(msg -> {
                        msg.setStatus(AggregationStatus.PROCESSED.name());
                        return msg;
                    });
            // All message are processed, silently return.
            if (messages.count() <= 0) {
                return;
            }
            JavaRDD<Message> aggregatedMessage = messages.map(msg -> {
                        me.rainj.flowlog.domain.Message message = msg.toMessage();
                        message.setReportTime(targetLevel.truncateTo(message.getReportTime()));
                        message.setAggLevel(targetLevel);
                        return message;
                    })
                    .mapToPair(msg -> new Tuple2<>(msg.hashCode(), msg))
                    .reduceByKey(me.rainj.flowlog.domain.Message::add)
                    .map((msg) -> Message.fromMessage(msg._2));
            // Persist the processed messages.
            javaFunctions(messages).writerBuilder(Constant.KEYSPACE_NAME, this.tableName, mapToRow(Message.class)).saveToCassandra();
            // Persist the aggregated messages.
            // Potential data lose if the aggregation job interrupt here after the previous statement persisting successfully.
            javaFunctions(aggregatedMessage).writerBuilder(Constant.KEYSPACE_NAME, this.tableName, mapToRow(Message.class)).saveToCassandra();
        } catch (Exception e) {
            throw new FlowlogException(e.getMessage());
        }
    }
}
