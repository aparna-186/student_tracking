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
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Locale;
import java.util.regex.Pattern;

public class LoginServlet extends HttpServlet {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        email = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        String password = request.getParameter("password");

        if (email.isEmpty() || password == null || password.isEmpty()
                || email.length() > 100 || !EMAIL_PATTERN.matcher(email).matches()) {
            show(request, response, "Invalid email or password", false);
            return;
        }

        try {
            User user = userDAO.findByEmail(email);
            if (user != null && verifyPassword(password, user.getPasswordHash())) {
                show(request, response, "Login successful.", true);
            } else {
                show(request, response, "Invalid email or password", false);
            }
        } catch (SQLException e) {
            getServletContext().log("Login failed because of a database error.", e);
            show(request, response, "Login could not be completed right now. Please try again.", false);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            getServletContext().log("Password verification failed during login.", e);
            show(request, response, "Login could not be completed right now. Please try again.", false);
        }
    }

    private boolean verifyPassword(String password, String encoded) throws GeneralSecurityException {
        if (encoded == null) return false;
        String[] parts = encoded.split("\\$", -1);
        if (parts.length != 4 || !"pbkdf2-sha256".equals(parts[0])) return false;
        int iterations = Integer.parseInt(parts[1]);
        byte[] salt = Base64.getDecoder().decode(parts[2]);
        byte[] expected = Base64.getDecoder().decode(parts[3]);
        if (iterations < 1 || salt.length == 0 || expected.length == 0) return false;
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                iterations, expected.length * 8);
        try {
            byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(keySpec).getEncoded();
            return MessageDigest.isEqual(expected, actual);
        } finally {
            keySpec.clearPassword();
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response,
                      String message, boolean success) throws ServletException, IOException {
        request.setAttribute("message", message);
        request.setAttribute("success", success);
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
}
