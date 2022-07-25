package me.rainj.flowlog.service.services;

import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.rainj.flowlog.domain.AggregationLevel;
import me.rainj.flowlog.domain.Message;
import me.rainj.flowlog.exceptions.FlowlogException;
import me.rainj.flowlog.service.repositories.MessageRepository;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;

/**
 * The log message service, uses to load message from database and send message to kafka.
 */
@Service
@AllArgsConstructor
@NoArgsConstructor
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    /**
     * Message repository.
     */
    @Autowired
    private MessageRepository repository;

    /**
     * Kafka template.
     */
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Kafka topic.
     */
    @Autowired
    private NewTopic topic;

    /**
     * load message by given hour and aggregate them.
     *
     * @param reportTime       the log agent report the log message.
     * @param aggregationLevel the log message aggregation level.
     * @return a distinct set of messages that hashcode are not the same.
     */
    public Flux<Message> loadMessage(String reportTime, String aggregationLevel) {
        Instant queryReportTime = null;
        AggregationLevel aggLevel = null;
        try {
            aggLevel = AggregationLevel.valueOf(aggregationLevel.toUpperCase());
            queryReportTime = aggLevel.truncateTo(Instant.parse(reportTime));
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            return Flux.error(new FlowlogException(e.getMessage()));
        }

        Flux<Message> result = repository.findAllByAggLevelAndReportTime(aggLevel.name(), queryReportTime)
                .map(me.rainj.flowlog.service.entities.Message::toMessage);

        // One minute aggregation still need to aggregate during retrieving
        // Five minutes, one hour, one day aggregation message doesn't require aggregation.
        if (AggregationLevel.ONE_MINUTE.equals(aggLevel)) {
            result = result.groupBy(Message::hashCode).flatMap((group) -> group.reduce(Message::add));
        }

        return result;
    }

    /**
     * Get current time.
     * @return current time.
     */
    @VisibleForTesting
    Instant currentTime() {
        return Instant.now();
    }

    /**
     * Send message to kafka if the message is not stale.
     *
     * @param message the log message.
     */
    public void sendMessage(Message message) {
        Instant now = currentTime();
        AggregationLevel level = AggregationLevel.ONE_MINUTE;
        message.setAggLevel(level);

        if (message.getReportTime().toInstant().isBefore(level.truncateTo(now).minusSeconds(level.getSeconds()))) {
            // Silently ignore the stale message.
            logger.error("Stale message: " + message);
            return;
        }
        message.setReportTime(level.truncateTo(message.getReportTime()));
        kafkaTemplate.send(topic.name(), message.toString());
    }
}
