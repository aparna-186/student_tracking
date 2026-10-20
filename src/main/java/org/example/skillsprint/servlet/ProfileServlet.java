package org.example.skillsprint.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.skillsprint.dao.UserDAO;
import org.example.skillsprint.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Pattern;

public class ProfileServlet extends HttpServlet {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = authenticatedUserId(session, request, response);
        if (userId == null) return;

        Object success = session.getAttribute("profileSuccess");
        if (success != null) {
            request.setAttribute("success", success);
            session.removeAttribute("profileSuccess");
        }
        try {
            User profile = userDAO.findProfileById(userId);
            if (profile == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Profile not found.");
                return;
            }
            request.setAttribute("profile", profile);
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
        } catch (SQLException e) {
            getServletContext().log("Profile could not be loaded because of a database error.", e);
            showError(request, response, "Your profile could not be loaded right now. Please try again.", null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Integer userId = authenticatedUserId(session, request, response);
        if (userId == null) return;

        User submitted = readProfile(request, userId);
        String error = validate(submitted);
        if (error != null) {
            showError(request, response, error, submitted);
            return;
        }

        try {
            if (userDAO.emailExistsForAnotherUser(submitted.getEmail(), userId)) {
                showError(request, response, "That email address is already used by another account.", submitted);
                return;
            }
            if (!userDAO.updateProfile(submitted)) {
                if (userDAO.emailExistsForAnotherUser(submitted.getEmail(), userId)) {
                    showError(request, response, "That email address is already used by another account.", submitted);
                } else {
                    showError(request, response, "Your profile could not be updated. Please try again.", submitted);
                }
                return;
            }

            session.setAttribute("userName", submitted.getFullName());
            session.setAttribute("profileSuccess", "Your profile was saved successfully.");
            response.sendRedirect(request.getContextPath() + "/profile");
        } catch (SQLException e) {
            getServletContext().log("Profile could not be saved because of a database error.", e);
            showError(request, response, "Your profile could not be saved right now. Please try again.", submitted);
        }
    }

    private Integer authenticatedUserId(HttpSession session, HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
        Object value = session == null ? null : session.getAttribute("userId");
        if (!(value instanceof Number) || ((Number) value).intValue() <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return ((Number) value).intValue();
    }

    private User readProfile(HttpServletRequest request, int userId) {
        String fullName = trim(request.getParameter("fullName"));
        String email = trim(request.getParameter("email")).toLowerCase(Locale.ROOT);
        String college = trim(request.getParameter("college"));
        String branch = trim(request.getParameter("branch"));
        String githubUrl = trim(request.getParameter("githubUrl"));
        String linkedinUrl = trim(request.getParameter("linkedinUrl"));
        return new User(userId, fullName, email, null, emptyToNull(college), emptyToNull(branch),
                emptyToNull(githubUrl), emptyToNull(linkedinUrl));
    }

    private String validate(User user) {
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) return "Full name is required.";
        if (user.getFullName().length() > 100) return "Full name must be 100 characters or fewer.";
        if (user.getEmail() == null || user.getEmail().isEmpty()) return "Email is required.";
        if (user.getEmail().length() > 100 || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return "Enter a valid email address (100 characters or fewer).";
        }
        if (length(user.getCollege()) > 150) return "College must be 150 characters or fewer.";
        if (length(user.getBranch()) > 100) return "Branch must be 100 characters or fewer.";
        if (length(user.getGithubUrl()) > 255) return "GitHub URL must be 255 characters or fewer.";
        if (length(user.getLinkedinUrl()) > 255) return "LinkedIn URL must be 255 characters or fewer.";
        if (user.getGithubUrl() != null && !isHttpUrl(user.getGithubUrl())) return "Enter a valid HTTP or HTTPS GitHub URL.";
        if (user.getLinkedinUrl() != null && !isHttpUrl(user.getLinkedinUrl())) return "Enter a valid HTTP or HTTPS LinkedIn URL.";
        return null;
    }

    private boolean isHttpUrl(String value) {
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            return uri.isAbsolute() && uri.getHost() != null && !uri.getHost().isEmpty()
                    && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme));
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private int length(String value) { return value == null ? 0 : value.length(); }
    private String trim(String value) { return value == null ? "" : value.trim(); }
    private String emptyToNull(String value) { return value.isEmpty() ? null : value; }

    private void showError(HttpServletRequest request, HttpServletResponse response,
                           String message, User profile) throws ServletException, IOException {
        request.setAttribute("error", message);
        request.setAttribute("profile", profile);
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
}
