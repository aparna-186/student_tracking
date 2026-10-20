package org.example.skillsprint.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.skillsprint.dao.CodingProgressDAO;
import org.example.skillsprint.model.CodingProgress;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class CodingProgressServlet extends HttpServlet {
    private static final List<String> DIFFICULTIES = Arrays.asList("Easy", "Medium", "Hard");
    private static final List<String> STATUSES = Arrays.asList("Solved", "Attempted", "Unsolved");
    private final CodingProgressDAO dao = new CodingProgressDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = authenticatedUserId(request, response);
        if (userId == null) return;
        String path = request.getPathInfo();
        try {
            if (path == null || "/".equals(path) || "/list".equals(path)) {
                String difficulty = request.getParameter("difficulty");
                String status = request.getParameter("status");
                if (difficulty != null && !difficulty.isEmpty() && !DIFFICULTIES.contains(difficulty)) difficulty = "";
                if (status != null && !status.isEmpty() && !STATUSES.contains(status)) status = "";
                String platform = trim(request.getParameter("platform"));
                if (platform.length() > 50) platform = platform.substring(0, 50);
                request.setAttribute("records", dao.listForUser(userId, platform, difficulty, status));
                request.setAttribute("difficulties", DIFFICULTIES);
                request.setAttribute("statuses", STATUSES);
                request.getRequestDispatcher("/WEB-INF/views/coding-list.jsp").forward(request, response);
            } else if ("/new".equals(path)) {
                showForm(request, response, new CodingProgress(), false, null, null);
            } else if ("/edit".equals(path)) {
                Integer id = parseId(request.getParameter("id"));
                if (id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid coding record ID."); return; }
                CodingProgress record = dao.findForUser(id, userId);
                if (record == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Coding record not found."); return; }
                showForm(request, response, record, true, null, null);
            } else response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (SQLException e) {
            showError(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Integer userId = authenticatedUserId(request, response);
        if (userId == null) return;
        String path = request.getPathInfo();
        try {
            if ("/create".equals(path) || "/update".equals(path)) {
                boolean edit = "/update".equals(path);
                Integer id = edit ? parseId(request.getParameter("id")) : null;
                if (edit && id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid coding record ID."); return; }
                CodingProgress record = readForm(request, userId, id == null ? 0 : id);
                String rawDate = request.getParameter("solvedDate");
                String error = validate(record, rawDate);
                if (error != null) { showForm(request, response, record, edit, error, rawDate); return; }
                if (edit) {
                    if (!dao.update(record, userId)) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Coding record not found."); return; }
                } else dao.create(record);
                response.sendRedirect(request.getContextPath() + "/coding/list");
            } else if ("/delete".equals(path)) {
                Integer id = parseId(request.getParameter("id"));
                if (id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid coding record ID."); return; }
                if (!dao.delete(id, userId)) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Coding record not found."); return; }
                response.sendRedirect(request.getContextPath() + "/coding/list");
            } else response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (SQLException e) {
            showError(request, response);
        }
    }

    private Integer authenticatedUserId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute("userId");
        if (!(value instanceof Number) || ((Number) value).intValue() <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return ((Number) value).intValue();
    }

    private Integer parseId(String value) {
        if (value == null || !value.matches("[1-9][0-9]{0,9}")) return null;
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { return null; }
    }

    private CodingProgress readForm(HttpServletRequest request, int userId, int id) {
        CodingProgress record = new CodingProgress();
        record.setCodingId(id);
        record.setUserId(userId);
        record.setPlatform(trim(request.getParameter("platform")));
        record.setProblemName(trim(request.getParameter("problemName")));
        record.setDifficulty(trim(request.getParameter("difficulty")));
        record.setProblemStatus(trim(request.getParameter("problemStatus")));
        record.setProblemUrl(trim(request.getParameter("problemUrl")));
        String date = trim(request.getParameter("solvedDate"));
        if (!date.isEmpty()) {
            try { record.setSolvedDate(Date.valueOf(LocalDate.parse(date))); }
            catch (DateTimeParseException e) { record.setSolvedDate(null); }
        }
        return record;
    }

    private String validate(CodingProgress record, String rawDate) {
        if (record.getProblemName().isEmpty()) return "Problem name is required.";
        if (record.getProblemName().length() > 150) return "Problem name must be 150 characters or fewer.";
        if (record.getPlatform() != null && record.getPlatform().length() > 50) return "Platform must be 50 characters or fewer.";
        if (!DIFFICULTIES.contains(record.getDifficulty())) return "Choose Easy, Medium or Hard for difficulty.";
        if (!STATUSES.contains(record.getProblemStatus())) return "Choose a valid problem status.";
        if (rawDate != null && !rawDate.trim().isEmpty() && record.getSolvedDate() == null) return "Enter a valid solved date.";
        if (record.getProblemUrl() != null && record.getProblemUrl().length() > 255) return "Problem URL must be 255 characters or fewer.";
        if (record.getProblemUrl() != null && !record.getProblemUrl().isEmpty() && !isHttpUrl(record.getProblemUrl())) {
            return "Problem URL must be a valid http or https URL.";
        }
        return null;
    }

    private boolean isHttpUrl(String value) {
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            return scheme != null && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && uri.getHost() != null && !uri.getHost().isEmpty();
        } catch (URISyntaxException e) { return false; }
    }

    private String trim(String value) { return value == null ? "" : value.trim(); }

    private void showForm(HttpServletRequest request, HttpServletResponse response, CodingProgress record,
                          boolean edit, String error, String rawDate) throws ServletException, IOException {
        request.setAttribute("record", record);
        request.setAttribute("editMode", edit);
        request.setAttribute("difficulties", DIFFICULTIES);
        request.setAttribute("statuses", STATUSES);
        request.setAttribute("error", error);
        request.setAttribute("rawDate", rawDate);
        request.getRequestDispatcher("/WEB-INF/views/coding-form.jsp").forward(request, response);
    }

    private void showError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.setAttribute("error", "The coding record could not be saved or loaded. Please try again.");
        request.getRequestDispatcher("/WEB-INF/views/coding-error.jsp").forward(request, response);
    }
}
