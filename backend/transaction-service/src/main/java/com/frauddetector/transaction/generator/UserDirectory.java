package com.frauddetector.transaction.generator;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserDirectory {

    private static final Logger log = LoggerFactory.getLogger(UserDirectory.class);

    public record SimUser(String userId, String homeCity, double avgAmount, List<String> devices) {
    }

    private record Row(String userId, String homeCity, double avgAmount, String deviceId) {
    }

    private final JdbcTemplate jdbc;
    private List<SimUser> users = List.of();

    public UserDirectory(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void load() {
        List<Row> rows = jdbc.query("""
                SELECT u.user_id, u.home_city, u.avg_amount, d.device_id
                FROM users u
                JOIN known_devices d ON d.user_id = u.user_id
                ORDER BY u.user_id
                """,
                (rs, i) -> new Row(
                        rs.getString("user_id"),
                        rs.getString("home_city"),
                        rs.getDouble("avg_amount"),
                        rs.getString("device_id")));

        Map<String, List<Row>> byUser = rows.stream()
                .collect(Collectors.groupingBy(Row::userId, LinkedHashMap::new, Collectors.toList()));

        this.users = byUser.values().stream()
                .map(list -> new SimUser(
                        list.get(0).userId(),
                        list.get(0).homeCity(),
                        list.get(0).avgAmount(),
                        list.stream().map(Row::deviceId).toList()))
                .toList();

        log.info("Loaded {} simulated users", users.size());
    }

    public List<SimUser> getUsers() {
        return users;
    }
}