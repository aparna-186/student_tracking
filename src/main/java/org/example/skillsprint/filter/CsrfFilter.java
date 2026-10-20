package org.example.skillsprint.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class CsrfFilter implements Filter {
    private static final String SESSION_TOKEN_ATTRIBUTE = CsrfFilter.class.getName() + ".token";
    private static final String REQUEST_TOKEN_ATTRIBUTE = "csrfToken";
    private static final String TOKEN_PARAMETER = "_csrf";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        String method = httpRequest.getMethod();

        if (isSafeMethod(method)) {
            if (session != null && session.getAttribute("userId") != null) {
                String token = getOrCreateToken(session);
                httpRequest.setAttribute(REQUEST_TOKEN_ATTRIBUTE, token);
            }
            chain.doFilter(request, response);
            return;
        }

        if (hasCsrfQueryParameter(httpRequest.getQueryString())) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing or invalid CSRF token.");
            return;
        }

        httpRequest.setCharacterEncoding("UTF-8");
        String expectedToken = session == null ? null : (String) session.getAttribute(SESSION_TOKEN_ATTRIBUTE);
        String submittedToken = httpRequest.getParameter(TOKEN_PARAMETER);
        if (submittedToken == null || submittedToken.isEmpty()) {
            submittedToken = httpRequest.getHeader("X-CSRF-Token");
        }
        if (expectedToken == null || submittedToken == null
                || !MessageDigest.isEqual(expectedToken.getBytes(StandardCharsets.UTF_8),
                submittedToken.getBytes(StandardCharsets.UTF_8))) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing or invalid CSRF token.");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean hasCsrfQueryParameter(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return false;
        }

        for (String parameter : queryString.split("&", -1)) {
            int equalsIndex = parameter.indexOf('=');
            String encodedName = equalsIndex >= 0 ? parameter.substring(0, equalsIndex) : parameter;
            try {
                if (TOKEN_PARAMETER.equals(URLDecoder.decode(encodedName, StandardCharsets.UTF_8.name()))) {
                    return true;
                }
            } catch (IllegalArgumentException | java.io.UnsupportedEncodingException ignored) {
                // A malformed or unrelated parameter name is not a CSRF token parameter.
            }
        }
        return false;
    }

    private boolean isSafeMethod(String method) {
        return "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)
                || "OPTIONS".equalsIgnoreCase(method);
    }

    private String getOrCreateToken(HttpSession session) {
        synchronized (session) {
            Object existing = session.getAttribute(SESSION_TOKEN_ATTRIBUTE);
            if (existing instanceof String && !((String) existing).isEmpty()) {
                return (String) existing;
            }
            byte[] randomBytes = new byte[32];
            SECURE_RANDOM.nextBytes(randomBytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
            session.setAttribute(SESSION_TOKEN_ATTRIBUTE, token);
            return token;
        }
    }
}
