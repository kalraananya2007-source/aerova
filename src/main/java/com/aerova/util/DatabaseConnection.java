package com.aerova.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central JDBC connection utility.
 *
 * Configure AEROVA_DB_URL, AEROVA_DB_USER and AEROVA_DB_PASSWORD
 * as environment variables before running the application.
 */
public final class DatabaseConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/aerova_db?serverTimezone=UTC";

    private DatabaseConnection() {
        // Utility class; do not instantiate.
    }

    public static Connection getConnection() throws SQLException {
        String url = getEnvironmentValue("AEROVA_DB_URL", DEFAULT_URL);
        String user = getEnvironmentValue("AEROVA_DB_USER", "root");
        String password = getEnvironmentValue("AEROVA_DB_PASSWORD", "");

        return DriverManager.getConnection(url, user, password);
    }

    private static String getEnvironmentValue(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
