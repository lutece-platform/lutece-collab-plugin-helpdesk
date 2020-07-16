/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.helpdesk.web;

import fr.paris.lutece.plugins.helpdesk.business.Faq;
import fr.paris.lutece.plugins.helpdesk.business.FaqHome;
import fr.paris.lutece.plugins.helpdesk.business.QuestionAnswer;
import fr.paris.lutece.plugins.helpdesk.business.Subject;
import fr.paris.lutece.plugins.helpdesk.business.SubjectHome;
import fr.paris.lutece.plugins.helpdesk.business.Theme;
import fr.paris.lutece.plugins.helpdesk.business.ThemeHome;
import fr.paris.lutece.plugins.helpdesk.business.VisitorQuestion;
import fr.paris.lutece.plugins.helpdesk.business.VisitorQuestionHome;
import fr.paris.lutece.plugins.helpdesk.service.HelpdeskPlugin;
import fr.paris.lutece.plugins.helpdesk.service.helpdesksearch.HelpdeskSearchService;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * This class implements the HelpDesk XPage.
 */
public class HelpdeskApp implements XPageApplication
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1592321991604068308L;
	//Public constants
    public static final String ANCHOR_SUBJECT = "subject_";
    public static final String ANCHOR_QUESTION_ANSWER = "question_answer_";
    public static final String PARAMETER_FAQ_ID = "faq_id";

    //Templates
    private static final String TEMPLATE_SUBJECT_LIST = "skin/plugins/helpdesk/subject_list.html";
    private static final String TEMPLATE_FAQ_LIST = "skin/plugins/helpdesk/faq_list.html";
    private static final String TEMPLATE_CONTACT_FORM_RESULT = "skin/plugins/helpdesk/contact_result.html";
    private static final String TEMPLATE_CONTACT_FORM = "skin/plugins/helpdesk/contact.html";
    private static final String TEMPLATE_SEND_VISITOR_QUESTION = "skin/plugins/helpdesk/send_visitor_question.html";
    private static final String TEMPLATE_CONTACT_FORM_ERROR = "skin/plugins/helpdesk/contact_error.html";

    //Parameters
    private static final String PARAMETER_KEYWORDS = "form_search_keywords";
    private static final String PARAMETER_DATE_BEGIN = "form_search_date_begin";
    private static final String PARAMETER_DATE_END = "form_search_date_end";
    private static final String PARAMETER_SUBJECT_ID = "form_search_subject_id";
    private static final String PARAMETER_SEARCH_SUB_SUBJECTS = "form_search_sub_subjects";
    private static final String PARAMETER_CONTACT = "contact";
    private static final String PARAMETER_CONTACT_RESULT = "result";
    private static final String PARAMETER_THEME_ID = "theme_id";
    private static final String PARAMETER_QUESTION = "question";
    private static final String PARAMETER_LAST_NAME = "last_name";
    private static final String PARAMETER_FIRST_NAME = "first_name";
    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_MAIL_SUBJECT = "mail_subject";
    
    //Markers
    private static final String MARK_VISITOR_QUESTION = "visitor_question";
    private static final String MARK_PORTAL_URL = "portal_url";
    private static final String MARK_DATE = "date";
    private static final String MARK_PLUGIN_NAME = "plugin_name";
    private static final String MARK_FILTER_SEARCHED_KEYWORDS = "filter_searched_keywords";
    private static final String MARK_FILTER_DATE_BEGIN = "filter_date_begin";
    private static final String MARK_FILTER_DATE_END = "filter_date_end";
    private static final String MARK_FILTER_SUBJECT = "filter_subject_id";
    private static final String MARK_FILTER_SEARCH_SUB_SUBJECT = "filter_search_sub_subject";
    private static final String MARK_PATH_LABEL = "path_label";
    private static final String MARK_SUBJECT_LIST = "helpdesk_subject_list";
    private static final String MARK_QUESTIONANSWER_LIST = "helpdesk_question_answer_list";
    private static final String MARK_SEARCH_PAGE = "search_page";
    private static final String MARK_PLUGIN = "plugin";
    private static final String MARK_LOCALE = "locale";
    private static final String FULL_URL = "fullurl";

    //    private static final String MARK_LOCALE_DATE_FORMAT = "date_format";
    private static final String MARK_FAQ = "faq";
    private static final String MARK_FAQ_LIST = "faq_list";
    private static final String MARK_THEME_LIST = "helpdesk_theme_list";
    private static final String MARK_DEFAULT_VALUE = "default_value";
    private static final String MARK_ANCHOR_SUBJECT = "anchor_subject";
    private static final String MARK_ANCHOR_QUESTION_ANSWER = "anchor_question_answer";    

    // I18n messages
    private static final String MESSAGE_CONTACT_MANDATORY_FIELDS_ERROR_MESSAGE = "helpdesk.siteMessage.mandatoryFields.errorMessage";
    private static final String MESSAGE_CONTACT_MANDATORY_FIELDS_TITLE_MESSAGE = "helpdesk.siteMessage.mandatoryFields.title";
    private static final String MESSAGE_CONTACT_UNSUFFICIENT_CARACTERS_ERROR_MESSAGE = "helpdesk.siteMessage.unsufficientCaracters.errorMessage";
    private static final String MESSAGE_CONTACT_UNSUFFICIENT_CARACTERS_TITLE_MESSAGE = "helpdesk.siteMessage.unsufficientCaracters.title";
    private static final String MESSAGE_CONTACT_INVALID_MAIL_ERROR_MESSAGE = "helpdesk.siteMessage.invalidMail.errorMessage";
    private static final String MESSAGE_CONTACT_INVALID_MAIL_TITLE_MESSAGE = "helpdesk.siteMessage.invalidMail.title";
    private static final String MESSAGE_SEARCH_DATE_MANDATORY = "helpdesk.siteMessage.datesMandatory";
    private static final String MESSAGE_SEARCH_DATE_VALIDITY = "helpdesk.siteMessage.dateValidity";
    private static final String MESSAGE_ACCESS_DENIED = "helpdesk.siteMessage.access.denied";
    private static final String MESSAGE_SUBJECT_LIST = "helpdesk.pageTitle.subjectList";
    private static final String MESSAGE_HELPDESK_PATH_LABEL = "helpdesk.pagePathLabel";
    private static final String MESSAGE_SUBJECT_LIST_RESULTS = "helpdesk.pageTitle.subjectList_results";
    private static final String MESSAGE_FAQ_LIST = "helpdesk.faq_list.pageTitle";
    private static final String MESSAGE_PORTAL_NAME = "lutece.name";
    private static final String MESSAGE_WEBMASTER_EMAIL = "email.webmaster";    
    private static final String REGEX_ID = "^[\\d]+$";
    private static final String CONSTANT_EMPTY_STRING = "";
    private static final String CONSTANT_SPACE = " ";
    private static final String CONSTANT_MINUS = " - ";
    
    //properties
    private static final String PROPERTY_MAIL_SUBJECT_PREFIX = "mail.helpdesk.subjectPrefix";

    /**
     * Creates a new QuizPage object.
     */
    public HelpdeskApp(  )
    {
    }

    /**
     * Returns the Helpdesk XPage content depending on the request parameters and the current mode.
     *
     * @param request The HTTP request.
     * @param nMode The current mode.
     * @param plugin The plugin.
     * @return The page content.
     * @throws SiteMessageException The Site message exception
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws SiteMessageException
    {
        XPage page = new XPage(  );

        String strContact = request.getParameter( PARAMETER_CONTACT );
        String strIdFaq = request.getParameter( PARAMETER_FAQ_ID );

        // Check if faq_id is specified
        if ( ( strIdFaq == null ) || !strIdFaq.matches( REGEX_ID ) )
        {
            page.setContent( getFaqList( request, plugin ) );
            page.setPathLabel( I18nService.getLocalizedString( MESSAGE_HELPDESK_PATH_LABEL, getLocale( request ) ) );
            page.setTitle( I18nService.getLocalizedString( MESSAGE_FAQ_LIST, getLocale( request ) ) );

            return page;
        }

        // Check if faq exists and user have access
        int nIdFaq = Integer.parseInt( strIdFaq );
        Faq faq = FaqHome.load( nIdFaq, plugin );

        if ( faq == null )
        {
            SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );
        }

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );

            if ( !faq.getRoleKey(  ).equals( Faq.ROLE_NONE ) &&
                    ( ( user == null ) || !SecurityService.getInstance(  ).isUserInRole( request, faq.getRoleKey(  ) ) ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );
            }
        }

        // generate pages
        if ( ( strContact != null ) && strContact.equals( PARAMETER_CONTACT ) )
        {
            page.setContent( getContactForm( request, plugin, faq ) );
            page.setPathLabel( I18nService.getLocalizedString( MESSAGE_HELPDESK_PATH_LABEL, getLocale( request ) ) );
            page.setTitle( I18nService.getLocalizedString( MESSAGE_SUBJECT_LIST, getLocale( request ) ) );
        }
        else if ( ( strContact != null ) && strContact.equals( PARAMETER_CONTACT_RESULT ) )
        {
            page.setContent( getContactFormResult( request, faq ) );
            page.setPathLabel( I18nService.getLocalizedString( MESSAGE_HELPDESK_PATH_LABEL, getLocale( request ) ) );
            page.setTitle( I18nService.getLocalizedString( MESSAGE_SUBJECT_LIST, getLocale( request ) ) );
        }
        else
        {
            page.setContent( getSubjectListSearch( request, plugin, faq ) );
            page.setPathLabel( I18nService.getLocalizedString( MESSAGE_HELPDESK_PATH_LABEL, getLocale( request ) ) );
            page.setTitle( I18nService.getLocalizedString( MESSAGE_SUBJECT_LIST_RESULTS, getLocale( request ) ) );
        }

        return page;
    }

    /**
     * Returns the contact form
     * @param request The Html request
     * @param plugin The plugin
     * @param faq The {@link Faq} concerned by contact form
     * @return The Html template
     */
    public String getContactForm( HttpServletRequest request, Plugin plugin, Faq faq )
    {
        Map<String, Object> model = new HashMap<>(  );
        model.put( MARK_THEME_LIST, (Collection<Theme>) ThemeHome.getInstance(  ).findByIdFaq( faq.getId(  ), plugin ) );
        model.put( MARK_PLUGIN, plugin );
        model.put( MARK_DEFAULT_VALUE, "" );
        model.put( MARK_FAQ, faq );
        model.put( FULL_URL, request.getRequestURL(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CONTACT_FORM, getLocale( request ), model );

        return template.getHtml(  );
    }

    /**
     * Processes the sending of a question
     * @param request The Http request
     * @throws SiteMessageException The Site message exception
     */
    public void doSendQuestionMail( HttpServletRequest request )
        throws SiteMessageException
    {
        HashMap<String, Object> model = new HashMap<>(  );

        Plugin plugin = PluginService.getPlugin( HelpdeskPlugin.PLUGIN_NAME );

        String strThemeId = request.getParameter( PARAMETER_THEME_ID );

        String strVisitorLastName = request.getParameter( PARAMETER_LAST_NAME );
        String strVisitorFirstName = request.getParameter( PARAMETER_FIRST_NAME );
        String strVisitorEmail = request.getParameter( PARAMETER_EMAIL );
        String strQuestion = request.getParameter( PARAMETER_QUESTION );
        String strSubject = request.getParameter( PARAMETER_MAIL_SUBJECT );

        // Mandatory field
        if ( ( strVisitorLastName == null ) || ( strVisitorFirstName == null ) ||
                strQuestion.equals( CONSTANT_EMPTY_STRING ) || ( strVisitorEmail == null ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_CONTACT_MANDATORY_FIELDS_ERROR_MESSAGE,
                new String[] { CONSTANT_EMPTY_STRING }, MESSAGE_CONTACT_MANDATORY_FIELDS_TITLE_MESSAGE, null,
                CONSTANT_EMPTY_STRING, SiteMessage.TYPE_STOP );
        }

        if ( strQuestion.length(  ) < 5 )
        {
            SiteMessageService.setMessage( request, MESSAGE_CONTACT_UNSUFFICIENT_CARACTERS_ERROR_MESSAGE,
                new String[] { CONSTANT_EMPTY_STRING }, MESSAGE_CONTACT_UNSUFFICIENT_CARACTERS_TITLE_MESSAGE, null,
                CONSTANT_EMPTY_STRING, SiteMessage.TYPE_STOP );
        }

        //check email
        if ( !StringUtil.checkEmail( strVisitorEmail ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_CONTACT_INVALID_MAIL_ERROR_MESSAGE,
                new String[] { CONSTANT_EMPTY_STRING }, MESSAGE_CONTACT_INVALID_MAIL_TITLE_MESSAGE, null,
                CONSTANT_EMPTY_STRING, SiteMessage.TYPE_STOP );
        }

        String strToday = DateUtil.getCurrentDateString( getLocale( request )  );
        java.sql.Date dateDateVQ = java.sql.Date.valueOf( strToday );

        int nIdTheme = Integer.parseInt( strThemeId );
        Theme theme = (Theme) ThemeHome.getInstance(  ).findByPrimaryKey( nIdTheme, plugin );

        VisitorQuestion visitorQuestion = new VisitorQuestion(  );
        visitorQuestion.setLastname( strVisitorLastName );
        visitorQuestion.setFirstname( strVisitorFirstName );
        visitorQuestion.setEmail( strVisitorEmail );
        visitorQuestion.setQuestion( strQuestion );
        visitorQuestion.setIdTheme( nIdTheme );

        visitorQuestion.setDate( dateDateVQ );
        visitorQuestion.setAnswer( CONSTANT_EMPTY_STRING );

        VisitorQuestionHome.create( visitorQuestion, plugin );

        String strBaseUrl = AppPathService.getBaseUrl( request );

        model.put( MARK_VISITOR_QUESTION, visitorQuestion );
        model.put( MARK_DATE, dateDateVQ );
        model.put( MARK_PORTAL_URL, strBaseUrl );
        model.put( MARK_PLUGIN_NAME, plugin );
        model.put( FULL_URL, request.getRequestURL(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SEND_VISITOR_QUESTION, getLocale( request ),
                model );

        String strPortal = AppPropertiesService.getProperty( MESSAGE_PORTAL_NAME );    
        String strEmailWebmaster = I18nService.getLocalizedString( MESSAGE_WEBMASTER_EMAIL, getLocale( request ) );
        String strMessage = template.getHtml(  );
        strSubject = AppPropertiesService.getProperty( PROPERTY_MAIL_SUBJECT_PREFIX )
    		+ CONSTANT_SPACE + strPortal + CONSTANT_MINUS + strSubject;

        Collection<Recipient> listRecipientTheme = AdminMailingListService.getRecipients( theme.getIdMailingList(  ) );

        for ( Recipient recipientUser : listRecipientTheme )
        {
            strEmailWebmaster = recipientUser.getEmail(  );
            MailService.sendMailHtml( strEmailWebmaster, strPortal, visitorQuestion.getEmail(  ), strSubject, strMessage );
        }
    }

    /**
     * Returns the contact form's result page
     * @param request The Http request
     * @return The Html template
     * @param faq The {@link Faq} concerned by contact form result
     * @throws SiteMessageException The Site message exception
     */
    public String getContactFormResult( HttpServletRequest request, Faq faq )
        throws SiteMessageException
    {
        doSendQuestionMail( request );

        HashMap<String, Object> model = new HashMap<>(  );
        model.put( MARK_FAQ, faq );
        model.put( FULL_URL, request.getRequestURL(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CONTACT_FORM_RESULT, getLocale( request ),
                model );

        return template.getHtml(  );
    }

    /**
     * Returns the contact form's result page
     * @param request The Http request
     * @param faq The {@link Faq} concerned by contact error
     * @return The Html template
     */
    public String getContactFormError( HttpServletRequest request, Faq faq ) //Error must be handled by Message Service
    {
        HashMap<String, Object> model = new HashMap<>(  );
        model.put( MARK_FAQ, faq );
        //useful if you want to work with Portal.jsp and RunStandaloneApp.jsp
        model.put( FULL_URL, request.getRequestURL(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CONTACT_FORM_ERROR, getLocale( request ),
                model );

        return template.getHtml(  );
    }

    /**
     * Returns the list of subjects containing a set of keywords.
     * @param request The Http request
     * @param plugin The plugin
     * @param faq The {@link Faq} concerned by search page
     * @return The Html template
     * @throws SiteMessageException The Site message exception
     */
    private String getSubjectListSearch( HttpServletRequest request, Plugin plugin, Faq faq )
        throws SiteMessageException
    {
        String strKeywords = request.getParameter( PARAMETER_KEYWORDS );
        String strDateBegin = request.getParameter( PARAMETER_DATE_BEGIN );
        String strDateEnd = request.getParameter( PARAMETER_DATE_END );
        String strIdSubject = request.getParameter( PARAMETER_SUBJECT_ID );
        String strSearchSubSubjects = request.getParameter( PARAMETER_SEARCH_SUB_SUBJECTS );
        Subject subject = null;
        boolean bSearchPage = true;
        boolean bSearchSubSubjects = false;
        Collection<QuestionAnswer> listQuestionAnswer = null;
        // Check if filters fields are null
        strKeywords = ( strKeywords == null ) ? CONSTANT_EMPTY_STRING : strKeywords;
        strDateBegin = ( strDateBegin == null ) ? CONSTANT_EMPTY_STRING : strDateBegin;
        strDateEnd = ( strDateEnd == null ) ? CONSTANT_EMPTY_STRING : strDateEnd;
        strIdSubject = ( strIdSubject == null ) ? CONSTANT_EMPTY_STRING : strIdSubject;
        strSearchSubSubjects = ( strSearchSubSubjects == null ) ? CONSTANT_EMPTY_STRING : strSearchSubSubjects;

        if ( ( ( strKeywords.equals( CONSTANT_EMPTY_STRING ) ) && ( strDateBegin.equals( CONSTANT_EMPTY_STRING ) ) &&
                ( strDateEnd.equals( CONSTANT_EMPTY_STRING ) ) && ( strIdSubject.equals( CONSTANT_EMPTY_STRING ) ) ) )
        {
            bSearchPage = false;
        }
        else
        {
            if ( strDateBegin.equals( CONSTANT_EMPTY_STRING ) ^ strDateEnd.equals( CONSTANT_EMPTY_STRING ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_SEARCH_DATE_MANDATORY, SiteMessage.TYPE_STOP );
            }

            Date dateBegin = DateUtil.formatDate( strDateBegin, getLocale( request ) );
            Date dateEnd = DateUtil.formatDate( strDateEnd, getLocale( request ) );

            if ( ( dateBegin == null ) ^ ( dateEnd == null ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_SEARCH_DATE_VALIDITY, SiteMessage.TYPE_STOP );
            }

            if ( strIdSubject.matches( REGEX_ID ) )
            {
                subject = (Subject) SubjectHome.getInstance(  )
                                               .findByPrimaryKey( Integer.parseInt( strIdSubject ), plugin );
            }

            bSearchSubSubjects = Boolean.parseBoolean( strSearchSubSubjects );

            listQuestionAnswer = HelpdeskSearchService.getInstance(  )
                                                      .getSearchResults( faq.getId(  ), strKeywords, dateBegin,
                    dateEnd, subject, bSearchSubSubjects, request, plugin );
        }

        Collection<Subject> listSubjects = (Collection<Subject>) SubjectHome.getInstance(  )
                                                                            .findByIdFaq( faq.getId(  ), plugin );

        Map<String, Object> model = new HashMap<>(  );

        model.put( MARK_SEARCH_PAGE, bSearchPage );
        model.put( MARK_QUESTIONANSWER_LIST, listQuestionAnswer );
        model.put( MARK_PLUGIN, plugin );
        model.put( MARK_FILTER_SEARCHED_KEYWORDS, strKeywords );
        model.put( MARK_FILTER_DATE_BEGIN, strDateBegin );
        model.put( MARK_FILTER_DATE_END, strDateEnd );
        model.put( MARK_FILTER_SUBJECT, strIdSubject );
        model.put( MARK_FILTER_SEARCH_SUB_SUBJECT, bSearchSubSubjects );
        model.put( MARK_PATH_LABEL, HelpdeskPlugin.PLUGIN_NAME );
        model.put( MARK_SUBJECT_LIST, listSubjects );
        model.put( MARK_LOCALE, getLocale( request ) );
        model.put( MARK_ANCHOR_SUBJECT, ANCHOR_SUBJECT );
        model.put( MARK_ANCHOR_QUESTION_ANSWER, ANCHOR_QUESTION_ANSWER );
        model.put( MARK_FAQ, faq );
        //useful if you want to work with Portal.jsp and RunStandaloneApp.jsp
        model.put( FULL_URL, request.getRequestURL(  ) );

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_SUBJECT_LIST, getLocale( request ), model );

        return t.getHtml(  );
    }

    /**
     * Returns the contact form's result page
     * @param request The Http request
     * @param plugin The plugin
     * @return The Html template
     * @throws SiteMessageException The Site message exception
     */
    public String getFaqList( HttpServletRequest request, Plugin plugin )
        throws SiteMessageException
    {
    	String strKeywords = request.getParameter( PARAMETER_KEYWORDS );
        String strDateBegin = request.getParameter( PARAMETER_DATE_BEGIN );
        String strDateEnd = request.getParameter( PARAMETER_DATE_END );
        boolean bSearchPage = true;
        Collection<QuestionAnswer> listQuestionAnswer = null;
        
        if( StringUtils.isBlank( strKeywords ) && StringUtils.isBlank( strDateBegin ) 
        		&& StringUtils.isBlank( strDateEnd ) )
        {
        	bSearchPage = false;
        }
        else
        {
            if ( StringUtils.isBlank( strDateBegin ) ^ StringUtils.isBlank( strDateEnd ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_SEARCH_DATE_MANDATORY, SiteMessage.TYPE_STOP );
            }

            Date dateBegin = DateUtil.formatDate( strDateBegin, getLocale( request ) );
            Date dateEnd = DateUtil.formatDate( strDateEnd, getLocale( request ) );

            if ( ( dateBegin == null ) ^ ( dateEnd == null ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_SEARCH_DATE_VALIDITY, SiteMessage.TYPE_STOP );
            }

            listQuestionAnswer = HelpdeskSearchService.getInstance(  )
                                                      .getSearchResults( strKeywords, dateBegin,
                    dateEnd, request, plugin );
        }

    	
        HashMap<String, Object> model = new HashMap<>(  );
        Collection<Faq> faqList = null;

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            //filter by role
            LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );
            String[] arrayRoleKey = null;

            if ( user != null )
            {
                arrayRoleKey = user.getRoles(  );
            }

            faqList = FaqHome.findAuthorizedFaq( arrayRoleKey, plugin );
        }
        else
        {
            faqList = FaqHome.findAll( plugin );
        }

        model.put( MARK_SEARCH_PAGE, bSearchPage );
        model.put( MARK_QUESTIONANSWER_LIST, listQuestionAnswer );
        model.put( MARK_PLUGIN, plugin );
        model.put( MARK_FILTER_SEARCHED_KEYWORDS, strKeywords );
        model.put( MARK_FILTER_DATE_BEGIN, strDateBegin );
        model.put( MARK_FILTER_DATE_END, strDateEnd );
        model.put( MARK_PATH_LABEL, HelpdeskPlugin.PLUGIN_NAME );
        model.put( MARK_LOCALE, getLocale( request ) );
        model.put( MARK_ANCHOR_SUBJECT, ANCHOR_SUBJECT );
        model.put( MARK_ANCHOR_QUESTION_ANSWER, ANCHOR_QUESTION_ANSWER );
        model.put( MARK_FAQ_LIST, faqList );
        //useful if you want to work with Portal.jsp and RunStandaloneApp.jsp
        model.put( FULL_URL, request.getRequestURL(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FAQ_LIST, getLocale( request ), model );

        return template.getHtml(  );
    }
    
    /**
     * Default getLocale() implementation. Could be overriden
     * 
     * @param request
     *            The HTTP request
     * @return The Locale
     */
    protected Locale getLocale( HttpServletRequest request )
    {
        return LocaleService.getContextUserLocale( request );
    }
}
