package com.frauddetector.transaction.generator;

import com.frauddetector.transaction.generator.UserDirectory.SimUser;
import com.frauddetector.transaction.model.TransactionEvent;
import com.frauddetector.transaction.producer.TransactionPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class ScenarioService {

    private final UserDirectory userDirectory;
    private final TransactionPublisher publisher;
    private final Random random = new Random();

    public ScenarioService(UserDirectory userDirectory, TransactionPublisher publisher) {
        this.userDirectory = userDirectory;
        this.publisher = publisher;
    }

    /** Runs one fraud scenario and returns the userId it used. */
    public String run(String type) {
        Instant now = Instant.now();
        SimUser user = switch (type) {
            case "velocity" -> velocity(now);
            case "travel" -> travel(now);
            case "device" -> device(now);
            case "amount" -> amount(now);
            case "daily" -> daily(now);
            case "takeover" -> takeover(now);
            default -> throw new IllegalArgumentException("Unknown scenario: " + type);
        };
        return user.userId();
    }

    // 8 normal-looking payments inside 8 seconds
    private SimUser velocity(Instant now) {
        SimUser u = pickUser(0);
        for (int i = 0; i < 8; i++) {
            send(u, u.avgAmount(), u.homeCity(), knownDevice(u), now.minusSeconds(7 - i));
        }
        return u;
    }

    // home city 10 minutes ago, then a far city now
    private SimUser travel(Instant now) {
        SimUser u = pickUser(0);
        send(u, u.avgAmount(), u.homeCity(), knownDevice(u), now.minusSeconds(600));
        send(u, u.avgAmount(), farCity(u), knownDevice(u), now);
        return u;
    }

    // normal payment from a device the user has never used
    private SimUser device(Instant now) {
        SimUser u = pickUser(0);
        send(u, u.avgAmount(), u.homeCity(), newDevice(), now);
        return u;
    }

    // payment 15 times bigger than usual
    private SimUser amount(Instant now) {
        SimUser u = pickUser(0);
        send(u, u.avgAmount() * 15, u.homeCity(), knownDevice(u), now);
        return u;
    }

    // three Rs 40,000 payments within 2 hours, using a user whose normal spend is high
    // so that the single amount is not unusual by itself
    private SimUser daily(Instant now) {
        SimUser u = pickUser(5000);
        send(u, 40000, u.homeCity(), knownDevice(u), now.minusSeconds(7200));
        send(u, 40000, u.homeCity(), knownDevice(u), now.minusSeconds(3600));
        send(u, 40000, u.homeCity(), knownDevice(u), now);
        return u;
    }

    // everything at once: new device + far city + big amount (account takeover)
    private SimUser takeover(Instant now) {
        SimUser u = pickUser(0);
        send(u, u.avgAmount(), u.homeCity(), knownDevice(u), now.minusSeconds(600));
        send(u, u.avgAmount() * 15, farCity(u), newDevice(), now);
        return u;
    }

    private SimUser pickUser(double minAvg) {
        List<SimUser> pool = userDirectory.getUsers().stream()
                .filter(u -> u.avgAmount() >= minAvg)
                .toList();
        if (pool.isEmpty()) {
            pool = userDirectory.getUsers();
        }
        return pool.get(random.nextInt(pool.size()));
    }

    private String knownDevice(SimUser u) {
        return u.devices().get(random.nextInt(u.devices().size()));
    }

    private String newDevice() {
        return "unknown-device-" + (100 + random.nextInt(900));
    }

    private String farCity(SimUser u) {
        return "Delhi".equals(u.homeCity()) ? "Chennai" : "Delhi";
    }

    private void send(SimUser u, double amount, String city, String device, Instant time) {
        double rounded = Math.round(amount * 100) / 100.0;
        publisher.publish(new TransactionEvent(
                UUID.randomUUID().toString(), u.userId(), rounded, city, device, time));
    }
}