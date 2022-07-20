package me.rainj.flowlog.service.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * Kafka configuration.
 */
@Configuration
public class KafkaConfig {

    /**
     * The kafka bootstrap server, use to connect kafka cluster.
     */
    @Value(value = "${flowlog.kafka.bootstrapserver}")
    private String bootstrapServer;

    /**
     * The kafka topic.
     */
    @Value(value = "${flowlog.kafka.topic}")
    private String topic;

    /**
     * KafkaAdmin bean.
     * @return KafkaAdmin object.
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(ImmutableMap.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServer));
    }

    /**
     * Kafka topic bean.
     * @return NewTopic object.
     */
    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }

    /**
     * ProducerFactory bean, consumed by KafkaTemplate.
     * @return ProducerFactory object.
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(ImmutableMap.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class));
    }

    /**
     * KafkaTemplate bean, use to send message to kafka cluster.
     * @param producerFactory the ProduceFactory object.
     * @return KafkaTemplate object.
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}
