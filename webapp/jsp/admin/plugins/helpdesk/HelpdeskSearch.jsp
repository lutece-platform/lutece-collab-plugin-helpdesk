<%@ page errorPage="../../ErrorPage.jsp" %>


<%@page import="fr.paris.lutece.plugins.helpdesk.web.HelpdeskJspBean"%>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="helpdeskSearch" scope="session" class="fr.paris.lutece.plugins.helpdesk.web.HelpdeskSearchJspBean" />

<% helpdeskSearch.init( request, HelpdeskJspBean.RIGHT_MANAGE_HELPDESK ); %>
<%= helpdeskSearch.getSearch( request ) %>

<%@ include file="../../AdminFooter.jsp" %>