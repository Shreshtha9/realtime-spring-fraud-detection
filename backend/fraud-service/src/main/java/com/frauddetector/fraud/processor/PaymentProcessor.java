package com.frauddetector.fraud.processor;

import com.frauddetector.fraud.engine.FraudEngine;
import com.frauddetector.fraud.model.AlertMessage;
import com.frauddetector.fraud.model.Decision;
import com.frauddetector.fraud.model.DecisionView;
import com.frauddetector.fraud.model.FraudDecision;
import com.frauddetector.fraud.model.TransactionEvent;
import com.frauddetector.fraud.producer.AlertPublisher;
import com.frauddetector.fraud.repository.AlertRepository;
import com.frauddetector.fraud.repository.DecisionRepository;
import com.frauddetector.fraud.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * One place that handles a payment, used by both the Kafka consumer and POST /api/evaluate.
 *
 * Save payment + run rules + save decision + save alert all happen in ONE database transaction.
 * Either everything is stored or nothing is, so a crash in the middle can no longer leave a
 * payment saved without its alert (the old "lost alert" bug). The Kafka message for the
 * dashboard is sent only after the commit.
 */
@Service
public class PaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessor.class);

    private record Result(DecisionView view, AlertMessage alertToPublish) {
    }

    private final TransactionRepository transactions;
    private final DecisionRepository decisions;
    private final AlertRepository alerts;
    private final FraudEngine engine;
    private final AlertPublisher publisher;
    private final TransactionTemplate tx;

    public PaymentProcessor(TransactionRepository transactions,
                            DecisionRepository decisions,
                            AlertRepository alerts,
                            FraudEngine engine,
                            AlertPublisher publisher,
                            PlatformTransactionManager txManager) {
        this.transactions = transactions;
        this.decisions = decisions;
        this.alerts = alerts;
        this.engine = engine;
        this.publisher = publisher;
        this.tx = new TransactionTemplate(txManager);
    }

    /** Safe to call twice with the same transactionId: the second call returns the first decision. */
    public DecisionView process(TransactionEvent event) {
        Result result = tx.execute(status -> decideAndStore(event));

        if (result.alertToPublish() != null) {          // after commit, never inside the transaction
            publisher.publish(result.alertToPublish());
            AlertMessage a = result.alertToPublish();
            log.warn("{} user={} score={} reasons={}", a.decision(), a.userId(), a.riskScore(), a.reasons());
        }
        return result.view();
    }

    private Result decideAndStore(TransactionEvent event) {
        boolean isNew = transactions.saveIfNew(event);

        if (!isNew) {
            var existing = decisions.find(event.transactionId());
            if (existing.isPresent()) {
                return new Result(existing.get(), null);        // duplicate delivery, nothing to redo
            }
            // payment was stored before decisions existed: decide it now
        }

        FraudDecision decision = engine.evaluate(event);
        decisions.save(decision);

        AlertMessage alert = null;
        if (decision.decision() != Decision.ALLOW) {
            alert = AlertMessage.from(decision);
            alerts.save(alert);
        }
        return new Result(DecisionView.from(decision), alert);
    }
}