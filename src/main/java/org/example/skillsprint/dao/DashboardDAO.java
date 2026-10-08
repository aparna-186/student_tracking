package org.example.skillsprint.dao;

import org.example.skillsprint.model.DashboardSummary;
import org.example.skillsprint.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {
    public DashboardSummary getSummaryForUser(int userId) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            int[] applicationCounts = countApplications(connection, userId);
            int totalCoding = count(connection, "SELECT COUNT(*) FROM coding_progress WHERE user_id = ?", userId);
            int totalCertificates = count(connection, "SELECT COUNT(*) FROM certificates WHERE user_id = ?", userId);
            int[] goalCounts = countGoals(connection, userId);

            return new DashboardSummary(applicationCounts[0], applicationCounts[1],
                    applicationCounts[2], applicationCounts[3], totalCoding, totalCertificates,
                    goalCounts[0], goalCounts[1]);
        }
    }

    private int[] countApplications(Connection connection, int userId) throws SQLException {
        String sql = "SELECT COUNT(*), "
                + "COALESCE(SUM(CASE WHEN status = 'Interview' THEN 1 ELSE 0 END), 0), "
                + "COALESCE(SUM(CASE WHEN status = 'Offer' THEN 1 ELSE 0 END), 0), "
                + "COALESCE(SUM(CASE WHEN status = 'Rejected' THEN 1 ELSE 0 END), 0) "
                + "FROM internship_applications WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return new int[]{resultSet.getInt(1), resultSet.getInt(2),
                        resultSet.getInt(3), resultSet.getInt(4)};
            }
        }
    }

    private int[] countGoals(Connection connection, int userId) throws SQLException {
        String sql = "SELECT "
                + "COALESCE(SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END), 0), "
                + "COALESCE(SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END), 0) "
                + "FROM goals WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return new int[]{resultSet.getInt(1), resultSet.getInt(2)};
            }
        }
    }

    private int count(Connection connection, String sql, int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }
}
