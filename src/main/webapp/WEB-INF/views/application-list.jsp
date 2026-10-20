<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,org.example.skillsprint.model.InternshipApplication" %>
<%! private String esc(Object value) { if (value == null) return ""; return String.valueOf(value).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;"); }
private String badge(String value) { if ("Offer".equals(value)) return "success"; if ("Rejected".equals(value) || "Withdrawn".equals(value)) return "danger"; if ("Interview".equals(value) || "Online Assessment".equals(value)) return "warning"; return "primary"; } %>
<% request.setAttribute("pageTitle", "Internship Applications"); request.setAttribute("pageSubtitle", "Keep every opportunity and its next step in view."); request.setAttribute("activePage", "applications"); request.setAttribute("pageActionLabel", "Add application"); request.setAttribute("pageActionUrl", "/applications/new"); %>
<%@ include file="_app-header.jspf" %>
<form class="filters" method="get" action="<%=request.getContextPath()%>/applications/list">
    <div class="field"><label for="company">Company search</label><input id="company" type="search" name="company" maxlength="100" placeholder="Search company" value="<%=esc(request.getParameter("company"))%>"></div>
    <div class="field"><label for="applicationStatus">Status</label><select id="applicationStatus" name="status"><option value="">All statuses</option><% for(String s:(List<String>)request.getAttribute("statuses")){ %><option value="<%=esc(s)%>" <%=s.equals(request.getParameter("status"))?"selected":""%>><%=esc(s)%></option><% } %></select></div>
    <button type="submit">Apply filters</button><a class="button secondary" href="<%=request.getContextPath()%>/applications/list">Clear</a>
</form>
<% List<InternshipApplication> apps=(List<InternshipApplication>)request.getAttribute("applications"); if(apps==null||apps.isEmpty()){ %>
<section class="empty-state"><h2>No applications found</h2><p>New opportunities you add will appear here. Adjust your filters or start tracking an application.</p><a class="button" href="<%=request.getContextPath()%>/applications/new">Add application</a></section>
<% } else { %><div class="table-wrap"><table><thead><tr><th>Company</th><th>Role</th><th>External reference</th><th>Applied</th><th>Status</th><th>Notes</th><th>Actions</th></tr></thead><tbody>
<% for(InternshipApplication a:apps){ %><tr><td><strong><%=esc(a.getCompanyName())%></strong></td><td><%=esc(a.getRole())%></td><td><%=esc(a.getApplicationReference())%></td><td><%=esc(a.getAppliedDate())%></td><td><span class="badge <%=badge(a.getStatus())%>"><%=esc(a.getStatus())%></span></td><td><%=esc(a.getNotes())%></td><td><div class="table-actions"><a class="button secondary" href="<%=request.getContextPath()%>/applications/edit?id=<%=a.getApplicationId()%>">Edit</a><form method="post" action="<%=request.getContextPath()%>/applications/delete" data-confirm="Delete this application?"><input type="hidden" name="_csrf" value="<%=request.getAttribute("csrfToken")%>"><input type="hidden" name="id" value="<%=a.getApplicationId()%>"><button class="danger" type="submit">Delete</button></form></div></td></tr><% } %>
</tbody></table></div><% } %>
<%@ include file="_app-footer.jspf" %>
