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
                return new User(resultSet.getString("full_name"), resultSet.getString("email"),
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
