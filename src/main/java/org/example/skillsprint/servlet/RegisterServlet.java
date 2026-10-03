package org.example.skillsprint.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.skillsprint.dao.UserDAO;
import org.example.skillsprint.model.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegisterServlet extends HttpServlet {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int HASH_ITERATIONS = 210_000;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int HASH_LENGTH_BITS = 256;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String fullName = trim(request.getParameter("fullName"));
        String email = trim(request.getParameter("email")).toLowerCase(Locale.ROOT);
        String college = trim(request.getParameter("college"));
        String branch = trim(request.getParameter("branch"));
        String githubUrl = trim(request.getParameter("githubUrl"));
        String linkedinUrl = trim(request.getParameter("linkedinUrl"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        String validationError = validate(fullName, email, password, confirmPassword,
                college, branch, githubUrl, linkedinUrl);
        if (validationError != null) {
            showMessage(request, response, "error", validationError);
            return;
        }

        try {
            if (userDAO.emailExists(email)) {
                showMessage(request, response, "error",
                        "An account with this email already exists.");
                return;
            }

            String passwordHash = hashPassword(password);
            User user = new User(fullName, email, passwordHash,
                    emptyToNull(college), emptyToNull(branch),
                    emptyToNull(githubUrl), emptyToNull(linkedinUrl));
            if (!userDAO.createUser(user)) {
                showMessage(request, response, "error",
                        "An account with this email already exists.");
                return;
            }

            showMessage(request, response, "success",
                    "Registration successful. You can now continue to login.");
        } catch (SQLException e) {
            getServletContext().log("Registration failed because of a database error.", e);
            showMessage(request, response, "error",
                    "Registration could not be completed right now. Please try again.");
        } catch (GeneralSecurityException e) {
            getServletContext().log("Password hashing failed during registration.", e);
            showMessage(request, response, "error",
                    "Registration could not be completed right now. Please try again.");
        }
    }

    private String validate(String fullName, String email, String password, String confirmPassword,
                            String college, String branch, String githubUrl, String linkedinUrl) {
        if (fullName.isEmpty()) {
            return "Full name is required.";
        }
        if (fullName.length() > 100) {
            return "Full name must be 100 characters or fewer.";
        }
        if (email.isEmpty()) {
            return "Email is required.";
        }
        if (email.length() > 100 || !EMAIL_PATTERN.matcher(email).matches()) {
            return "Enter a valid email address.";
        }
        if (password == null || password.isEmpty()) {
            return "Password is required.";
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least 8 characters long.";
        }
        if (password.length() > 128) {
            return "Password must be 128 characters or fewer.";
        }
        if (confirmPassword == null || !password.equals(confirmPassword)) {
            return "Password and confirm password must match.";
        }
        if (college.length() > 150) {
            return "College must be 150 characters or fewer.";
        }
        if (branch.length() > 100) {
            return "Branch must be 100 characters or fewer.";
        }
        if (!isBlankOrHttpUrl(githubUrl)) {
            return "Enter a valid GitHub URL beginning with http:// or https://.";
        }
        if (!isBlankOrHttpUrl(linkedinUrl)) {
            return "Enter a valid LinkedIn URL beginning with http:// or https://.";
        }
        return null;
    }

    private boolean isBlankOrHttpUrl(String value) {
        if (value.isEmpty()) {
            return true;
        }
        if (value.length() > 255) {
            return false;
        }
        try {
            URI uri = URI.create(value);
            String scheme = uri.getScheme();
            return uri.isAbsolute()
                    && uri.getHost() != null
                    && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String hashPassword(String password) throws GeneralSecurityException {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(salt);

        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                HASH_ITERATIONS, HASH_LENGTH_BITS);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(keySpec)
                    .getEncoded();
            return "pbkdf2-sha256$" + HASH_ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);
        } finally {
            keySpec.clearPassword();
        }
    }

    private void showMessage(HttpServletRequest request, HttpServletResponse response,
                             String messageType, String message)
            throws ServletException, IOException {
        request.setAttribute(messageType, message);
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String emptyToNull(String value) {
        return value.isEmpty() ? null : value;
    }
}
