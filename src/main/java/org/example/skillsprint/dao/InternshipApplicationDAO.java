package org.example.skillsprint.dao;

import org.example.skillsprint.model.InternshipApplication;
import org.example.skillsprint.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InternshipApplicationDAO {
    private static final String COLUMNS = "application_id, user_id, company_name, role, application_reference, applied_date, status, notes";

    public List<InternshipApplication> listForUser(int userId, String company, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT ").append(COLUMNS)
                .append(" FROM internship_applications WHERE user_id = ?");
        boolean hasCompany = company != null && !company.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();
        if (hasCompany) sql.append(" AND company_name LIKE ?");
        if (hasStatus) sql.append(" AND status = ?");
        sql.append(" ORDER BY applied_date IS NULL, applied_date DESC, application_id DESC");
        List<InternshipApplication> applications = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setInt(i++, userId);
            if (hasCompany) ps.setString(i++, "%" + company.trim() + "%");
            if (hasStatus) ps.setString(i, status);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) applications.add(fromRow(rs)); }
        }
        return applications;
    }

    public InternshipApplication findForUser(int applicationId, int userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM internship_applications WHERE application_id = ? AND user_id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, applicationId); ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? fromRow(rs) : null; }
        }
    }

    public int create(InternshipApplication a) throws SQLException {
        String sql = "INSERT INTO internship_applications (user_id, company_name, role, application_reference, applied_date, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindFields(ps, a, true);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) return keys.getInt(1); }
        }
        return 0;
    }

    public boolean update(InternshipApplication a, int userId) throws SQLException {
        String sql = "UPDATE internship_applications SET company_name = ?, role = ?, application_reference = ?, applied_date = ?, status = ?, notes = ? WHERE application_id = ? AND user_id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getCompanyName()); ps.setString(2, a.getRole()); setNullableString(ps, 3, a.getApplicationReference());
            setNullableDate(ps, 4, a.getAppliedDate()); ps.setString(5, a.getStatus()); setNullableString(ps, 6, a.getNotes());
            ps.setInt(7, a.getApplicationId()); ps.setInt(8, userId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(int applicationId, int userId) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM internship_applications WHERE application_id = ? AND user_id = ?")) {
            ps.setInt(1, applicationId); ps.setInt(2, userId); return ps.executeUpdate() == 1;
        }
    }

    private void bindFields(PreparedStatement ps, InternshipApplication a, boolean includeUser) throws SQLException {
        ps.setInt(1, a.getUserId()); ps.setString(2, a.getCompanyName()); ps.setString(3, a.getRole());
        setNullableString(ps, 4, a.getApplicationReference()); setNullableDate(ps, 5, a.getAppliedDate());
        ps.setString(6, a.getStatus()); setNullableString(ps, 7, a.getNotes());
    }
    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.isEmpty()) ps.setNull(index, Types.VARCHAR); else ps.setString(index, value);
    }
    private void setNullableDate(PreparedStatement ps, int index, Date value) throws SQLException {
        if (value == null) ps.setNull(index, Types.DATE); else ps.setDate(index, value);
    }
    private InternshipApplication fromRow(ResultSet rs) throws SQLException {
        return new InternshipApplication(rs.getInt("application_id"), rs.getInt("user_id"), rs.getString("company_name"),
                rs.getString("role"), rs.getString("application_reference"), rs.getDate("applied_date"),
                rs.getString("status"), rs.getString("notes"));
    }
}
