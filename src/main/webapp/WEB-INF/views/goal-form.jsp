<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,org.example.skillsprint.model.Goal" %>
<%! private String esc(Object value) { if(value==null)return ""; return String.valueOf(value).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); } %>
<% Goal goal=(Goal)request.getAttribute("goal"); if(goal==null)goal=new Goal(); boolean edit=Boolean.TRUE.equals(request.getAttribute("editMode")); String rawDeadline=(String)request.getAttribute("rawDeadline"); String deadline=rawDeadline!=null?rawDeadline:(goal.getDeadline()==null?"":goal.getDeadline().toString()); String selectedStatus=goal.getStatus()==null||goal.getStatus().isEmpty()?"Pending":goal.getStatus(); %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title><%=edit?"Edit":"Add"%> Goal - SkillSprint</title><style>body{font-family:Arial,sans-serif;margin:2rem;max-width:720px}label{display:grid;gap:.3rem;margin:.9rem 0}input,select{padding:.55rem;font:inherit}.error{color:#9b1c1c}</style></head><body><main>
<h1><%=edit?"Edit":"Add"%> goal</h1><% if(request.getAttribute("error")!=null){ %><p class="error"><%=esc(request.getAttribute("error"))%></p><% } %>
<form method="post" action="<%=request.getContextPath()%>/goals/<%=edit?"update":"create"%>">
<% if(edit){ %><input type="hidden" name="id" value="<%=goal.getGoalId()%>"><% } %>
<label>Goal title <input name="goalTitle" required maxlength="200" value="<%=esc(goal.getGoalTitle())%>"></label>
<label>Deadline <input type="date" name="deadline" value="<%=esc(deadline)%>"></label>
<label>Status <select name="status" required><% for(String status:(List<String>)request.getAttribute("statuses")){ %><option value="<%=esc(status)%>" <%=status.equals(selectedStatus)?"selected":""%>><%=esc(status)%></option><% } %></select></label>
<button type="submit"><%=edit?"Save changes":"Add goal"%></button> <a href="<%=request.getContextPath()%>/goals/list">Cancel</a></form></main></body></html>
