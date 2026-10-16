package com.frauddetector.fraud.consumer;

import com.frauddetector.fraud.engine.FraudEngine;
import com.frauddetector.fraud.model.AlertMessage;
import com.frauddetector.fraud.model.Decision;
import com.frauddetector.fraud.model.FraudDecision;
import com.frauddetector.fraud.model.TransactionEvent;
import com.frauddetector.fraud.producer.AlertPublisher;
import com.frauddetector.fraud.repository.AlertRepository;
import com.frauddetector.fraud.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    private final TransactionRepository transactions;
    private final AlertRepository alerts;
    private final FraudEngine engine;
    private final AlertPublisher publisher;

    public TransactionConsumer(TransactionRepository transactions,
                               AlertRepository alerts,
                               FraudEngine engine,
                               AlertPublisher publisher) {
        this.transactions = transactions;
        this.alerts = alerts;
        this.engine = engine;
        this.publisher = publisher;
    }

    @KafkaListener(topics = "transactions", concurrency = "3")
    public void onTransaction(TransactionEvent event) {
        // 1. save the payment; if we already saw it, stop here
        if (!transactions.saveIfNew(event)) {
            return;
        }

        // 2. run all fraud rules
        FraudDecision decision = engine.evaluate(event);
        if (decision.decision() == Decision.ALLOW) {
            return;
        }

        // 3. suspicious: save the alert and publish it
        AlertMessage alert = AlertMessage.from(decision);
        alerts.save(alert);
        publisher.publish(alert);

        log.warn("{} user={} score={} reasons={}",
                alert.decision(), alert.userId(), alert.riskScore(), alert.reasons());
    }
}