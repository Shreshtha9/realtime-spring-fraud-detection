package com.frauddetector.fraud.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean exists(String userId) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE user_id = ?", Integer.class, userId);
        return n != null && n > 0;
    }
}