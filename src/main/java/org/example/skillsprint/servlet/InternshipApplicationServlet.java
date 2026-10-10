package org.example.skillsprint.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.skillsprint.dao.InternshipApplicationDAO;
import org.example.skillsprint.model.InternshipApplication;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class InternshipApplicationServlet extends HttpServlet {
    private static final List<String> STATUSES = Arrays.asList("Applied", "Online Assessment", "Interview", "Offer", "Rejected", "Withdrawn");
    private final InternshipApplicationDAO dao = new InternshipApplicationDAO();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = authenticatedUserId(request, response);
        if (userId == null) return;
        String path = request.getPathInfo();
        try {
            if ("/list".equals(path) || path == null || "/".equals(path)) {
                String status = request.getParameter("status");
                if (status != null && !status.isEmpty() && !STATUSES.contains(status)) status = "";
                request.setAttribute("applications", dao.listForUser(userId, request.getParameter("company"), status));
                request.setAttribute("statuses", STATUSES);
                request.getRequestDispatcher("/WEB-INF/views/application-list.jsp").forward(request, response);
            } else if ("/new".equals(path)) {
                showForm(request, response, new InternshipApplication(), false, null);
            } else if ("/edit".equals(path)) {
                Integer id = parseId(request.getParameter("id"));
                if (id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid application ID."); return; }
                InternshipApplication app = dao.findForUser(id, userId);
                if (app == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Application not found."); return; }
                showForm(request, response, app, true, null);
            } else response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (SQLException e) { showError(request, response); }
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Integer userId = authenticatedUserId(request, response);
        if (userId == null) return;
        String path = request.getPathInfo();
        try {
            if ("/create".equals(path) || "/update".equals(path)) {
                boolean edit = "/update".equals(path);
                Integer id = edit ? parseId(request.getParameter("id")) : null;
                if (edit && id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid application ID."); return; }
                InternshipApplication app = readForm(request, userId, id == null ? 0 : id);
                String error = validate(app, request.getParameter("appliedDate"));
                if (error != null) { showForm(request, response, app, edit, error); return; }
                if (edit) {
                    if (!dao.update(app, userId)) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Application not found."); return; }
                } else dao.create(app);
                response.sendRedirect(request.getContextPath() + "/applications/list");
            } else if ("/delete".equals(path)) {
                Integer id = parseId(request.getParameter("id"));
                if (id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid application ID."); return; }
                if (!dao.delete(id, userId)) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Application not found."); return; }
                response.sendRedirect(request.getContextPath() + "/applications/list");
            } else response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (SQLException e) { showError(request, response); }
    }

    private Integer authenticatedUserId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute("userId");
        if (!(value instanceof Number)) { response.sendRedirect(request.getContextPath() + "/login"); return null; }
        int id = ((Number) value).intValue();
        if (id <= 0) { response.sendRedirect(request.getContextPath() + "/login"); return null; }
        return id;
    }
    private Integer parseId(String value) {
        if (value == null || !value.matches("[1-9][0-9]{0,9}")) return null;
        try { int id = Integer.parseInt(value); return id > 0 ? id : null; } catch (NumberFormatException e) { return null; }
    }
    private InternshipApplication readForm(HttpServletRequest request, int userId, int id) {
        InternshipApplication app = new InternshipApplication(); app.setApplicationId(id); app.setUserId(userId);
        app.setCompanyName(trim(request.getParameter("companyName"))); app.setRole(trim(request.getParameter("role")));
        app.setApplicationReference(trim(request.getParameter("applicationReference")));
        app.setNotes(trim(request.getParameter("notes"))); app.setStatus(trim(request.getParameter("status")));
        String date = trim(request.getParameter("appliedDate"));
        if (!date.isEmpty()) try { app.setAppliedDate(Date.valueOf(LocalDate.parse(date))); } catch (DateTimeParseException e) { app.setAppliedDate(null); }
        return app;
    }
    private String validate(InternshipApplication app, String rawDate) {
        if (app.getCompanyName().isEmpty() || app.getRole().isEmpty()) return "Company name and role are required.";
        if (app.getCompanyName().length() > 100 || app.getRole().length() > 100) return "Company name and role must be 100 characters or fewer.";
        if (app.getApplicationReference() != null && app.getApplicationReference().length() > 100) return "External reference must be 100 characters or fewer.";
        if (app.getNotes() != null && app.getNotes().length() > 500) return "Notes must be 500 characters or fewer.";
        if (!STATUSES.contains(app.getStatus())) return "Choose a valid application status.";
        if (rawDate != null && !rawDate.trim().isEmpty() && app.getAppliedDate() == null) return "Enter a valid applied date.";
        return null;
    }
    private String trim(String value) { return value == null ? "" : value.trim(); }
    private void showForm(HttpServletRequest request, HttpServletResponse response, InternshipApplication app, boolean edit, String error) throws ServletException, IOException {
        request.setAttribute("application", app); request.setAttribute("editMode", edit);
        request.setAttribute("statuses", STATUSES); request.setAttribute("error", error);
        request.getRequestDispatcher("/WEB-INF/views/application-form.jsp").forward(request, response);
    }
    private void showError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.setAttribute("error", "The application could not be saved or loaded. Please try again.");
        request.getRequestDispatcher("/WEB-INF/views/application-error.jsp").forward(request, response);
    }
}
