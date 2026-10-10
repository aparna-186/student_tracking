<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,org.example.skillsprint.model.InternshipApplication" %>
<%! private String esc(Object value) { if (value == null) return ""; return String.valueOf(value).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;"); } %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>Applications - SkillSprint</title>
<style>body{font-family:Arial,sans-serif;margin:2rem;color:#222}table{border-collapse:collapse;width:100%;margin-top:1rem}th,td{border:1px solid #ccc;padding:.65rem;text-align:left;vertical-align:top}th{background:#f2f4f7}form.inline{display:inline}.filters{display:flex;gap:.6rem;align-items:end;flex-wrap:wrap}.filters label{display:grid;gap:.25rem}a,button{cursor:pointer}</style></head><body>
<main><h1>Internship Applications</h1><p><a href="<%=request.getContextPath()%>/applications/new">Add application</a> · <a href="<%=request.getContextPath()%>/dashboard">Dashboard</a></p>
<form class="filters" method="get" action="<%=request.getContextPath()%>/applications/list">
<label>Company search <input type="search" name="company" maxlength="100" value="<%=esc(request.getParameter("company"))%>"></label>
<label>Status <select name="status"><option value="">All statuses</option><% for(String s : (List<String>)request.getAttribute("statuses")) { %><option value="<%=esc(s)%>" <%=s.equals(request.getParameter("status"))?"selected":""%>><%=esc(s)%></option><% } %></select></label>
<button type="submit">Search</button><a href="<%=request.getContextPath()%>/applications/list">Clear filters</a></form>
<% List<InternshipApplication> apps=(List<InternshipApplication>)request.getAttribute("applications"); if(apps==null || apps.isEmpty()) { %><p>No applications match these filters. Add an application or clear the filters.</p><% } else { %>
<table><thead><tr><th>Company</th><th>Role</th><th>External reference</th><th>Applied date</th><th>Status</th><th>Notes</th><th>Actions</th></tr></thead><tbody>
<% for(InternshipApplication a:apps) { %><tr><td><%=esc(a.getCompanyName())%></td><td><%=esc(a.getRole())%></td><td><%=esc(a.getApplicationReference())%></td><td><%=esc(a.getAppliedDate())%></td><td><%=esc(a.getStatus())%></td><td><%=esc(a.getNotes())%></td><td><a href="<%=request.getContextPath()%>/applications/edit?id=<%=a.getApplicationId()%>">Edit</a>
<form class="inline" method="post" action="<%=request.getContextPath()%>/applications/delete" onsubmit="return confirm('Delete this application?');"><input type="hidden" name="id" value="<%=a.getApplicationId()%>"><button type="submit">Delete</button></form></td></tr><% } %>
</tbody></table><% } %></main></body></html>
