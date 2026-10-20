<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.net.URI,java.net.URISyntaxException,org.example.skillsprint.model.CodingProgress" %>
<%! private String esc(Object value) { if(value==null)return ""; return String.valueOf(value).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); }
private boolean safeUrl(String value) { try { URI u=new URI(value); String s=u.getScheme(); return s!=null&&(s.equalsIgnoreCase("http")||s.equalsIgnoreCase("https"))&&u.getHost()!=null; } catch(URISyntaxException e){return false;} } %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>Coding Progress - SkillSprint</title>
<style>body{font-family:Arial,sans-serif;margin:2rem;color:#222}table{border-collapse:collapse;width:100%;margin-top:1rem}th,td{border:1px solid #ccc;padding:.65rem;text-align:left;vertical-align:top}th{background:#f2f4f7}.filters{display:flex;gap:.7rem;align-items:end;flex-wrap:wrap}.filters label{display:grid;gap:.25rem}.inline{display:inline}</style></head><body><main>
<h1>Coding Progress</h1><p><a href="<%=request.getContextPath()%>/coding/new">Add coding record</a> · <a href="<%=request.getContextPath()%>/dashboard">Dashboard</a></p>
<form class="filters" method="get" action="<%=request.getContextPath()%>/coding/list">
<label>Platform <input name="platform" maxlength="50" value="<%=esc(request.getParameter("platform"))%>"></label>
<label>Difficulty <select name="difficulty"><option value="">All difficulties</option><% for(String d:(List<String>)request.getAttribute("difficulties")){ %><option value="<%=esc(d)%>" <%=d.equals(request.getParameter("difficulty"))?"selected":""%>><%=esc(d)%></option><% } %></select></label>
<label>Status <select name="status"><option value="">All statuses</option><% for(String s:(List<String>)request.getAttribute("statuses")){ %><option value="<%=esc(s)%>" <%=s.equals(request.getParameter("status"))?"selected":""%>><%=esc(s)%></option><% } %></select></label>
<button type="submit">Filter</button><a href="<%=request.getContextPath()%>/coding/list">Clear filters</a></form>
<% List<CodingProgress> records=(List<CodingProgress>)request.getAttribute("records"); if(records==null||records.isEmpty()){ %><p>No coding records match these filters. Add a record or clear the filters.</p><% }else{ %>
<table><thead><tr><th>Platform</th><th>Problem</th><th>Difficulty</th><th>Status</th><th>Solved date</th><th>Problem URL</th><th>Actions</th></tr></thead><tbody>
<% for(CodingProgress r:records){ String url=r.getProblemUrl(); %><tr><td><%=esc(r.getPlatform())%></td><td><%=esc(r.getProblemName())%></td><td><%=esc(r.getDifficulty())%></td><td><%=esc(r.getProblemStatus())%></td><td><%=esc(r.getSolvedDate())%></td><td><% if(url!=null&&!url.isEmpty()){ if(safeUrl(url)){ %><a href="<%=esc(url)%>" target="_blank" rel="noopener noreferrer"><%=esc(url)%></a><% }else{ %><%=esc(url)%><% } } %></td><td><a href="<%=request.getContextPath()%>/coding/edit?id=<%=r.getCodingId()%>">Edit</a>
<form class="inline" method="post" action="<%=request.getContextPath()%>/coding/delete" onsubmit="return confirm('Delete this coding record?');"><input type="hidden" name="id" value="<%=r.getCodingId()%>"><button type="submit">Delete</button></form></td></tr><% } %>
</tbody></table><% } %></main></body></html>
