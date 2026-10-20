package org.example.skillsprint.dao;

import org.example.skillsprint.model.CodingProgress;
import org.example.skillsprint.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CodingProgressDAO {
    private static final String COLUMNS = "coding_id, user_id, platform, problem_name, difficulty, problem_status, solved_date, problem_url";

    public List<CodingProgress> listForUser(int userId, String platform, String difficulty, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT ").append(COLUMNS)
                .append(" FROM coding_progress WHERE user_id = ?");
        boolean filterPlatform = platform != null && !platform.trim().isEmpty();
        boolean filterDifficulty = difficulty != null && !difficulty.trim().isEmpty();
        boolean filterStatus = status != null && !status.trim().isEmpty();
        if (filterPlatform) sql.append(" AND platform LIKE ?");
        if (filterDifficulty) sql.append(" AND difficulty = ?");
        if (filterStatus) sql.append(" AND problem_status = ?");
        sql.append(" ORDER BY solved_date IS NULL, solved_date DESC, coding_id DESC");

        List<CodingProgress> records = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int index = 1;
            statement.setInt(index++, userId);
            if (filterPlatform) statement.setString(index++, "%" + platform.trim() + "%");
            if (filterDifficulty) statement.setString(index++, difficulty);
            if (filterStatus) statement.setString(index, status);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) records.add(fromRow(results));
            }
        }
        return records;
    }

    public CodingProgress findForUser(int codingId, int userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM coding_progress WHERE coding_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, codingId);
            statement.setInt(2, userId);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? fromRow(results) : null;
            }
        }
    }

    public int create(CodingProgress record) throws SQLException {
        String sql = "INSERT INTO coding_progress (user_id, platform, problem_name, difficulty, problem_status, solved_date, problem_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, record.getUserId());
            setNullableString(statement, 2, record.getPlatform());
            statement.setString(3, record.getProblemName());
            setNullableString(statement, 4, record.getDifficulty());
            statement.setString(5, record.getProblemStatus());
            setNullableDate(statement, 6, record.getSolvedDate());
            setNullableString(statement, 7, record.getProblemUrl());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean update(CodingProgress record, int userId) throws SQLException {
        String sql = "UPDATE coding_progress SET platform = ?, problem_name = ?, difficulty = ?, problem_status = ?, solved_date = ?, problem_url = ? WHERE coding_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setNullableString(statement, 1, record.getPlatform());
            statement.setString(2, record.getProblemName());
            setNullableString(statement, 3, record.getDifficulty());
            statement.setString(4, record.getProblemStatus());
            setNullableDate(statement, 5, record.getSolvedDate());
            setNullableString(statement, 6, record.getProblemUrl());
            statement.setInt(7, record.getCodingId());
            statement.setInt(8, userId);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int codingId, int userId) throws SQLException {
        String sql = "DELETE FROM coding_progress WHERE coding_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, codingId);
            statement.setInt(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    private void setNullableString(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null || value.isEmpty()) statement.setNull(index, Types.VARCHAR);
        else statement.setString(index, value);
    }

    private void setNullableDate(PreparedStatement statement, int index, java.sql.Date value) throws SQLException {
        if (value == null) statement.setNull(index, Types.DATE);
        else statement.setDate(index, value);
    }

    private CodingProgress fromRow(ResultSet results) throws SQLException {
        return new CodingProgress(results.getInt("coding_id"), results.getInt("user_id"),
                results.getString("platform"), results.getString("problem_name"),
                results.getString("difficulty"), results.getString("problem_status"),
                results.getDate("solved_date"), results.getString("problem_url"));
    }
}
