package com.frauddetector.transaction.generator;

import com.frauddetector.transaction.generator.UserDirectory.SimUser;
import com.frauddetector.transaction.model.TransactionEvent;
import com.frauddetector.transaction.producer.TransactionPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TrafficGenerator {

    private final UserDirectory userDirectory;
    private final TransactionPublisher publisher;
    private final Random random = new Random();
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Value("${simulator.events-per-second:2}")
    private int eventsPerSecond;

    public TrafficGenerator(UserDirectory userDirectory, TransactionPublisher publisher) {
        this.userDirectory = userDirectory;
        this.publisher = publisher;
    }

    public void start() {
        running.set(true);
    }

    public void stop() {
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }

    @Scheduled(fixedRate = 1000)
    public void tick() {
        if (!running.get()) {
            return;
        }
        for (int i = 0; i < eventsPerSecond; i++) {
            publisher.publish(normalTransaction());
        }
    }

    private TransactionEvent normalTransaction() {
        List<SimUser> users = userDirectory.getUsers();
        SimUser user = users.get(random.nextInt(users.size()));

        double amount = user.avgAmount() * (0.5 + random.nextDouble());
        amount = Math.round(amount * 100) / 100.0;

        String device = user.devices().get(random.nextInt(user.devices().size()));

        return new TransactionEvent(
                UUID.randomUUID().toString(),
                user.userId(),
                amount,
                user.homeCity(),
                device,
                Instant.now());
    }
}