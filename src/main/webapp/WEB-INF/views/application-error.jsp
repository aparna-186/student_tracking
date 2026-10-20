<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%! private String esc(Object value) { return String.valueOf(value==null?"":value).replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;"); } %>
<% request.setAttribute("pageTitle","Applications"); request.setAttribute("pageSubtitle","Something interrupted the request."); request.setAttribute("activePage","applications"); %>
<%@ include file="_app-header.jspf" %><section class="error-page"><div class="alert error" role="alert"><%=esc(request.getAttribute("error"))%></div><a class="button" href="<%=request.getContextPath()%>/applications/list">Return to applications</a></section><%@ include file="_app-footer.jspf" %>
