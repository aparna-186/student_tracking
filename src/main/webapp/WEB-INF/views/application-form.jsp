<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,org.example.skillsprint.model.InternshipApplication" %>
<%! private String esc(Object value) { if (value == null) return ""; return String.valueOf(value).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;"); } %>
<% InternshipApplication a=(InternshipApplication)request.getAttribute("application"); if(a==null)a=new InternshipApplication(); boolean edit=Boolean.TRUE.equals(request.getAttribute("editMode")); String date=a.getAppliedDate()==null?"":a.getAppliedDate().toString(); request.setAttribute("pageTitle",edit?"Edit application":"Add application"); request.setAttribute("pageSubtitle","Capture the details that help you follow this opportunity."); request.setAttribute("activePage","applications"); %>
<%@ include file="_app-header.jspf" %>
<% if(request.getAttribute("error")!=null){ %><div class="alert error" role="alert" data-dismissible><%=esc(request.getAttribute("error"))%><button class="alert-dismiss" type="button" data-dismiss aria-label="Dismiss message">×</button></div><% } %>
<section class="form-card"><form method="post" action="<%=request.getContextPath()%>/applications/<%=edit?"update":"create"%>" data-submitting>
<input type="hidden" name="_csrf" value="<%=request.getAttribute("csrfToken")%>"><% if(edit){ %><input type="hidden" name="id" value="<%=a.getApplicationId()%>"><% } %>
<div class="form-grid">
<div class="field"><label for="companyName">Company name <span class="required-mark">*</span></label><input id="companyName" name="companyName" required maxlength="100" value="<%=esc(a.getCompanyName())%>"></div>
<div class="field"><label for="role">Role <span class="required-mark">*</span></label><input id="role" name="role" required maxlength="100" value="<%=esc(a.getRole())%>"></div>
<div class="field"><label for="applicationReference">External application reference</label><input id="applicationReference" name="applicationReference" maxlength="100" value="<%=esc(a.getApplicationReference())%>"></div>
<div class="field"><label for="appliedDate">Applied date</label><input id="appliedDate" type="date" name="appliedDate" value="<%=esc(date)%>"></div>
<div class="field"><label for="status">Status <span class="required-mark">*</span></label><select id="status" name="status" required><% for(String s:(List<String>)request.getAttribute("statuses")){ %><option value="<%=esc(s)%>" <%=s.equals(a.getStatus()==null||a.getStatus().isEmpty()?"Applied":a.getStatus())?"selected":""%>><%=esc(s)%></option><% } %></select></div>
<div class="field full"><label for="notes">Notes</label><textarea id="notes" name="notes" maxlength="500" rows="5" placeholder="Add a reminder or useful context"><%=esc(a.getNotes())%></textarea></div>
</div><div class="form-actions"><button type="submit"><%=edit?"Save changes":"Add application"%></button><a class="button secondary" href="<%=request.getContextPath()%>/applications/list">Cancel</a></div>
</form></section>
<%@ include file="_app-footer.jspf" %>
