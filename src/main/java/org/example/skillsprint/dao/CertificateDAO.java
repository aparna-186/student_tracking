package org.example.skillsprint.dao;

import org.example.skillsprint.model.Certificate;
import org.example.skillsprint.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CertificateDAO {
    private static final String COLUMNS = "certificate_id, user_id, title, issuer, issue_date, credential_link";

    public List<Certificate> listForUser(int userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM certificates WHERE user_id = ? ORDER BY issue_date IS NULL, issue_date DESC, certificate_id DESC";
        List<Certificate> certificates = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) certificates.add(fromRow(results));
            }
        }
        return certificates;
    }

    public Certificate findForUser(int certificateId, int userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM certificates WHERE certificate_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, certificateId);
            statement.setInt(2, userId);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? fromRow(results) : null;
            }
        }
    }

    public int create(Certificate certificate) throws SQLException {
        String sql = "INSERT INTO certificates (user_id, title, issuer, issue_date, credential_link) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, certificate.getUserId());
            statement.setString(2, certificate.getTitle());
            setNullableString(statement, 3, certificate.getIssuer());
            setNullableDate(statement, 4, certificate.getIssueDate());
            setNullableString(statement, 5, certificate.getCredentialLink());
            if (statement.executeUpdate() != 1) return 0;
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean update(Certificate certificate, int userId) throws SQLException {
        String sql = "UPDATE certificates SET title = ?, issuer = ?, issue_date = ?, credential_link = ? WHERE certificate_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, certificate.getTitle());
            setNullableString(statement, 2, certificate.getIssuer());
            setNullableDate(statement, 3, certificate.getIssueDate());
            setNullableString(statement, 4, certificate.getCredentialLink());
            statement.setInt(5, certificate.getCertificateId());
            statement.setInt(6, userId);
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 1) return true;

            // MySQL may report zero changed rows when the submitted values match the stored values.
            String ownedRecordSql = "SELECT 1 FROM certificates WHERE certificate_id = ? AND user_id = ?";
            try (PreparedStatement check = connection.prepareStatement(ownedRecordSql)) {
                check.setInt(1, certificate.getCertificateId());
                check.setInt(2, userId);
                try (ResultSet results = check.executeQuery()) {
                    return results.next();
                }
            }
        }
    }

    public boolean delete(int certificateId, int userId) throws SQLException {
        String sql = "DELETE FROM certificates WHERE certificate_id = ? AND user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, certificateId);
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

    private Certificate fromRow(ResultSet results) throws SQLException {
        return new Certificate(results.getInt("certificate_id"), results.getInt("user_id"),
                results.getString("title"), results.getString("issuer"),
                results.getDate("issue_date"), results.getString("credential_link"));
    }
}
