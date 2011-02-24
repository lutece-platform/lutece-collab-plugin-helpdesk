<%@ page errorPage="../../ErrorPage.jsp"%>

<jsp:useBean id="helpdesk" scope="session"
	class="fr.paris.lutece.plugins.helpdesk.web.HelpdeskJspBean" />


<%
	out.println(helpdesk.getAnswerSelection(request));
%>

