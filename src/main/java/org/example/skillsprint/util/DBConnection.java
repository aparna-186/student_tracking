package org.example.skillsprint.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = getSetting("skillsprint.db.url", "SKILLSPRINT_DB_URL");
        String username = getSetting("skillsprint.db.username", "SKILLSPRINT_DB_USERNAME");
        String password = getSetting("skillsprint.db.password", "SKILLSPRINT_DB_PASSWORD");

        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Connector/J driver is not available.", e);
        }

        return DriverManager.getConnection(url, username, password);
    }

    private static String getSetting(String propertyName, String environmentName) throws SQLException {
        String value = System.getProperty(propertyName);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(environmentName);
        }
        if (value == null || value.trim().isEmpty()) {
            throw new SQLException("Set the " + environmentName + " environment variable or "
                    + propertyName + " system property before connecting to MySQL.");
        }
        return value;
    }
}
