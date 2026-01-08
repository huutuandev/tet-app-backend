package com.tet.tet_app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tet.tet_app.event.EmailVerificationEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    // ===== PRODUCER =====
    @Bean
    public ProducerFactory<String, EmailVerificationEvent> producerFactory(
            ObjectMapper objectMapper
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                new org.springframework.kafka.support.serializer.JsonSerializer<>(objectMapper)
        );
    }

    @Bean
    public KafkaTemplate<String, EmailVerificationEvent> kafkaTemplate(
            ProducerFactory<String, EmailVerificationEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    // ===== CONSUMER =====
    @Bean
    public ConsumerFactory<String, EmailVerificationEvent> consumerFactory(
            ObjectMapper objectMapper
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "tet-email-verification-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new org.springframework.kafka.support.serializer.JsonDeserializer<>(
                        EmailVerificationEvent.class,
                        objectMapper
                )
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailVerificationEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, EmailVerificationEvent> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, EmailVerificationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
