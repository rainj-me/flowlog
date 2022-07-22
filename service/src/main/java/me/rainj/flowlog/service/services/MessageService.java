package me.rainj.flowlog.service.services;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.mapping.BasicMapId;
import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import me.rainj.flowlog.domain.Message;
import me.rainj.flowlog.service.repositories.MessageRepository;
import reactor.core.publisher.Flux;

/**
 * The log message service, uses to load message from database and send message to kafka.
 */
@Service
@AllArgsConstructor
@NoArgsConstructor
public class MessageService {

    @Autowired
    private MessageRepository repository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private NewTopic topic;

    /**
     * load message by given hour and aggregate them.
     *
     * @param reportTime the log agent report the log message.
     * @return a distinct set of messages that hashcode are not the same.
     */
    public Flux<Message> loadMessageByHour(String reportTime) {
        Instant queryReportTime = null;
        try {
            queryReportTime = Instant.parse(reportTime).truncatedTo(ChronoUnit.MINUTES);
        } catch (RuntimeException e) {
            return Flux.error(e);
        }
        List<MapId> ids = new ArrayList<>();
        ids.add(BasicMapId.id("report_time", queryReportTime));
        return repository.findAllById(ids).map(me.rainj.flowlog.service.entities.Message::toMessage)
                .groupBy(Message::hashCode)
                .flatMap((group) -> group.reduce(Message::add));

    }

    /**
     * send message to kafka.
     *
     * @param message the log message.
     */
    public void sendMessage(Message message) {
        message.setReportTime(message.getReportTime().truncatedTo(ChronoUnit.MINUTES));
        kafkaTemplate.send(topic.name(), message.toString());
    }
}
