<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.example.skillsprint.model.Certificate" %>
<%! private String esc(Object value) { if(value==null)return ""; return String.valueOf(value).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); } %>
<% Certificate certificate=(Certificate)request.getAttribute("certificate"); if(certificate==null)certificate=new Certificate(); boolean edit=Boolean.TRUE.equals(request.getAttribute("editMode")); String rawDate=(String)request.getAttribute("rawDate"); String date=rawDate!=null?rawDate:(certificate.getIssueDate()==null?"":certificate.getIssueDate().toString()); %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title><%=edit?"Edit":"Add"%> Certificate - SkillSprint</title><style>body{font-family:Arial,sans-serif;margin:2rem;max-width:720px}label{display:grid;gap:.3rem;margin:.9rem 0}input{padding:.55rem;font:inherit}.error{color:#9b1c1c}</style></head><body><main>
<h1><%=edit?"Edit":"Add"%> certificate</h1><% if(request.getAttribute("error")!=null){ %><p class="error"><%=esc(request.getAttribute("error"))%></p><% } %>
<form method="post" action="<%=request.getContextPath()%>/certificates/<%=edit?"update":"create"%>">
<% if(edit){ %><input type="hidden" name="id" value="<%=certificate.getCertificateId()%>"><% } %>
<label>Certificate title <input name="title" required maxlength="150" value="<%=esc(certificate.getTitle())%>"></label>
<label>Issuer <input name="issuer" maxlength="100" value="<%=esc(certificate.getIssuer())%>"></label>
<label>Issue date <input type="date" name="issueDate" value="<%=esc(date)%>"></label>
<label>Credential URL <input type="url" name="credentialLink" maxlength="500" placeholder="https://..." value="<%=esc(certificate.getCredentialLink())%>"></label>
<button type="submit"><%=edit?"Save changes":"Add certificate"%></button> <a href="<%=request.getContextPath()%>/certificates/list">Cancel</a></form></main></body></html>
