<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard - SkillSprint</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 2rem; }
        .summary { display: grid; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); gap: 1rem; }
        .card { border: 1px solid #ccc; border-radius: 6px; padding: 1rem; }
        .card h2 { font-size: 1rem; margin-top: 0; }
        .count { font-size: 1.6rem; margin-bottom: 0; }
        nav { display: flex; gap: 1rem; flex-wrap: wrap; margin-top: 2rem; }
    </style>
</head>
<body>
<main>
    <h1>Welcome, ${userName}</h1>
    <section class="summary" aria-label="Career tracking summary">
        <article class="card"><h2>Applications</h2><p class="count">${dashboardSummary.totalApplications}</p></article>
        <article class="card"><h2>Interviews</h2><p class="count">${dashboardSummary.totalInterviews}</p></article>
        <article class="card"><h2>Offers</h2><p class="count">${dashboardSummary.totalOffers}</p></article>
        <article class="card"><h2>Rejected</h2><p class="count">${dashboardSummary.totalRejected}</p></article>
        <article class="card"><h2>Coding Problems</h2><p class="count">${dashboardSummary.totalCoding}</p></article>
        <article class="card"><h2>Certificates</h2><p class="count">${dashboardSummary.totalCertificates}</p></article>
        <article class="card"><h2>Completed Goals</h2><p class="count">${dashboardSummary.completedGoals}</p></article>
        <article class="card"><h2>Pending Goals</h2><p class="count">${dashboardSummary.pendingGoals}</p></article>
    </section>
    <nav aria-label="Application navigation">
        <a href="${pageContext.request.contextPath}/applications/list">Applications</a>
        <span>Coding</span>
        <span>Certificates</span>
        <span>Goals</span>
        <span>Profile</span>
        <form method="post" action="${pageContext.request.contextPath}/logout">
            <button type="submit">Logout</button>
        </form>
    </nav>
</main>
</body>
</html>
