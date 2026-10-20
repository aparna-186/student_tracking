package org.example.skillsprint.dao;

import org.example.skillsprint.model.User;
import org.example.skillsprint.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Types;

public class UserDAO {
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, full_name, email, password_hash FROM users WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return new User(resultSet.getInt("user_id"), resultSet.getString("full_name"), resultSet.getString("email"),
                        resultSet.getString("password_hash"), null, null, null, null);
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /** Loads profile fields only; password_hash is deliberately not selected. */
    public User findProfileById(int userId) throws SQLException {
        String sql = "SELECT user_id, full_name, email, college, branch, github_url, linkedin_url FROM users WHERE user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) return null;
                return new User(resultSet.getInt("user_id"), resultSet.getString("full_name"),
                        resultSet.getString("email"), null, resultSet.getString("college"),
                        resultSet.getString("branch"), resultSet.getString("github_url"),
                        resultSet.getString("linkedin_url"));
            }
        }
    }

    public boolean emailExistsForAnotherUser(String email, int userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? AND user_id <> ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setInt(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /** Updates profile columns only; password_hash is never included in this statement. */
    public boolean updateProfile(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, college = ?, branch = ?, github_url = ?, linkedin_url = ? WHERE user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            setOptionalString(statement, 3, user.getCollege());
            setOptionalString(statement, 4, user.getBranch());
            setOptionalString(statement, 5, user.getGithubUrl());
            setOptionalString(statement, 6, user.getLinkedinUrl());
            statement.setInt(7, user.getUserId());
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 1) return true;

            // An unchanged profile can produce an affected-row count of zero in MySQL.
            try (PreparedStatement check = connection.prepareStatement("SELECT 1 FROM users WHERE user_id = ?")) {
                check.setInt(1, user.getUserId());
                try (ResultSet resultSet = check.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // Another request may claim the email after the duplicate check.
            return false;
        }
    }

    public boolean createUser(User user) throws SQLException {
        String sql = "INSERT INTO users "
                + "(full_name, email, password_hash, college, branch, github_url, linkedin_url) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            setOptionalString(statement, 4, user.getCollege());
            setOptionalString(statement, 5, user.getBranch());
            setOptionalString(statement, 6, user.getGithubUrl());
            setOptionalString(statement, 7, user.getLinkedinUrl());
            return statement.executeUpdate() == 1;
        } catch (SQLIntegrityConstraintViolationException e) {
            // A duplicate email may be added by another request after emailExists().
            return false;
        }
    }

    private void setOptionalString(PreparedStatement statement, int index, String value)
            throws SQLException {
        if (value == null || value.isEmpty()) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }
}
