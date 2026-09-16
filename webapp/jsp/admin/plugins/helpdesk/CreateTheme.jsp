<%@ page errorPage="../../ErrorPage.jsp" %>
<%@ page import="fr.paris.lutece.plugins.helpdesk.web.HelpdeskJspBean" %>
${helpdeskJspBean.init( pageContext.request, HelpdeskJspBean.RIGHT_MANAGE_HELPDESK )}
${pageContext.setAttribute( 'strContent', helpdeskJspBean.getCreateTheme( pageContext.request ) )}
<jsp:include page="../../AdminHeader.jsp" />
${pageContext.getAttribute( 'strContent' )}
<%@ include file="../../AdminFooter.jsp" %>
