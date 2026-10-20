<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.net.URI,java.net.URISyntaxException,java.util.List,org.example.skillsprint.model.Certificate" %>
<%! private String esc(Object value) { if(value==null)return ""; return String.valueOf(value).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); }
private boolean safeUrl(String value) { try { URI uri=new URI(value); String scheme=uri.getScheme(); return scheme!=null&&(scheme.equalsIgnoreCase("http")||scheme.equalsIgnoreCase("https"))&&uri.getHost()!=null&&!uri.getHost().isEmpty(); } catch(URISyntaxException e){return false;} } %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>Certificates - SkillSprint</title>
<style>body{font-family:Arial,sans-serif;margin:2rem;color:#222}table{border-collapse:collapse;width:100%;margin-top:1rem}th,td{border:1px solid #ccc;padding:.7rem;text-align:left;vertical-align:top}th{background:#f2f4f7}.inline{display:inline}</style></head><body><main>
<h1>Certificates</h1><p><a href="<%=request.getContextPath()%>/certificates/new">Add certificate</a> · <a href="<%=request.getContextPath()%>/dashboard">Dashboard</a></p>
<% List<Certificate> certificates=(List<Certificate>)request.getAttribute("certificates"); if(certificates==null||certificates.isEmpty()){ %>
<p>You have not added any certificates yet.</p>
<% }else{ %><table><thead><tr><th>Title</th><th>Issuer</th><th>Issue date</th><th>Credential</th><th>Actions</th></tr></thead><tbody>
<% for(Certificate certificate:certificates){ String url=certificate.getCredentialLink(); %><tr><td><%=esc(certificate.getTitle())%></td><td><%=esc(certificate.getIssuer())%></td><td><%=esc(certificate.getIssueDate())%></td><td><% if(url==null||url.isEmpty()){ %>No credential link provided<% }else if(safeUrl(url)){ %><a href="<%=esc(url)%>" target="_blank" rel="noopener noreferrer">View Certificate</a><% }else{ %>No credential link provided<% } %></td><td><a href="<%=request.getContextPath()%>/certificates/edit?id=<%=certificate.getCertificateId()%>">Edit</a>
<form class="inline" method="post" action="<%=request.getContextPath()%>/certificates/delete" onsubmit="return confirm('Delete this certificate?');"><input type="hidden" name="id" value="<%=certificate.getCertificateId()%>"><button type="submit">Delete</button></form></td></tr><% } %>
</tbody></table><% } %></main></body></html>
