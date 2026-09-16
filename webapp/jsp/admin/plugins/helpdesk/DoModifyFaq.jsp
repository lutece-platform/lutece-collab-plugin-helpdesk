<%@ page errorPage="../../ErrorPage.jsp" %>
<%@ page import="fr.paris.lutece.plugins.helpdesk.web.HelpdeskJspBean" %>
${helpdeskJspBean.init( pageContext.request, HelpdeskJspBean.RIGHT_MANAGE_HELPDESK )}
${pageContext.response.sendRedirect( helpdeskJspBean.doModifyFaq( pageContext.request ) )}
