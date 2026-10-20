<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,org.example.skillsprint.model.CodingProgress" %>
<%! private String esc(Object value) { if(value==null)return ""; return String.valueOf(value).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); } %>
<% CodingProgress record=(CodingProgress)request.getAttribute("record"); if(record==null)record=new CodingProgress(); boolean edit=Boolean.TRUE.equals(request.getAttribute("editMode")); String rawDate=(String)request.getAttribute("rawDate"); String date=rawDate!=null?rawDate:(record.getSolvedDate()==null?"":record.getSolvedDate().toString()); String selectedStatus=record.getProblemStatus()==null||record.getProblemStatus().isEmpty()?"Solved":record.getProblemStatus(); %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title><%=edit?"Edit":"Add"%> Coding Record - SkillSprint</title><style>body{font-family:Arial,sans-serif;margin:2rem;max-width:720px}label{display:grid;gap:.3rem;margin:.8rem 0}input,select{padding:.5rem;font:inherit}.error{color:#9b1c1c}</style></head><body><main>
<h1><%=edit?"Edit":"Add"%> coding record</h1><% if(request.getAttribute("error")!=null){ %><p class="error"><%=esc(request.getAttribute("error"))%></p><% } %>
<form method="post" action="<%=request.getContextPath()%>/coding/<%=edit?"update":"create"%>">
<% if(edit){ %><input type="hidden" name="id" value="<%=record.getCodingId()%>"><% } %>
<label>Platform <input name="platform" maxlength="50" value="<%=esc(record.getPlatform())%>"></label>
<label>Problem name <input name="problemName" required maxlength="150" value="<%=esc(record.getProblemName())%>"></label>
<label>Difficulty <select name="difficulty" required><option value="">Choose difficulty</option><% for(String d:(List<String>)request.getAttribute("difficulties")){ %><option value="<%=esc(d)%>" <%=d.equals(record.getDifficulty())?"selected":""%>><%=esc(d)%></option><% } %></select></label>
<label>Status <select name="problemStatus" required><% for(String s:(List<String>)request.getAttribute("statuses")){ %><option value="<%=esc(s)%>" <%=s.equals(selectedStatus)?"selected":""%>><%=esc(s)%></option><% } %></select></label>
<label>Solved date <input type="date" name="solvedDate" value="<%=esc(date)%>"></label>
<label>Problem URL <input type="url" name="problemUrl" maxlength="255" value="<%=esc(record.getProblemUrl())%>" placeholder="https://..."></label>
<button type="submit"><%=edit?"Save changes":"Add record"%></button> <a href="<%=request.getContextPath()%>/coding/list">Cancel</a></form></main></body></html>
