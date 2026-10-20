<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Objects" %>
<%! private String esc(Object value) { return String.valueOf(value==null?"":value).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); } %>
<% request.setAttribute("pageTitle","Session check"); request.setAttribute("pageSubtitle","Your sign-in is ready."); request.setAttribute("activePage",""); String name=esc(Objects.toString(request.getAttribute("userName"),"")); %>
<%@ include file="_app-header.jspf" %>
<section class="panel"><h2 class="panel-title">You’re signed in</h2><p>Welcome, <strong><%=name%></strong>. Your workspace is ready.</p><p>Session user ID: <%=esc(request.getAttribute("userId"))%></p><p><a class="button" href="<%=request.getContextPath()%>/dashboard">Open dashboard</a></p></section>
<%@ include file="_app-footer.jspf" %>
