<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,org.example.skillsprint.model.Goal" %>
<%! private String esc(Object value) { if(value==null)return ""; return String.valueOf(value).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); } %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>Learning Goals - SkillSprint</title>
<style>body{font-family:Arial,sans-serif;margin:2rem;color:#222}table{border-collapse:collapse;width:100%;margin-top:1rem}th,td{border:1px solid #ccc;padding:.7rem;text-align:left;vertical-align:top}th{background:#f2f4f7}.filters{display:flex;gap:.7rem;align-items:end;flex-wrap:wrap}.filters label{display:grid;gap:.25rem}.inline{display:inline}</style></head><body><main>
<h1>Learning Goals</h1><p><a href="<%=request.getContextPath()%>/goals/new">Add goal</a> · <a href="<%=request.getContextPath()%>/dashboard">Dashboard</a></p>
<form class="filters" method="get" action="<%=request.getContextPath()%>/goals/list"><label>Status <select name="status">
<option value="" <%=request.getAttribute("selectedStatus").equals("")?"selected":""%>>All</option>
<option value="Pending" <%=request.getAttribute("selectedStatus").equals("Pending")?"selected":""%>>Pending</option>
<option value="Completed" <%=request.getAttribute("selectedStatus").equals("Completed")?"selected":""%>>Completed</option>
</select></label><button type="submit">Filter</button><a href="<%=request.getContextPath()%>/goals/list">Clear filter</a></form>
<% List<Goal> goals=(List<Goal>)request.getAttribute("goals"); if(goals==null||goals.isEmpty()){ %><p>No goals match this filter. Add a goal or clear the filter.</p><% }else{ %>
<table><thead><tr><th>Goal</th><th>Deadline</th><th>Status</th><th>Actions</th></tr></thead><tbody>
<% for(Goal goal:goals){ %><tr><td><%=esc(goal.getGoalTitle())%></td><td><%=esc(goal.getDeadline())%></td><td><%=esc(goal.getStatus())%></td><td><a href="<%=request.getContextPath()%>/goals/edit?id=<%=goal.getGoalId()%>">Edit</a>
<% if("Pending".equals(goal.getStatus())){ %><form class="inline" method="post" action="<%=request.getContextPath()%>/goals/complete"><input type="hidden" name="id" value="<%=goal.getGoalId()%>"><button type="submit">Mark Completed</button></form><% } %>
<form class="inline" method="post" action="<%=request.getContextPath()%>/goals/delete" onsubmit="return confirm('Delete this goal?');"><input type="hidden" name="id" value="<%=goal.getGoalId()%>"><button type="submit">Delete</button></form></td></tr><% } %>
</tbody></table><% } %></main></body></html>
