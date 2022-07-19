package me.rainj.flowlog.service.services;

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
     * @param hour the hour that agent report the log message.
     * @return a distinct set of messages that hashcode are not the same.
     */
    public Flux<Message> loadMessageByHour(int hour) {
        List<MapId> ids = new ArrayList<>();
        ids.add(BasicMapId.id("hour", hour));
        return repository.findAllById(ids).map(me.rainj.flowlog.service.entities.Message::toMessage)
                .groupBy(Message::hashCode)
                .flatMap((group) -> group.reduce(Message::add));

    }

    /**
     * send message to kafka.
     * @param message the log message.
     */
    public void sendMessage(Message message) {
        kafkaTemplate.send(topic.name(), message.toString());
    }
}
