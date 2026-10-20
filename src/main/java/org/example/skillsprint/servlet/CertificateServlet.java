package org.example.skillsprint.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.skillsprint.dao.CertificateDAO;
import org.example.skillsprint.model.Certificate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CertificateServlet extends HttpServlet {
    private final CertificateDAO dao = new CertificateDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = authenticatedUserId(request, response);
        if (userId == null) return;
        String path = request.getPathInfo();
        try {
            if (path == null || "/".equals(path) || "/list".equals(path)) {
                request.setAttribute("certificates", dao.listForUser(userId));
                request.getRequestDispatcher("/WEB-INF/views/certificate-list.jsp").forward(request, response);
            } else if ("/new".equals(path)) {
                showForm(request, response, new Certificate(), false, null, null);
            } else if ("/edit".equals(path)) {
                Integer id = parseId(request.getParameter("id"));
                if (id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid certificate ID."); return; }
                Certificate certificate = dao.findForUser(id, userId);
                if (certificate == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found."); return; }
                showForm(request, response, certificate, true, null, null);
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
                if (edit && id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid certificate ID."); return; }
                Certificate certificate = readForm(request, userId, id == null ? 0 : id);
                String rawDate = request.getParameter("issueDate");
                String error = validate(certificate, rawDate);
                if (error != null) { showForm(request, response, certificate, edit, error, rawDate); return; }

                if (edit) {
                    if (!dao.update(certificate, userId)) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found."); return; }
                } else {
                    dao.create(certificate);
                }
                response.sendRedirect(request.getContextPath() + "/certificates/list");
            } else if ("/delete".equals(path)) {
                Integer id = parseId(request.getParameter("id"));
                if (id == null) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid certificate ID."); return; }
                if (!dao.delete(id, userId)) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found."); return; }
                response.sendRedirect(request.getContextPath() + "/certificates/list");
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

    private Certificate readForm(HttpServletRequest request, int userId, int certificateId) {
        Certificate certificate = new Certificate();
        certificate.setCertificateId(certificateId);
        certificate.setUserId(userId);
        certificate.setTitle(trim(request.getParameter("title")));
        certificate.setIssuer(trim(request.getParameter("issuer")));
        certificate.setCredentialLink(trim(request.getParameter("credentialLink")));
        String date = trim(request.getParameter("issueDate"));
        if (!date.isEmpty()) {
            try { certificate.setIssueDate(Date.valueOf(LocalDate.parse(date))); }
            catch (DateTimeParseException e) { certificate.setIssueDate(null); }
        }
        return certificate;
    }

    private String validate(Certificate certificate, String rawDate) {
        if (certificate.getTitle().isEmpty()) return "Certificate title is required.";
        if (certificate.getTitle().length() > 150) return "Certificate title must be 150 characters or fewer.";
        if (certificate.getIssuer() != null && certificate.getIssuer().length() > 100) return "Issuer must be 100 characters or fewer.";
        if (certificate.getCredentialLink() != null && certificate.getCredentialLink().length() > 500) return "Credential URL must be 500 characters or fewer.";
        if (rawDate != null && !rawDate.trim().isEmpty() && certificate.getIssueDate() == null) return "Enter a valid issue date.";
        if (certificate.getCredentialLink() != null && !certificate.getCredentialLink().isEmpty()
                && !isHttpUrl(certificate.getCredentialLink())) {
            return "Credential URL must be a valid HTTP or HTTPS URL.";
        }
        return null;
    }

    private boolean isHttpUrl(String value) {
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            return scheme != null && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && uri.getHost() != null && !uri.getHost().isEmpty();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private String trim(String value) { return value == null ? "" : value.trim(); }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Certificate certificate,
                          boolean edit, String error, String rawDate) throws ServletException, IOException {
        request.setAttribute("certificate", certificate);
        request.setAttribute("editMode", edit);
        request.setAttribute("error", error);
        request.setAttribute("rawDate", rawDate);
        request.getRequestDispatcher("/WEB-INF/views/certificate-form.jsp").forward(request, response);
    }

    private void showError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.setAttribute("error", "The certificate could not be saved or loaded. Please try again.");
        request.getRequestDispatcher("/WEB-INF/views/certificate-error.jsp").forward(request, response);
    }
}
