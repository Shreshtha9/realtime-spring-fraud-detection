package com.frauddetector.fraud.consumer;

import com.frauddetector.fraud.model.TransactionEvent;
import com.frauddetector.fraud.processor.PaymentProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    private final PaymentProcessor processor;

    public TransactionConsumer(PaymentProcessor processor) {
        this.processor = processor;
    }

    /**
     * If processing throws, the error handler in KafkaConfig retries 3 times and then
     * moves the message to the "transactions.DLT" topic instead of blocking the partition.
     */
    @KafkaListener(topics = "transactions", concurrency = "3")
    public void onTransaction(TransactionEvent event) {
        processor.process(event);
    }
}