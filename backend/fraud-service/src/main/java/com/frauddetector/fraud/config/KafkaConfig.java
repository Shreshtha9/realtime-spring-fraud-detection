package com.frauddetector.fraud.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name("transactions").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic fraudAlertsTopic() {
        return TopicBuilder.name("fraud-alerts").partitions(3).replicas(1).build();
    }

    /** Messages that keep failing end up here so one bad payment cannot block the partition. */
    @Bean
    public NewTopic transactionsDlt() {
        return TopicBuilder.name("transactions.DLT").partitions(3).replicas(1).build();
    }

    /** Retry 3 times, 1 second apart, then send the message to transactions.DLT (same partition number). */
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> template) {
        var recoverer = new DeadLetterPublishingRecoverer(template,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
    }
}