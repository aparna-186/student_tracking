<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%! private String esc(Object value) { return String.valueOf(value==null?"":value).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); } %>
<% request.setAttribute("pageTitle","Learning Goals"); request.setAttribute("pageSubtitle","Something interrupted the request."); request.setAttribute("activePage","goals"); %>
<%@ include file="_app-header.jspf" %><section class="error-page"><div class="alert error" role="alert"><%=esc(request.getAttribute("error"))%></div><a class="button" href="<%=request.getContextPath()%>/goals/list">Return to goals</a></section><%@ include file="_app-footer.jspf" %>
