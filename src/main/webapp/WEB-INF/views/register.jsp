<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - SkillSprint</title>
</head>
<body>
<main>
    <h1>Create your SkillSprint account</h1>

    <% if (request.getAttribute("error") != null) { %>
        <p role="alert"><%= request.getAttribute("error") %></p>
    <% } %>

    <% if (request.getAttribute("success") != null) { %>
        <p role="status"><%= request.getAttribute("success") %></p>
        <p><a href="<%= request.getContextPath() %>/login">Continue to login</a></p>
    <% } else { %>
        <form method="post" action="<%= request.getContextPath() %>/register">
            <div>
                <label for="fullName">Full Name</label>
                <input type="text" id="fullName" name="fullName" maxlength="100" required>
            </div>
            <div>
                <label for="email">Email</label>
                <input type="email" id="email" name="email" maxlength="100" required>
            </div>
            <div>
                <label for="college">College (optional)</label>
                <input type="text" id="college" name="college" maxlength="150">
            </div>
            <div>
                <label for="branch">Branch (optional)</label>
                <input type="text" id="branch" name="branch" maxlength="100">
            </div>
            <div>
                <label for="githubUrl">GitHub URL (optional)</label>
                <input type="url" id="githubUrl" name="githubUrl" maxlength="255">
            </div>
            <div>
                <label for="linkedinUrl">LinkedIn URL (optional)</label>
                <input type="url" id="linkedinUrl" name="linkedinUrl" maxlength="255">
            </div>
            <div>
                <label for="password">Password</label>
                <input type="password" id="password" name="password" minlength="8"
                       maxlength="128" required>
            </div>
            <div>
                <label for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword"
                       minlength="8" maxlength="128" required>
            </div>
            <button type="submit">Register</button>
        </form>
    <% } %>
</main>
</body>
</html>
