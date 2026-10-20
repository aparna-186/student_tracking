package org.example.skillsprint.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.skillsprint.dao.GoalDAO;
import org.example.skillsprint.model.Goal;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class GoalServlet extends HttpServlet {
    private static final List<String> STATUSES = Arrays.asList("Pending", "Completed");
    private final GoalDAO dao = new GoalDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = authenticatedUserId(request, response);
        if (userId == null) return;
        String path = request.getPathInfo();
        try {
            if (path == null || "/".equals(path) || "/list".equals(path)) {
                String status = request.getParameter("status");
                if (status != null && !status.isEmpty() && !STATUSES.contains(status)) status = "";
                request.setAttribute("goals", status == null || status.isEmpty()
                        ? dao.listForUser(userId) : dao.listForUser(userId, status));
                request.setAttribute("statuses", STATUSES);
                request.setAttribute("selectedStatus", status == null ? "" : status);
                request.getRequestDispatcher("/WEB-INF/views/goal-list.jsp").forward(request, response);
            } else if ("/new".equals(path)) {
                Goal goal = new Goal();
                goal.setStatus("Pending");
                showForm(request, response, goal, false, null, null);
            } else if ("/edit".equals(path)) {
                Integer id = parseId(request.getParameter("id"));
                if (id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid goal ID."); return; }
                Goal goal = dao.findForUser(id, userId);
                if (goal == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Goal not found."); return; }
                showForm(request, response, goal, true, null, null);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
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
                if (edit && id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid goal ID."); return; }
                Goal goal = readForm(request, userId, id == null ? 0 : id);
                String rawDeadline = request.getParameter("deadline");
                String error = validate(goal, rawDeadline);
                if (error != null) { showForm(request, response, goal, edit, error, rawDeadline); return; }

                if (edit) {
                    if (!dao.update(goal, userId)) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Goal not found."); return; }
                } else {
                    dao.create(goal);
                }
                response.sendRedirect(request.getContextPath() + "/goals/list");
            } else if ("/complete".equals(path)) {
                Integer id = parseId(request.getParameter("id"));
                if (id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid goal ID."); return; }
                if (!dao.markCompleted(id, userId)) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Goal not found."); return; }
                response.sendRedirect(request.getContextPath() + "/goals/list");
            } else if ("/delete".equals(path)) {
                Integer id = parseId(request.getParameter("id"));
                if (id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid goal ID."); return; }
                if (!dao.delete(id, userId)) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Goal not found."); return; }
                response.sendRedirect(request.getContextPath() + "/goals/list");
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
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

    private Goal readForm(HttpServletRequest request, int userId, int goalId) {
        Goal goal = new Goal();
        goal.setGoalId(goalId);
        goal.setUserId(userId);
        goal.setGoalTitle(trim(request.getParameter("goalTitle")));
        goal.setStatus(trim(request.getParameter("status")));
        String deadline = trim(request.getParameter("deadline"));
        if (!deadline.isEmpty()) {
            try { goal.setDeadline(Date.valueOf(LocalDate.parse(deadline))); }
            catch (DateTimeParseException e) { goal.setDeadline(null); }
        }
        return goal;
    }

    private String validate(Goal goal, String rawDeadline) {
        if (goal.getGoalTitle().isEmpty()) return "Goal title is required.";
        if (goal.getGoalTitle().length() > 200) return "Goal title must be 200 characters or fewer.";
        if (!STATUSES.contains(goal.getStatus())) return "Choose Pending or Completed for status.";
        if (rawDeadline != null && !rawDeadline.trim().isEmpty() && goal.getDeadline() == null) return "Enter a valid deadline.";
        return null;
    }

    private String trim(String value) { return value == null ? "" : value.trim(); }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Goal goal,
                          boolean edit, String error, String rawDeadline) throws ServletException, IOException {
        request.setAttribute("goal", goal);
        request.setAttribute("editMode", edit);
        request.setAttribute("statuses", STATUSES);
        request.setAttribute("error", error);
        request.setAttribute("rawDeadline", rawDeadline);
        request.getRequestDispatcher("/WEB-INF/views/goal-form.jsp").forward(request, response);
    }

    private void showError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.setAttribute("error", "The goal could not be saved or loaded. Please try again.");
        request.getRequestDispatcher("/WEB-INF/views/goal-error.jsp").forward(request, response);
    }
}
