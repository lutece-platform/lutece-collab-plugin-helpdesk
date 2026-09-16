<%@ page errorPage="../../ErrorPage.jsp" %>
<%@ page import="fr.paris.lutece.plugins.helpdesk.web.HelpdeskJspBean" %>
<%@ page import="fr.paris.lutece.plugins.helpdesk.web.HelpdeskSearchJspBean" %>
${helpdeskSearchJspBean.init( pageContext.request, HelpdeskJspBean.RIGHT_MANAGE_HELPDESK )}
${pageContext.setAttribute( 'strContent', helpdeskSearchJspBean.getSearch( pageContext.request ) )}
<jsp:include page="../../AdminHeader.jsp" />
${pageContext.getAttribute( 'strContent' )}
<%@ include file="../../AdminFooter.jsp" %>
