<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="helpdesk" scope="session" class="fr.paris.lutece.plugins.helpdesk.web.HelpdeskJspBean" />

<%
    helpdesk.init( request , helpdesk.RIGHT_MANAGE_HELPDESK );
    response.sendRedirect( helpdesk.doCreateSubject( request ) );
%>