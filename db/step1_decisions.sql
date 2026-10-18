-- Step 1: store EVERY decision (ALLOW too) and which rules fired.
-- Safe to run more than once. Run it on your existing database; fresh setups get it from init.sql.

CREATE TABLE IF NOT EXISTS decisions (
    transaction_id  VARCHAR(50) PRIMARY KEY REFERENCES transactions(transaction_id),
    user_id         VARCHAR(10) NOT NULL,
    risk_score      INT         NOT NULL,
    decision        VARCHAR(10) NOT NULL,
    decided_at      TIMESTAMP   NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_decisions_decision_time ON decisions (decision, decided_at);

CREATE TABLE IF NOT EXISTS decision_rules (
    transaction_id  VARCHAR(50) NOT NULL REFERENCES decisions(transaction_id),
    rule_name       VARCHAR(30) NOT NULL,
    score           INT         NOT NULL,
    reason          TEXT,
    PRIMARY KEY (transaction_id, rule_name)
);