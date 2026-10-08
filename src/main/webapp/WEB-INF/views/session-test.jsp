<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Objects" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Session Test - SkillSprint</title>
</head>
<body>
<main>
    <% if (Boolean.TRUE.equals(request.getAttribute("sessionValid"))) {
        String userName = Objects.toString(request.getAttribute("userName"), "");
        userName = userName.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    %>
        <h1>Logged in successfully</h1>
        <p>User ID: <%= request.getAttribute("userId") %></p>
        <p>Welcome, <%= userName %></p>
        <form method="post" action="<%= request.getContextPath() %>/logout">
            <button type="submit">Logout</button>
        </form>
    <% } else { %>
        <p>No active session</p>
    <% } %>
</main>
</body>
</html>
