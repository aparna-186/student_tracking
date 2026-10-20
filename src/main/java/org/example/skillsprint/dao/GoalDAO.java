package org.example.skillsprint.dao;

import org.example.skillsprint.model.Goal;
import org.example.skillsprint.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {
    private static final String COLUMNS = "goal_id, user_id, goal_title, deadline, status";

    public List<Goal> listForUser(int userId) throws SQLException {
        return listForUser(userId, null);
    }

    public List<Goal> listForUser(int userId, String status) throws SQLException {
        boolean filterStatus = status != null && !status.trim().isEmpty();
        String sql = "SELECT " + COLUMNS + " FROM goals WHERE user_id = ?"
                + (filterStatus ? " AND status = ?" : "")
                + " ORDER BY deadline IS NULL, deadline ASC, goal_id DESC";
        List<Goal> goals = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            if (filterStatus) statement.setString(2, status);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) goals.add(fromRow(results));
            }
        }
        return goals;
    }

    public Goal findForUser(int goalId, int userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM goals WHERE goal_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, goalId);
            statement.setInt(2, userId);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? fromRow(results) : null;
            }
        }
    }

    public int create(Goal goal) throws SQLException {
        String sql = "INSERT INTO goals (user_id, goal_title, deadline, status) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, goal.getUserId());
            statement.setString(2, goal.getGoalTitle());
            setNullableDate(statement, 3, goal.getDeadline());
            statement.setString(4, goal.getStatus());
            if (statement.executeUpdate() != 1) return 0;
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean update(Goal goal, int userId) throws SQLException {
        String sql = "UPDATE goals SET goal_title = ?, deadline = ?, status = ? WHERE goal_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, goal.getGoalTitle());
            setNullableDate(statement, 2, goal.getDeadline());
            statement.setString(3, goal.getStatus());
            statement.setInt(4, goal.getGoalId());
            statement.setInt(5, userId);
            if (statement.executeUpdate() == 1) return true;
            return ownedGoalExists(connection, goal.getGoalId(), userId);
        }
    }

    public boolean markCompleted(int goalId, int userId) throws SQLException {
        String sql = "UPDATE goals SET status = 'Completed' WHERE goal_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, goalId);
            statement.setInt(2, userId);
            if (statement.executeUpdate() == 1) return true;
            return ownedGoalExists(connection, goalId, userId);
        }
    }

    public boolean delete(int goalId, int userId) throws SQLException {
        String sql = "DELETE FROM goals WHERE goal_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, goalId);
            statement.setInt(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    private boolean ownedGoalExists(Connection connection, int goalId, int userId) throws SQLException {
        String sql = "SELECT 1 FROM goals WHERE goal_id = ? AND user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, goalId);
            statement.setInt(2, userId);
            try (ResultSet results = statement.executeQuery()) {
                return results.next();
            }
        }
    }

    private void setNullableDate(PreparedStatement statement, int index, java.sql.Date date) throws SQLException {
        if (date == null) statement.setNull(index, Types.DATE);
        else statement.setDate(index, date);
    }

    private Goal fromRow(ResultSet results) throws SQLException {
        return new Goal(results.getInt("goal_id"), results.getInt("user_id"),
                results.getString("goal_title"), results.getDate("deadline"), results.getString("status"));
    }
}
