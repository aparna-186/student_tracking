package org.example.skillsprint.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.skillsprint.dao.DashboardDAO;
import org.example.skillsprint.model.DashboardSummary;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardServlet extends HttpServlet {
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Object sessionUserId = session.getAttribute("userId");
        if (!(sessionUserId instanceof Number)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = ((Number) sessionUserId).intValue();
        try {
            DashboardSummary summary = dashboardDAO.getSummaryForUser(userId);
            request.setAttribute("dashboardSummary", summary);
            request.setAttribute("userName", session.getAttribute("userName"));
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Unable to load the dashboard summary.", e);
        }
    }
}
