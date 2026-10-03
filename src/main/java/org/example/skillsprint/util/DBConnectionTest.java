package org.example.skillsprint.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class DBConnectionTest {
    public static void main(String[] args) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            System.out.println("Database connection successful");

            try {
                DatabaseMetaData metadata = connection.getMetaData();
                String productName = metadata.getDatabaseProductName();
                String productVersion = metadata.getDatabaseProductVersion();

                if (productName != null && !productName.trim().isEmpty()) {
                    System.out.println("Database product: " + productName
                            + (productVersion == null || productVersion.trim().isEmpty()
                            ? "" : " " + productVersion));
                }
            } catch (SQLException e) {
                System.out.println("Database product information is unavailable.");
            }
        }
    }
}
