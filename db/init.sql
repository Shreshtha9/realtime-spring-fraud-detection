CREATE TABLE cities (
    city_name  VARCHAR(30) PRIMARY KEY,
    latitude   DOUBLE PRECISION,
    longitude  DOUBLE PRECISION
);

CREATE TABLE users (
    user_id     VARCHAR(10) PRIMARY KEY,
    name        VARCHAR(50),
    home_city   VARCHAR(30),
    avg_amount  NUMERIC(12,2)
);

CREATE TABLE known_devices (
    user_id    VARCHAR(10) REFERENCES users(user_id),
    device_id  VARCHAR(50),
    PRIMARY KEY (user_id, device_id)
);

CREATE TABLE transactions (
    transaction_id  VARCHAR(50) PRIMARY KEY,
    user_id         VARCHAR(10) REFERENCES users(user_id),
    amount          NUMERIC(12,2),
    city            VARCHAR(30),
    device_id       VARCHAR(50),
    event_time      TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_transactions_user_time ON transactions (
    user_id, event_time
);

CREATE TABLE alerts (
    id              SERIAL PRIMARY KEY,
    transaction_id  VARCHAR(50) UNIQUE,
    user_id         VARCHAR(10),
    risk_score      INT,
    decision        VARCHAR(10),
    reasons         TEXT,
    created_at      TIMESTAMP DEFAULT now()
);

-- 10 Indian cities with coordinates (used by the impossible-travel rule later)
INSERT INTO cities VALUES
  ('Pune', 18.5204, 73.8567),
  ('Mumbai', 19.0760, 72.8777),
  ('Delhi', 28.7041, 77.1025),
  ('Bengaluru', 12.9716, 77.5946),
  ('Hyderabad', 17.3850, 78.4867),
  ('Chennai', 13.0827, 80.2707),
  ('Kolkata', 22.5726, 88.3639),
  ('Ahmedabad', 23.0225, 72.5714),
  ('Jaipur', 26.9124, 75.7873),
  ('Lucknow', 26.8467, 80.9462);

-- 500 simulated users with varied names, cities and spending levels
INSERT INTO users (user_id, name, home_city, avg_amount)
SELECT
    'U' || g,
    (ARRAY['Rahul','Priya','Amit','Sneha','Vikram','Anjali','Rohan','Neha','Karan','Pooja'])[1 + (g % 10)]
      || ' ' ||
    (ARRAY['Sharma','Verma','Patel','Singh','Iyer','Reddy','Gupta','Nair','Joshi','Mehta'])[1 + ((g / 10) % 10)],
    (ARRAY['Pune','Mumbai','Delhi','Bengaluru','Hyderabad','Chennai','Kolkata','Ahmedabad','Jaipur','Lucknow'])[1 + (g % 10)],
    (ARRAY[300, 500, 800, 1500, 2000, 5000])[1 + (g % 6)]
FROM generate_series(1, 500) AS g;

-- every user has one known device
INSERT INTO known_devices (user_id, device_id)
SELECT user_id, 'device-' || substring(user_id from 2) || '-a'
FROM users;

-- every third user has a second known device
INSERT INTO known_devices (user_id, device_id)
SELECT user_id, 'device-' || substring(user_id from 2) || '-b'
FROM users
WHERE substring(user_id from 2)::int % 3 = 0;