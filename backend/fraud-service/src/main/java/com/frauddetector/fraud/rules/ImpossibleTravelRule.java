package com.frauddetector.fraud.rules;

import com.frauddetector.fraud.model.RuleResult;
import com.frauddetector.fraud.model.TransactionEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class ImpossibleTravelRule implements FraudRule {

    private static final String NAME = "IMPOSSIBLE_TRAVEL";
    private static final double MAX_SPEED_KMH = 900.0;   // about the speed of a passenger plane

    private final JdbcTemplate jdbc;

    public ImpossibleTravelRule(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

      @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "Two payments from cities too far apart to travel between at 900 km/h";
    }

    @Override
    public int score() {
        return 40;
    }

    @Override
    public RuleResult evaluate(TransactionEvent event) {
        // the user's most recent earlier payment
        List<Map<String, Object>> previous = jdbc.queryForList("""
                SELECT city, event_time FROM transactions
                WHERE user_id = ? AND transaction_id <> ? AND event_time < ?
                ORDER BY event_time DESC
                LIMIT 1
                """,
                event.userId(), event.transactionId(), Timestamp.from(event.eventTime()));

        if (previous.isEmpty()) {
            return RuleResult.clean(NAME);
        }

        String previousCity = (String) previous.get(0).get("city");
        Instant previousTime = ((Timestamp) previous.get(0).get("event_time")).toInstant();

        if (previousCity == null || previousCity.equals(event.city())) {
            return RuleResult.clean(NAME);
        }

        double[] from = coordinates(previousCity);
        double[] to = coordinates(event.city());
        if (from == null || to == null) {
            return RuleResult.clean(NAME);          // a city we don't know, so we can't judge
        }

        double distanceKm = distanceKm(from[0], from[1], to[0], to[1]);
        double hours = Duration.between(previousTime, event.eventTime()).toSeconds() / 3600.0;

        // could a plane at 900 km/h cover this distance in the time that passed?
        if (distanceKm > MAX_SPEED_KMH * hours) {
            return RuleResult.flagged(NAME, score(), String.format(
                    "%s to %s is %.0f km, but only %.0f minutes passed",
                    previousCity, event.city(), distanceKm, hours * 60));
        }
        return RuleResult.clean(NAME);
    }

    private double[] coordinates(String city) {
        List<double[]> rows = jdbc.query(
                "SELECT latitude, longitude FROM cities WHERE city_name = ?",
                (rs, i) -> new double[]{rs.getDouble("latitude"), rs.getDouble("longitude")},
                city);
        return rows.isEmpty() ? null : rows.get(0);
    }

    // straight-line distance between two points on the Earth (haversine formula)
    private static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * earthRadiusKm * Math.asin(Math.sqrt(a));
    }
}