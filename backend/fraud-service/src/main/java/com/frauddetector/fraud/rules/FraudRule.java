package com.frauddetector.fraud.rules;

import com.frauddetector.fraud.model.RuleResult;
import com.frauddetector.fraud.model.TransactionEvent;

public interface FraudRule {
    RuleResult evaluate(TransactionEvent event);
}