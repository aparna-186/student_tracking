<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.net.URI,java.net.URISyntaxException,org.example.skillsprint.model.User" %>
<%! private String esc(Object value) { if(value==null)return ""; return String.valueOf(value).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); }
private boolean safeUrl(String value) { try { URI uri=new URI(value); String scheme=uri.getScheme(); return uri.isAbsolute()&&uri.getHost()!=null&&(scheme!=null&&(scheme.equalsIgnoreCase("http")||scheme.equalsIgnoreCase("https"))); } catch(URISyntaxException e){return false;} } %>
<% User profile=(User)request.getAttribute("profile"); %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>Student Profile - SkillSprint</title>
<style>body{font-family:Arial,sans-serif;margin:2rem;color:#222;max-width:760px}label{display:grid;gap:.3rem;margin:.9rem 0}input{padding:.55rem;font:inherit;width:min(100%,34rem)}.message{padding:.7rem;border-radius:5px}.success{background:#e8f6ec;color:#175b2b}.error{background:#fdecec;color:#8d1e1e}.profile-links{margin:.4rem 0 1.2rem}</style></head><body><main>
<h1>Student Profile</h1><p><a href="<%=request.getContextPath()%>/dashboard">Dashboard</a></p>
<% if(request.getAttribute("success")!=null){ %><p class="message success" role="status"><%=esc(request.getAttribute("success"))%></p><% } %>
<% if(request.getAttribute("error")!=null){ %><p class="message error" role="alert"><%=esc(request.getAttribute("error"))%></p><% } %>
<% if(profile!=null){ %><section class="profile-links" aria-label="Saved profile links">
<% if(profile.getGithubUrl()!=null&&safeUrl(profile.getGithubUrl())){ %><p>GitHub: <a href="<%=esc(profile.getGithubUrl())%>" target="_blank" rel="noopener noreferrer"><%=esc(profile.getGithubUrl())%></a></p><% } %>
<% if(profile.getLinkedinUrl()!=null&&safeUrl(profile.getLinkedinUrl())){ %><p>LinkedIn: <a href="<%=esc(profile.getLinkedinUrl())%>" target="_blank" rel="noopener noreferrer"><%=esc(profile.getLinkedinUrl())%></a></p><% } %>
</section>
<form method="post" action="<%=request.getContextPath()%>/profile">
<label>Full name <input name="fullName" required maxlength="100" value="<%=profile==null?"":esc(profile.getFullName())%>"></label>
<label>Email <input type="email" name="email" required maxlength="100" value="<%=profile==null?"":esc(profile.getEmail())%>"></label>
<label>College <input name="college" maxlength="150" value="<%=profile==null?"":esc(profile.getCollege())%>"></label>
<label>Branch <input name="branch" maxlength="100" value="<%=profile==null?"":esc(profile.getBranch())%>"></label>
<label>GitHub profile URL <input type="url" name="githubUrl" maxlength="255" placeholder="https://github.com/username" value="<%=profile==null?"":esc(profile.getGithubUrl())%>"></label>
<label>LinkedIn profile URL <input type="url" name="linkedinUrl" maxlength="255" placeholder="https://www.linkedin.com/in/username" value="<%=profile==null?"":esc(profile.getLinkedinUrl())%>"></label>
<button type="submit">Save profile</button></form><% } %></main></body></html>
