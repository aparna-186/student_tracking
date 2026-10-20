<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.example.skillsprint.model.DashboardSummary" %>
<% request.setAttribute("pageTitle", "Dashboard"); request.setAttribute("pageSubtitle", "Welcome back, " + String.valueOf(session.getAttribute("userName")) + ". Here is the progress you are making."); request.setAttribute("activePage", "dashboard"); DashboardSummary summary=(DashboardSummary)request.getAttribute("dashboardSummary"); %>
<%@ include file="_app-header.jspf" %>
<section class="summary-grid" aria-label="Career tracking summary">
    <article class="metric-card"><p class="metric-label">Applications</p><p class="metric-value"><%=summary.getTotalApplications()%></p></article>
    <article class="metric-card"><p class="metric-label">Interviews</p><p class="metric-value"><%=summary.getTotalInterviews()%></p></article>
    <article class="metric-card"><p class="metric-label">Offers</p><p class="metric-value"><%=summary.getTotalOffers()%></p></article>
    <article class="metric-card"><p class="metric-label">Rejected</p><p class="metric-value"><%=summary.getTotalRejected()%></p></article>
    <article class="metric-card"><p class="metric-label">Coding problems</p><p class="metric-value"><%=summary.getTotalCoding()%></p></article>
    <article class="metric-card"><p class="metric-label">Certificates</p><p class="metric-value"><%=summary.getTotalCertificates()%></p></article>
    <article class="metric-card"><p class="metric-label">Completed goals</p><p class="metric-value"><%=summary.getCompletedGoals()%></p></article>
    <article class="metric-card"><p class="metric-label">Pending goals</p><p class="metric-value"><%=summary.getPendingGoals()%></p></article>
</section>
<section class="section-spaced" aria-labelledby="shortcuts-title"><h2 class="panel-title" id="shortcuts-title">Your workspace</h2>
<div class="shortcut-grid">
    <a class="shortcut-card" href="<%=request.getContextPath()%>/applications/list"><span class="shortcut-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><rect x="4" y="4" width="16" height="17" rx="2"/><path d="M8 4V2m8 2V2M8 10h8"/></svg></span><span><strong>Internship applications</strong><span>Track roles, stages, and follow-ups.</span></span></a>
    <a class="shortcut-card" href="<%=request.getContextPath()%>/coding/list"><span class="shortcut-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M8 5 3 12l5 7m8-14 5 7-5 7m-2-16-4 20"/></svg></span><span><strong>Coding progress</strong><span>Keep your practice organized.</span></span></a>
    <a class="shortcut-card" href="<%=request.getContextPath()%>/certificates/list"><span class="shortcut-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M7 3h8l4 4v14H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2zm8 0v5h4"/></svg></span><span><strong>Certificates</strong><span>Keep credentials easy to find.</span></span></a>
    <a class="shortcut-card" href="<%=request.getContextPath()%>/goals/list"><span class="shortcut-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="m8 12 2.5 2.5L16 9"/></svg></span><span><strong>Learning goals</strong><span>Turn your next milestone into a plan.</span></span></a>
    <a class="shortcut-card" href="<%=request.getContextPath()%>/profile"><span class="shortcut-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/></svg></span><span><strong>Student profile</strong><span>Keep your details up to date.</span></span></a>
</div></section>
<%@ include file="_app-footer.jspf" %>
