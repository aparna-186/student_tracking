<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login - SkillSprint</title>
</head>
<body>
<main>
    <h1>Login to SkillSprint</h1>
    <% if (request.getAttribute("message") != null) { %>
        <p role="<%= Boolean.TRUE.equals(request.getAttribute("success")) ? "status" : "alert" %>"><%= request.getAttribute("message") %></p>
    <% } %>
    <form method="post" action="<%= request.getContextPath() %>/login">
        <div>
            <label for="email">Email</label>
            <input type="email" id="email" name="email" maxlength="100" required>
        </div>
        <div>
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
        </div>
        <button type="submit">Login</button>
    </form>
</main>
</body>
</html>
