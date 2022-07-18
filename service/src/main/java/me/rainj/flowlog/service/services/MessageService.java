package me.rainj.flowlog.service.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.mapping.BasicMapId;
import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import me.rainj.flowlog.domain.Message;
import me.rainj.flowlog.service.repositories.MessageRepository;
import reactor.core.publisher.Flux;

@Service
public class MessageService {
    @Autowired
    private MessageRepository repository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private NewTopic topic;

    public Flux<Message> loadMessageByHour(int hour) {
        List<MapId> ids = new ArrayList<>();
        ids.add(BasicMapId.id("hour", hour));
        Flux<Message> messages = repository.findAllById(ids).map((msg) -> msg.toMessage())
                .groupBy(Message::hashCode)
                .flatMap((group) -> group.reduce((a, b) -> a.add(b)));

        return messages;
    }

    public void sendMessage(Message message) {
        kafkaTemplate.send(topic.name(), message.toString());
    }
}
