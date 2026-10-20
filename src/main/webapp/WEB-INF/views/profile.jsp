<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.net.URI,java.net.URISyntaxException,org.example.skillsprint.model.User" %>
<%! private String esc(Object value) { if(value==null)return ""; return String.valueOf(value).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); }
private boolean safeUrl(String value) { try { URI uri=new URI(value); String scheme=uri.getScheme(); return uri.isAbsolute()&&uri.getHost()!=null&&(scheme!=null&&(scheme.equalsIgnoreCase("http")||scheme.equalsIgnoreCase("https"))); } catch(URISyntaxException e){return false;} } %>
<% User profile=(User)request.getAttribute("profile"); request.setAttribute("pageTitle","Student Profile"); request.setAttribute("pageSubtitle","Keep your profile details current and ready to share."); request.setAttribute("activePage","profile"); String success=(String)request.getAttribute("success"); %>
<%@ include file="_app-header.jspf" %>
<% if(success!=null){ %><div class="alert success" role="status" data-dismissible><%=esc(success)%><button class="alert-dismiss" type="button" data-dismiss aria-label="Dismiss message">×</button></div><% } %>
<% if(request.getAttribute("error")!=null){ %><div class="alert error" role="alert" data-dismissible><%=esc(request.getAttribute("error"))%><button class="alert-dismiss" type="button" data-dismiss aria-label="Dismiss message">×</button></div><% } %>
<% if(profile!=null){ %><div class="profile-layout"><section class="form-card"><form method="post" action="<%=request.getContextPath()%>/profile" data-submitting><input type="hidden" name="_csrf" value="<%=request.getAttribute("csrfToken")%>">
<div class="form-grid"><div class="field full"><label for="fullName">Full name <span class="required-mark">*</span></label><input id="fullName" name="fullName" required maxlength="100" autocomplete="name" value="<%=esc(profile.getFullName())%>"></div>
<div class="field full"><label for="email">Email <span class="required-mark">*</span></label><input id="email" type="email" name="email" required maxlength="100" autocomplete="email" value="<%=esc(profile.getEmail())%>"></div>
<div class="field"><label for="college">College</label><input id="college" name="college" maxlength="150" value="<%=esc(profile.getCollege())%>"></div>
<div class="field"><label for="branch">Branch</label><input id="branch" name="branch" maxlength="100" value="<%=esc(profile.getBranch())%>"></div>
<div class="field full"><label for="githubUrl">GitHub profile URL</label><input id="githubUrl" type="url" name="githubUrl" maxlength="255" placeholder="https://github.com/username" value="<%=esc(profile.getGithubUrl())%>"></div>
<div class="field full"><label for="linkedinUrl">LinkedIn profile URL</label><input id="linkedinUrl" type="url" name="linkedinUrl" maxlength="255" placeholder="https://linkedin.com/in/username" value="<%=esc(profile.getLinkedinUrl())%>"></div></div>
<div class="form-actions"><button type="submit">Save profile</button><a class="button secondary" href="<%=request.getContextPath()%>/dashboard">Cancel</a></div></form></section>
<aside class="profile-aside"><h2>Profile links</h2><p>Public profile links make it easy to find your work.</p>
<% if(profile.getGithubUrl()!=null&&safeUrl(profile.getGithubUrl())){ %><a href="<%=esc(profile.getGithubUrl())%>" target="_blank" rel="noopener noreferrer">View GitHub profile ↗</a><% } %>
<% if(profile.getLinkedinUrl()!=null&&safeUrl(profile.getLinkedinUrl())){ %><a href="<%=esc(profile.getLinkedinUrl())%>" target="_blank" rel="noopener noreferrer">View LinkedIn profile ↗</a><% } %>
<% if((profile.getGithubUrl()==null||!safeUrl(profile.getGithubUrl()))&&(profile.getLinkedinUrl()==null||!safeUrl(profile.getLinkedinUrl()))){ %><span class="field-hint">Add a valid HTTP or HTTPS link to display it here.</span><% } %></aside></div><% } %>
<%@ include file="_app-footer.jspf" %>
