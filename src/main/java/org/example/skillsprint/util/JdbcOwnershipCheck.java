package org.example.skillsprint.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Shared JDBC check for an entity ID owned by a user. */
public final class JdbcOwnershipCheck {
    private JdbcOwnershipCheck() { }

    public static boolean exists(Connection connection, String sql, int recordId, int userId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, recordId);
            statement.setInt(2, userId);
            try (ResultSet results = statement.executeQuery()) {
                return results.next();
            }
        }
    }
}
