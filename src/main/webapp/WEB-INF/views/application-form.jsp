<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,org.example.skillsprint.model.InternshipApplication" %>
<%! private String esc(Object value) { if (value == null) return ""; return String.valueOf(value).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;"); } %>
<% InternshipApplication a=(InternshipApplication)request.getAttribute("application"); if(a==null)a=new InternshipApplication(); boolean edit=Boolean.TRUE.equals(request.getAttribute("editMode")); String date=a.getAppliedDate()==null?"":a.getAppliedDate().toString(); %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title><%=edit?"Edit":"Add"%> Application - SkillSprint</title><style>body{font-family:Arial,sans-serif;margin:2rem;max-width:720px}label{display:grid;gap:.3rem;margin:.8rem 0}input,select,textarea{padding:.5rem;font:inherit}.error{color:#9b1c1c}</style></head><body><main>
<h1><%=edit?"Edit":"Add"%> application</h1><% if(request.getAttribute("error")!=null){ %><p class="error"><%=esc(request.getAttribute("error"))%></p><% } %>
<form method="post" action="<%=request.getContextPath()%>/applications/<%=edit?"update":"create"%>">
<% if(edit){ %><input type="hidden" name="id" value="<%=a.getApplicationId()%>"><% } %>
<label>Company name <input name="companyName" required maxlength="100" value="<%=esc(a.getCompanyName())%>"></label>
<label>Role <input name="role" required maxlength="100" value="<%=esc(a.getRole())%>"></label>
<label>External application reference <input name="applicationReference" maxlength="100" value="<%=esc(a.getApplicationReference())%>"></label>
<label>Applied date <input type="date" name="appliedDate" value="<%=esc(date)%>"></label>
<label>Status <select name="status" required><% for(String s:(List<String>)request.getAttribute("statuses")){ %><option value="<%=esc(s)%>" <%=s.equals(a.getStatus()==null||a.getStatus().isEmpty()?"Applied":a.getStatus())?"selected":""%>><%=esc(s)%></option><% } %></select></label>
<label>Notes <textarea name="notes" maxlength="500" rows="5"><%=esc(a.getNotes())%></textarea></label>
<button type="submit"><%=edit?"Save changes":"Add application"%></button> <a href="<%=request.getContextPath()%>/applications/list">Cancel</a></form></main></body></html>
