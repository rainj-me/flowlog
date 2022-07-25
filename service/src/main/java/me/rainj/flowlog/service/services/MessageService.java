package me.rainj.flowlog.service.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.rainj.flowlog.domain.AggregationLevel;
import me.rainj.flowlog.exceptions.FlowlogException;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.mapping.BasicMapId;
import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import me.rainj.flowlog.domain.Message;
import me.rainj.flowlog.service.repositories.MessageRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The log message service, uses to load message from database and send message to kafka.
 */
@Service
@AllArgsConstructor
@NoArgsConstructor
public class MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageRepository repository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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
            return Flux.error(new FlowlogException(e.getMessage()));
        }

        if (AggregationLevel.ONE_MINUTE.equals(aggLevel)) {
            return repository.findAllByAggLevelAndReportTime(aggLevel.name(), queryReportTime)
                    .map(me.rainj.flowlog.service.entities.Message::toMessage)
                    .groupBy(Message::hashCode)
                    .flatMap((group) -> group.reduce(Message::add));
        } else {
            return repository.findAllByAggLevelAndReportTime(aggLevel.name(), queryReportTime)
                    .map(me.rainj.flowlog.service.entities.Message::toMessage);
        }
    }

    @VisibleForTesting
    Instant currentTime() {
        return Instant.now();
    }

    /**
     * Send message to kafka if the message is not stale.
     *
     * @param message the log message.
     */
    public Mono<Message> sendMessage(Message message) {
        Instant now = currentTime();
        AggregationLevel level = AggregationLevel.ONE_MINUTE;
        message.setAggLevel(level);
        LOG.info("log report time: " + message.getReportTime() + ", now: " + now);

        if (message.getReportTime().toInstant().isBefore(level.truncateTo(now).minusSeconds(level.getSeconds()))) {
            LOG.error("The log message: " + message + " is stale");;
            Mono.error(new FlowlogException("The log message: " + message + " is stale"));
        }
        message.setReportTime(level.truncateTo(message.getReportTime()));
        kafkaTemplate.send(topic.name(), message.toString());
        return Mono.just(message);
    }
}
