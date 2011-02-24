<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="helpdesk" scope="session" class="fr.paris.lutece.plugins.helpdesk.web.HelpdeskJspBean" />

<% helpdesk.init( request , helpdesk.RIGHT_MANAGE_HELPDESK ); %>
<%= helpdesk.getArchivedQuestionList(request) %>

<%@ include file="../../AdminFooter.jsp" %>




