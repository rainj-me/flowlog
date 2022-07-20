package me.rainj.flowlog.service.services;

import me.rainj.flowlog.domain.Message;
import me.rainj.flowlog.service.repositories.MessageRepository;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
public class MessageServiceTests {
    @MockBean
    private MessageRepository repository;

    @MockBean
    private KafkaTemplate<String, String> mockKafkaTemplate;

    private MessageService service;

    @BeforeEach
    public void initialize() {
        NewTopic topic = new NewTopic("test", 1, (short) 1);
        this.service = new MessageService(repository, mockKafkaTemplate, topic);
    }

    @Test
    public void testLoadMessageByHour() {
        me.rainj.flowlog.service.entities.Message message = me.rainj.flowlog.service.entities.Message.builder()
                .id(UUID.randomUUID()).srcApp("foo")
                .descApp("bar").vpcId("vpc-0")
                .bytesRx(100).bytesTx(200).hour(1)
                .build();

        Mockito.when(repository.findAllById(anyIterable())).thenReturn(Flux.just(message));
        Flux<Message> messages = this.service.loadMessageByHour(1);
        Message result = messages.blockFirst();
        assertNotNull(result);
        assertEquals("foo", result.getSrcApp());
        assertEquals("bar", result.getDescApp());
        assertEquals("vpc-0", result.getVpcId());
        assertEquals(100, result.getBytesRx());
        assertEquals(200, result.getBytesTx());
        assertEquals(1, result.getHour());
    }

    @Test
    public void testLoadMessageByHourAggregate() {
        me.rainj.flowlog.service.entities.Message msg1 = me.rainj.flowlog.service.entities.Message.builder()
                .id(UUID.randomUUID()).srcApp("foo")
                .descApp("bar").vpcId("vpc-0")
                .bytesRx(100).bytesTx(200).hour(1)
                .build();

        me.rainj.flowlog.service.entities.Message msg2 = me.rainj.flowlog.service.entities.Message.builder()
                .id(UUID.randomUUID()).srcApp("foo")
                .descApp("bar").vpcId("vpc-0")
                .bytesRx(200).bytesTx(100).hour(1)
                .build();


        Mockito.when(repository.findAllById(anyIterable())).thenReturn(Flux.just(msg1, msg2));
        Flux<Message> messages = this.service.loadMessageByHour(1);
        Message result = messages.blockFirst();
        assertNotNull(result);
        assertEquals("foo", result.getSrcApp());
        assertEquals("bar", result.getDescApp());
        assertEquals("vpc-0", result.getVpcId());
        assertEquals(300, result.getBytesRx());
        assertEquals(300, result.getBytesTx());
        assertEquals(1, result.getHour());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSendMessageToKafka() {
        Message message = Mockito.mock(Message.class);
        SendResult<String, String> result = (SendResult<String, String>) Mockito.mock(SendResult.class);
        Mockito.when(mockKafkaTemplate.send(anyString(), anyString())).thenReturn(new AsyncResult<>(result));
        this.service.sendMessage(message);

        Mockito.verify(mockKafkaTemplate, Mockito.times(1)).send(anyString(), anyString());
    }

}
