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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import au.com.bytecode.opencsv.CSVReader;
import fr.paris.lutece.plugins.helpdesk.business.Faq;
import fr.paris.lutece.plugins.helpdesk.business.FaqHome;
import fr.paris.lutece.plugins.helpdesk.business.QuestionAnswer;
import fr.paris.lutece.plugins.helpdesk.business.QuestionAnswerHome;
import fr.paris.lutece.plugins.helpdesk.business.Subject;
import fr.paris.lutece.plugins.helpdesk.business.SubjectHome;
import fr.paris.lutece.plugins.helpdesk.business.Theme;
import fr.paris.lutece.plugins.helpdesk.business.ThemeHome;
import fr.paris.lutece.plugins.helpdesk.business.VisitorQuestion;
import fr.paris.lutece.plugins.helpdesk.business.VisitorQuestionHome;
import fr.paris.lutece.plugins.helpdesk.service.FaqResourceIdService;
import fr.paris.lutece.plugins.helpdesk.service.search.HelpdeskIndexer;
import fr.paris.lutece.plugins.helpdesk.utils.HelpdeskIndexerUtils;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.filesystem.UploadUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;


/**
 * This class provides the user interface to manage helpdesk features ( manage,
 * create, modify, remove)
 */
public class HelpdeskJspBean extends PluginAdminPageJspBean
{
    //right
    public static final String RIGHT_MANAGE_HELPDESK = "HELPDESK_MANAGEMENT";

    //Jsp
    private static final String JSP_MANAGE_HELPDESK = "ManageHelpdesk.jsp";
    private static final String JSP_SUBJECTS_LIST = "SubjectsList.jsp";
    private static final String JSP_QUESTION_ANSWER_LIST = "QuestionAnswerList.jsp";
    private static final String JSP_VISITOR_QUESTION_LIST = "VisitorQuestionList.jsp";
    private static final String JSP_CREATE_QUESTION_ANSWER = "CreateQuestionAnswer.jsp";
    private static final String JSP_ARCHIVED_QUESTION_LIST = "ArchivedQuestionList.jsp";
    private static final String JSP_MANAGE_HELPDESK_LIST = "ManageHelpdeskAdmin.jsp";
    private static final String JSP_DO_REMOVE_SUBJECT = "jsp/admin/plugins/helpdesk/DoRemoveSubject.jsp";
    private static final String JSP_DO_REMOVE_QUESTION_ANSWER = "jsp/admin/plugins/helpdesk/DoRemoveQuestionAnswer.jsp";
    private static final String JSP_LIST_SUBJECTS = "jsp/admin/plugins/helpdesk/SubjectsList.jsp";
    private static final String JSP_HELPDESK_MANAGE = "jsp/admin/plugins/helpdesk/ManageHelpdesk.jsp";
    private static final String JSP_LIST_QUESTIONS_ANSWER = "jsp/admin/plugins/helpdesk/QuestionAnswerList.jsp";
    private static final String JSP_MANAGE_HELPDESK_ADMIN = "jsp/admin/plugins/helpdesk/ManageHelpdeskAdmin.jsp";
    private static final String JSP_DO_REMOVE_THEME = "jsp/admin/plugins/helpdesk/DoRemoveTheme.jsp";
    private static final String JSP_DO_REMOVE_FAQ = "jsp/admin/plugins/helpdesk/DoRemoveFaq.jsp";
    private static final String JSP_DO_REMOVE_SELECTION = "jsp/admin/plugins/helpdesk/DoRemoveSelection.jsp";
    private static final String JSP_DO_IMPORT_CSV = "jsp/admin/plugins/helpdesk/DoConfirmImportCSV.jsp";

    //Parameters
    private static final String PARAMETER_LAST_NAME = "last_name";
    private static final String PARAMETER_FIRST_NAME = "first_name";
    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_QUESTION = "question";
    private static final String PARAMETER_QUESTION_ID = "question_id";
    private static final String PARAMETER_STATUS = "status";
    private static final String PARAMETER_ANSWER = "answer";
    private static final String PARAMETER_SUBJECT_ID = "subject_id";
    private static final String PARAMETER_SUBJECT = "subject";
    private static final String PARAMETER_MAIL_SUBJECT = "mail_subject";
    private static final String PARAMETER_PARENT_ID = "parent_id";
    private static final String PARAMETER_ADD_QUESTION_ANSWER = "add_question_answer";
    private static final String PARAMETER_CSV_FILE = "csv_file";
    private static final String PARAMETER_DELETE_LIST = "delete_list";
    private static final String PARAMETER_THEME = "theme";
    private static final String PARAMETER_THEME_ID = "theme_id";
    private static final String PARAMETER_QUESTION_MAILINGLIST = "mailinglists";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_ROLE_KEY = "role_key";
    private static final String PARAMETER_WORKGROUP_KEY = "workgroup_key";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_FAQ_ID = "faq_id";
    private static final String PARAMETER_CONTENT_HTML = "html_content";
    private static final String PARAMETER_NB_ITEMS_PER_PAGE = "items_per_page";   
    private static final String PARAMETER_QUESTION_SELECTION = "question_selection";
    private static final String PARAMETER_SELECTION = "selection";

    //Templates
    private static final String TEMPLATE_SUBJECTS = "admin/plugins/helpdesk/subjects.html";
    private static final String TEMPLATE_MANAGE_HELPDESK = "admin/plugins/helpdesk/manage_helpdesk.html";
    private static final String TEMPLATE_CREATE_SUBJECT = "admin/plugins/helpdesk/create_subject.html";
    private static final String TEMPLATE_MODIFY_SUBJECT = "admin/plugins/helpdesk/modify_subject.html";
    private static final String TEMPLATE_QUESTION_ANSWER_LIST = "admin/plugins/helpdesk/question_answer_list.html";
    private static final String TEMPLATE_CREATE_QUESTION_ANSWER = "admin/plugins/helpdesk/create_question_answer.html";
    private static final String TEMPLATE_MODIFY_QUESTION_ANSWER = "admin/plugins/helpdesk/modify_question_answer.html";
    private static final String TEMPLATE_MODIFY_VISITOR_QUESTION = "admin/plugins/helpdesk/modify_visitor_question.html";
    private static final String TEMPLATE_ANSWER_SELECTION = "admin/plugins/helpdesk/answer_selection.html";
    private static final String TEMPLATE_VISITOR_QUESTION_LIST = "admin/plugins/helpdesk/visitor_question_list.html";
    private static final String TEMPLATE_VIEW_VISITOR_QUESTION = "admin/plugins/helpdesk/view_visitor_question.html";
    private static final String TEMPLATE_SEND_ANSWER = "admin/plugins/helpdesk/send_answer.html";
    private static final String TEMPLATE_ARCHIVED_QUESTION_LIST = "admin/plugins/helpdesk/archived_question_list.html";
    private static final String TEMPLATE_THEME_LIST = "admin/plugins/helpdesk/themes.html";
    private static final String TEMPLATE_CREATE_THEME = "admin/plugins/helpdesk/create_theme.html";
    private static final String TEMPLATE_MODIFY_THEME = "admin/plugins/helpdesk/modify_theme.html";
    private static final String TEMPLATE_CREATE_FAQ = "admin/plugins/helpdesk/create_faq.html";
    private static final String TEMPLATE_MODIFY_FAQ = "admin/plugins/helpdesk/modify_faq.html";
    private static final String TEMPLATE_IMPORT_QUESTION_ANSWER_LIST = "admin/plugins/helpdesk/import_question_answer_list.html";
    private static final String TEMPLATE_SEND_NEW_QUESTION_NOTIFICATION = "admin/plugins/helpdesk/send_notification.html";

    //Properties
    private static final String PROPERTY_IMPORT_DELIMITER = "csv.import.delimiter";
    private static final String PROPERTY_CSV_FILE_EXTENSION = "csv.import.extension";
    private static final String PROPERTY_CSV_FILE_CHARSET = "csv.import.charset";
    private static final String PROPERTY_CSV_HEADER = "csv.import.header";
    private static final String PROPERTY_CSV_COLUMNS_LIST = "csv.import.columns";
    private static final String PROPERTY_CHECK = "checked";
    private static final String PROPERTY_NULL = "";
    private static final String PROPERTY_STYLES_PER_PAGE = "paginator.style.itemsPerPage";
    private static final String PROPERTY_RESULTS_PER_PAGE = "helpdesk.healpdesksearch.nb.docs.per.page";
    private static final String PROPERTY_MAIL_SUBJECT_PREFIX = "mail.helpdesk.subjectPrefix";
    
    //Messages
    private static final String MESSAGE_PAGE_TITLE_SUBJECT_LIST = "helpdesk.subjects.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_FAQ_LIST = "helpdesk.faq.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_CREATE_SUBJECT = "helpdesk.create_subject.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_CREATE_FAQ = "helpdesk.create_faq.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_QUESTION_ANSWER_LIST = "helpdesk.question_answer_list.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_CREATE_QUESTION_ANSWER = "helpdesk.create_question_answer.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_MODIFY_QUESTION_ANSWER = "helpdesk.modify_question_answer.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_MODIFY_VISITOR_QUESTION = "helpdesk.modify_visitor_question.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_VISITOR_QUESTION_LIST = "helpdesk.visitor_question_list.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_ARCHIVED_QUESTION_LIST = "helpdesk.archived_question_list.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_MODIFY_SUBJECT = "helpdesk.modify_subject.pageTitle";
    private static final String MESSAGE_PORTAL_NAME = "lutece.name";
    private static final String MESSAGE_MAIL_HELPDESK_SENDER = "mail.helpdesk.sender";
    private static final String MESSAGE_PAGE_TITLE_CREATE_THEME = "helpdesk.create_theme.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_MODIFY_THEME = "helpdesk.modify_theme.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_THEMES = "helpdesk.themes.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_MODIFY_FAQ = "helpdesk.modify_faq.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_IMPORT_QUESTION_ANSWER_LIST = "helpdesk.import_question_answer_list.pageTitle";
    private static final String MESSAGE_CONFIRM_DELETE_SUBJECT = "helpdesk.message.confirmDeleteSubject";
    private static final String MESSAGE_CANNOT_DELETE_SUBJECT = "helpdesk.message.cannotDeleteSubject";
    private static final String MESSAGE_CONFIRM_DELETE_QUESTION_ANSWER = "helpdesk.message.confirmDeleteQuestionAnswer";
    private static final String MESSAGE_CSV_FILE_NOT_VALID = "helpdesk.message.fileNotValid";
    private static final String MESSAGE_CSV_FIELD_ERROR = "helpdesk.message.fieldError";
    private static final String MESSAGE_CSV_FILE_EMPTY = "helpdesk.message.fileEmpty";
    private static final String MESSAGE_CSV_COLUMNS_LIST_ERROR = "helpdesk.message.csvColumnListError";
    private static final String MESSAGE_IMPORT_CSV_OK = "helpdesk.message.csvImportOk";
    private static final String MESSAGE_CONFIRM_DELETE_THEME = "helpdesk.message.confirmDeleteTheme";
    private static final String MESSAGE_CANNOT_DELETE_THEME_QUESTION_EXISTS = "helpdesk.message.cannotDeleteThemeQuestionExists";
    private static final String MESSAGE_CANNOT_DELETE_THEME_SUB_THEME_EXISTS = "helpdesk.message.cannotDeleteThemeSubThemeExists";
    private static final String MESSAGE_NO_MAILING_LIST_EXIST_THEME = "helpdesk.message.noMailingListExistTheme";
    private static final String MESSAGE_CANNOT_DELETE_FAQ = "helpdesk.message.cannotDeleteFaq";
    private static final String MESSAGE_CONFIRM_DELETE_FAQ = "helpdesk.message.confirmDeleteFaq";
    private static final String MESSAGE_ACCESS_DENIED = "helpdesk.message.accessDenied";
    private static final String MESSAGE_NEW_QUESTION = "helpdesk.message.newQuestion";
    private static final String MESSAGE_CONFIRM_DELETE_SELECTION = "helpdesk.message.confirmDeleteSelection";
    private static final String MESSAGE_CONFIRM_IMPORT_WITHOUT_DELETING = "helpdesk.message.confirmImportWithoutDeleting";

    //Markers
    private static final String MARK_VISITOR_QUESTION = "visitor_question";
    private static final String MARK_PORTAL_URL = "portal_url";
    private static final String MARK_ARCHIVED_QUESTION_LIST = "helpdesk_archived_question_list";
    private static final String MARK_SUBJECT_LIST = "helpdesk_subject_list";
    private static final String MARK_FAQ_LIST = "helpdesk_faq_list";
    private static final String MARK_DEFAULT_VALUE = "default_value";
    private static final String MARK_PLUGIN = "plugin";
    private static final String MARK_SUBJECT = "subject";
    private static final String MARK_QUESTION_LIST = "helpdesk_question_list";
    private static final String MARK_QUESTION_ANSWER = "question_answer";
    private static final String MARK_CHECKED = "checked";
    private static final String MARK_ANSWER = "answer";
    private static final String MARK_QUESTION = "question";
    private static final String MARK_HELPDESK_USER = "helpdesk_user";
    private static final String MARK_QUESTION_ANSWER_LIST = "question_answer_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_HTML_CONTENT = "html_content";
    private static final String MARK_THEME = "theme";
    private static final String MARK_THEME_LIST = "helpdesk_theme_list";
    private static final String MARK_THEME_ID = "theme_id";
    private static final String MARK_MAILINGLISTS_LIST = "mailing_list";
    private static final String MARK_FAQ = "faq";
    private static final Object MARK_ROLE_KEY_LIST = "role_key_list";
    private static final Object MARK_DEFAULT_VALUE_ROLE_KEY = "role_key_default_value";
    private static final Object MARK_DEFAULT_VALUE_WORKGROUP_KEY = "workgroup_key_default_value";
    private static final Object MARK_WORKGROUP_KEY_LIST = "workgroup_key_list";
    private static final String MARK_FAQ_ID = "faq_id";

    //Default values
    private static final String DEFAULT_CSV_FILE_EXTENSION = "csv";
    private static final String DEFAULT_CSV_FILE_CHARSET = "UTF-8";
    private static final String DEFAULT_IMPORT_DELIMITER = ";";
    private static final String DEFAULT_CSV_HEADER = "false";
    private static final String DEFAULT_CSV_COLUMNS = "subject_id,id_order,question,answer";
    private static final String DEFAULT_CONSTANT_DELIMITER_COLUMNS_LIST = ",";
    private static final String DEFAULT_RESULTS_PER_PAGE = "10";

    //Others
    private static final int ROOT_SUBJECT_ID = 0;
    private static final String REGEX_ID = "^[\\d]+$";
    private static final String CONSTANT_SPACE = " ";
    private static final String CONSTANT_MINUS = " - ";
    private static final String CONSTANT_PUBLISH = "publish";
    private static final String CONSTANT_UNPUBLISH = "unpublish";
    private static final String CONSTANT_REMOVE = "remove";
    private int _nItemsPerPage;
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private String[] _multiSelectionValues;
    private FileItem _csvItem;

    /**
     * Creates a new HelpdeskJspBean object.
     */
    public HelpdeskJspBean(  )
    {
    }

    /**
     * Returns the list of subjects
     * @param request The Http request
     * @return The Html template
     */
    public String getSubjectsList( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return getManageHelpdesk( request );
        }

        setPageTitleProperty( MESSAGE_PAGE_TITLE_SUBJECT_LIST );

        Collection<Subject> listSubject = (Collection<Subject>) SubjectHome.getInstance(  )
                                                                           .findByIdFaq( faq.getId(  ), getPlugin(  ) );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_STYLES_PER_PAGE, 10 );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_LIST_SUBJECTS );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        Paginator paginator = new Paginator( (List<Subject>) listSubject, _nItemsPerPage, url.getUrl(  ),
                Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_SUBJECT_LIST, paginator.getPageItems(  ) );
        model.put( MARK_PLUGIN, getPlugin(  ) );
        model.put( MARK_FAQ, faq );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SUBJECTS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the subject creation form
     * @param request The Http request
     * @return The Html template
     */
    public String getCreateSubject( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return getSubjectsList( request );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_CREATE_SUBJECT );

        Collection<Subject> listSubjects = (Collection<Subject>) SubjectHome.getInstance(  )
                                                                            .findByIdFaq( faq.getId(  ), getPlugin(  ) );
        model.put( MARK_SUBJECT_LIST, listSubjects );
        model.put( MARK_PLUGIN, getPlugin(  ) );
        model.put( MARK_DEFAULT_VALUE, ROOT_SUBJECT_ID );
        model.put( MARK_FAQ_ID, request.getParameter( PARAMETER_FAQ_ID ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_SUBJECT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes subject creation
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doCreateSubject( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        String strParentId = request.getParameter( PARAMETER_PARENT_ID );

        // Mandatory field
        if ( ( strSubject == null ) || strSubject.equals( "" ) || ( strParentId == null ) ||
                !strParentId.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Subject subject = new Subject(  );
        subject.setText( strSubject );
        subject.setIdParent( Integer.parseInt( strParentId ) );
        subject = (Subject) SubjectHome.getInstance(  ).create( subject, faq.getId(  ), getPlugin(  ) );

        if ( subject.getIdParent(  ) == 0 )
        {
            SubjectHome.getInstance(  ).createLinkToFaq( subject.getId(  ), faq.getId(  ), getPlugin(  ) );
        }

        // If the operation is successfull, redirect towards the list of subjects
        UrlItem url = new UrlItem( JSP_SUBJECTS_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return url.getUrl(  );
    }

    /**
     * Move the subject down
     * @param request The Http servlet request
     * @return The redirect url
     */
    public String doGoDownSubject( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strIdSubject = request.getParameter( PARAMETER_SUBJECT_ID );
        int nIdSubject = Integer.parseInt( strIdSubject );
        SubjectHome.getInstance(  ).goDown( nIdSubject, faq.getId(  ), getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_SUBJECTS_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return url.getUrl(  );
    }

    /**
     * Move the subject up
     * @param request The Http servlet request
     * @return The redirect url
     */
    public String doGoUpSubject( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strIdSubject = request.getParameter( PARAMETER_SUBJECT_ID );
        int nIdSubject = Integer.parseInt( strIdSubject );
        SubjectHome.getInstance(  ).goUp( nIdSubject, faq.getId(  ), getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_SUBJECTS_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return url.getUrl(  );
    }

    /**
     * Move the subject in a parent subject
     * @param request The Http servlet request
     * @return The redirect url
     */
    public String doGoInSubject( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_SUBJECTS_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        String strIdSubject = request.getParameter( PARAMETER_SUBJECT_ID );
        int nIdSubject = Integer.parseInt( strIdSubject );
        Subject subject = (Subject) SubjectHome.getInstance(  ).findByPrimaryKey( nIdSubject, getPlugin(  ) );

        if ( subject == null )
        {
            return url.getUrl(  );
        }

        if ( subject.getIdParent(  ) == 0 )
        {
            SubjectHome.getInstance(  ).removeLinkToFaq( nIdSubject, faq.getId(  ), getPlugin(  ) );
        }

        SubjectHome.getInstance(  ).goIn( nIdSubject, faq.getId(  ), getPlugin(  ) );

        return url.getUrl(  );
    }

    /**
     * Move the subject out a parent subject
     * @param request The Http servlet request
     * @return The redirect url
     */
    public String doGoOutSubject( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_SUBJECTS_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        String strIdSubject = request.getParameter( PARAMETER_SUBJECT_ID );
        int nIdSubject = Integer.parseInt( strIdSubject );
        SubjectHome.getInstance(  ).goOut( nIdSubject, faq.getId(  ), getPlugin(  ) );

        Subject subject = (Subject) SubjectHome.getInstance(  ).findByPrimaryKey( nIdSubject, getPlugin(  ) );

        if ( subject == null )
        {
            return url.getUrl(  );
        }

        if ( subject.getIdParent(  ) == 0 )
        {
            SubjectHome.getInstance(  ).removeLinkToFaq( nIdSubject, faq.getId(  ), getPlugin(  ) );
            SubjectHome.getInstance(  ).createLinkToFaq( nIdSubject, faq.getId(  ), getPlugin(  ) );
        }

        return url.getUrl(  );
    }

    /**
     * Returns the subject modification form
     * @param request The Http request
     * @return The Html template
     */
    public String getModifySubject( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return getSubjectsList( request );
        }

        HashMap model = new HashMap(  );

        setPageTitleProperty( MESSAGE_PAGE_TITLE_MODIFY_SUBJECT );

        String strIdSubject = request.getParameter( PARAMETER_SUBJECT_ID );

        int nIdSubject = Integer.parseInt( strIdSubject );
        Subject subject = (Subject) SubjectHome.getInstance(  ).findByPrimaryKey( nIdSubject, getPlugin(  ) );

        if ( subject == null )
        {
            return getSubjectsList( request );
        }

        model.put( MARK_SUBJECT, subject );
        model.put( MARK_PLUGIN, getPlugin(  ) );
        model.put( MARK_FAQ_ID, faq.getId(  ) );

        Collection<Subject> listSubjects = (Collection<Subject>) SubjectHome.getInstance(  )
                                                                            .findByIdFaq( faq.getId(  ), getPlugin(  ) );
        model.put( MARK_SUBJECT_LIST, listSubjects );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_SUBJECT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes subject modification
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doModifySubject( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        String strIdSubject = request.getParameter( PARAMETER_SUBJECT_ID );
        String strIdParent = request.getParameter( PARAMETER_PARENT_ID );

        // Mandatory field
        if ( request.getParameter( PARAMETER_SUBJECT ).equals( "" ) || ( strIdParent == null ) ||
                !strIdParent.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdSubject = Integer.parseInt( strIdSubject );
        int nIdParent = Integer.parseInt( strIdParent );

        //FIXME check if the parent space is a child space from current subject
        Subject subject = (Subject) SubjectHome.getInstance(  ).findByPrimaryKey( nIdSubject, getPlugin(  ) );
        subject.setText( strSubject );

        //If the parent subject have been modified, then set the order to 0
        if ( nIdParent != subject.getIdParent(  ) )
        {
            if ( subject.getIdParent(  ) == 0 )
            {
                SubjectHome.getInstance(  ).removeLinkToFaq( nIdSubject, faq.getId(  ), getPlugin(  ) );
            }

            if ( nIdParent == 0 )
            {
                SubjectHome.getInstance(  ).createLinkToFaq( nIdSubject, faq.getId(  ), getPlugin(  ) );
            }

            subject.setIdParent( nIdParent );
            subject.setIdOrder( SubjectHome.FIRST_ORDER );
        }

        SubjectHome.getInstance(  ).update( subject, faq.getId(  ), getPlugin(  ) );

        // If the operation is successfull, redirect towards the list of subjects
        UrlItem url = new UrlItem( JSP_SUBJECTS_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return url.getUrl(  );
    }

    /**
     * Returns the subject removal form
     * @param request The Http request
     * @return The Html template
     */
    public String getConfirmRemoveSubject( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        int nIdSubject = Integer.parseInt( request.getParameter( PARAMETER_SUBJECT_ID ) );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_SUBJECT );
        url.addParameter( PARAMETER_SUBJECT_ID, nIdSubject );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        Subject subject = (Subject) SubjectHome.getInstance(  ).findByPrimaryKey( nIdSubject, getPlugin(  ) );

        if ( ( subject == null ) || ( subject.getChilds( getPlugin(  ) ).size(  ) > 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_DELETE_SUBJECT, AdminMessage.TYPE_STOP );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_SUBJECT, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Processes subject removal
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doRemoveSubject( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        int nIdSubject = Integer.parseInt( request.getParameter( PARAMETER_SUBJECT_ID ) );

        if ( QuestionAnswerHome.countbySubject( nIdSubject, getPlugin(  ) ) > 0 )
        {
            QuestionAnswerHome.removeBySubject( nIdSubject, getPlugin(  ) );
        }

        Subject subject = (Subject) SubjectHome.getInstance(  ).findByPrimaryKey( nIdSubject, getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_SUBJECTS_LIST );
        url.addParameter( PARAMETER_SUBJECT_ID, nIdSubject );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        //FIXME display error message
        if ( ( subject == null ) || ( subject.getChilds( getPlugin(  ) ).size(  ) > 0 ) )
        {
            return url.getUrl(  );
        }

        if ( subject.getIdParent(  ) == 0 )
        {
            SubjectHome.getInstance(  ).removeLinkToFaq( subject.getId(  ), faq.getId(  ), getPlugin(  ) );
        }

        SubjectHome.getInstance(  ).remove( nIdSubject, faq.getId(  ), getPlugin(  ) );

        // If the operation is successfull, redirect towards the list of subjects
        return url.getUrl(  );
    }

    /**
     * Returns the list of QuestionAnswer
     * @param request The Http request
     * @return The Html template
     */
    public String getQuestionAnswerList( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return getManageHelpdesk( request );
        }

        HashMap model = new HashMap(  );

        String strIdSubject = request.getParameter( PARAMETER_SUBJECT_ID );
        Subject subject = null;

        if ( ( strIdSubject != null ) && strIdSubject.matches( REGEX_ID ) )
        {
            int nIdSubject = Integer.parseInt( strIdSubject );
            subject = (Subject) SubjectHome.getInstance(  ).findByPrimaryKey( nIdSubject, getPlugin(  ) );
        }

        setPageTitleProperty( MESSAGE_PAGE_TITLE_QUESTION_ANSWER_LIST );

        Collection<Subject> listSubject = (Collection<Subject>) SubjectHome.getInstance(  )
                                                                           .findByIdFaq( faq.getId(  ), getPlugin(  ) );

        if ( ( subject == null ) && ( listSubject.size(  ) > 0 ) )
        {
            subject = listSubject.iterator(  ).next(  );
        }

        String strNbItemPerPage = request.getParameter( PARAMETER_NB_ITEMS_PER_PAGE );
        String strDefaultNbItemPerPage = AppPropertiesService.getProperty( PROPERTY_RESULTS_PER_PAGE,
                DEFAULT_RESULTS_PER_PAGE );
        strNbItemPerPage = ( strNbItemPerPage != null ) ? strNbItemPerPage : strDefaultNbItemPerPage;
        
        model.put( MARK_SUBJECT_LIST, listSubject );
        model.put( MARK_PLUGIN, getPlugin(  ) );
        model.put( MARK_SUBJECT, subject );
        model.put( MARK_FAQ, faq );
        model.put( MARK_NB_ITEMS_PER_PAGE, strNbItemPerPage );
        model.put( MARK_LOCALE, request.getLocale(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_QUESTION_ANSWER_LIST, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }
    
    /**
     * Move the question down
     * @param request The Http servlet request
     * @return The redirect url
     */
    public String doGoDownQuestion( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strIdQuestion = request.getParameter( PARAMETER_QUESTION_ID );
        int nIdQuestion = Integer.parseInt( strIdQuestion );
        QuestionAnswerHome.goDown( nIdQuestion, getPlugin(  ) );

        QuestionAnswer questionAnswer = QuestionAnswerHome.findByPrimaryKey(nIdQuestion, getPlugin(  ) );
        
        UrlItem url = new UrlItem( JSP_QUESTION_ANSWER_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );
        url.addParameter( PARAMETER_SUBJECT_ID, questionAnswer.getIdSubject(  ) );

        return url.getUrl(  );
    }

    /**
     * Move the question up
     * @param request The Http servlet request
     * @return The redirect url
     */
    public String doGoUpQuestion( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strIdQuestion = request.getParameter( PARAMETER_QUESTION_ID );
        int nIdQuestion = Integer.parseInt( strIdQuestion );
        QuestionAnswerHome.goUp( nIdQuestion, getPlugin(  ) );
        
        QuestionAnswer questionAnswer = QuestionAnswerHome.findByPrimaryKey(nIdQuestion, getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_QUESTION_ANSWER_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );
        url.addParameter( PARAMETER_SUBJECT_ID, questionAnswer.getIdSubject(  ) );

        return url.getUrl(  );
    }

    /**
     * Returns the QuestionAnswer creation form
     * @param request The Http request
     * @return The Html template
     */
    public String getCreateQuestionAnswer( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return getQuestionAnswerList( request );
        }

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_CREATE_QUESTION_ANSWER );

        String strSubjectId = request.getParameter( PARAMETER_SUBJECT_ID );
        String strIdQuestion = request.getParameter( PARAMETER_QUESTION_ID );
        String strIdTheme = request.getParameter( PARAMETER_THEME_ID );
        
        model.put( MARK_SUBJECT_LIST,
            (Collection<Subject>) SubjectHome.getInstance(  ).findByIdFaq( faq.getId(  ), getPlugin(  ) ) );
        model.put( MARK_DEFAULT_VALUE, ( strSubjectId == null ) ? "" : strSubjectId );
        model.put( MARK_PLUGIN, getPlugin(  ) );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_FAQ_ID, faq.getId(  ) );
        model.put( MARK_THEME_ID, strIdTheme);

        if ( strIdQuestion == null )
        {
            model.put( MARK_QUESTION, "" );
            model.put( MARK_HTML_CONTENT, "" );
        }
        else
        {
            VisitorQuestion visitorQuestion = VisitorQuestionHome.findByPrimaryKey( Integer.parseInt( strIdQuestion ),
                    getPlugin(  ) );
            model.put( MARK_QUESTION, visitorQuestion.getQuestion(  ) );
            model.put( MARK_HTML_CONTENT, visitorQuestion.getAnswer(  ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_QUESTION_ANSWER, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes QuestionAnswer creation
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doCreateQuestionAnswer( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strIdSubject = request.getParameter( PARAMETER_SUBJECT_ID );
        String strQuestion = request.getParameter( PARAMETER_QUESTION );
        String strAnswer = request.getParameter( PARAMETER_CONTENT_HTML );
        String strStatus = request.getParameter( PARAMETER_STATUS );
        String strThemeId = request.getParameter( PARAMETER_THEME_ID );
        int nStatus = 0;

        if ( strStatus != null )
        {
            nStatus = Integer.parseInt( strStatus );
        }

        // Mandatory field
        if ( ( strIdSubject == null ) || ( strQuestion == null ) || ( strAnswer == null ) || strIdSubject.equals( "" ) ||
                strQuestion.equals( "" ) || strAnswer.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdSubject = Integer.parseInt( strIdSubject );
        QuestionAnswer questionAnswer = new QuestionAnswer(  );
        questionAnswer.setIdSubject( nIdSubject );
        questionAnswer.setQuestion( strQuestion );
        questionAnswer.setAnswer( strAnswer );
        questionAnswer.setStatus( nStatus );
        questionAnswer.setCreationDate( new Date(  ) );

        QuestionAnswerHome.create( questionAnswer, getPlugin(  ) );
        
        if( strThemeId != null )
        {
        	//send a mail to warn that a new question is published
        	Subject subject = ( Subject ) SubjectHome.getInstance( ).findByPrimaryKey(nIdSubject, getPlugin( ) );
        	int nIdTheme = Integer.parseInt( strThemeId );
            Theme theme = (Theme) ThemeHome.getInstance(  ).findByPrimaryKey( nIdTheme, getPlugin(  ) );            
          
            Collection<Recipient> listRecipientTheme = AdminMailingListService.getRecipients( theme.getIdMailingList(  ) );
            
            String strPortal = AppPropertiesService.getProperty( MESSAGE_PORTAL_NAME );
            String strEmailSender = AppPropertiesService.getProperty( MESSAGE_MAIL_HELPDESK_SENDER );
            String strSubject = AppPropertiesService.getProperty( PROPERTY_MAIL_SUBJECT_PREFIX )
            	+ CONSTANT_SPACE + strPortal + CONSTANT_MINUS
            	+ I18nService.getLocalizedString( MESSAGE_NEW_QUESTION, request.getLocale(  ) );
        
            
            model.put( MARK_QUESTION, strQuestion );
            model.put( MARK_ANSWER, strAnswer );
            model.put( MARK_SUBJECT, subject.getText( ) );
            
            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SEND_NEW_QUESTION_NOTIFICATION, request.getLocale(  ),
                    model );
            String strMessage = template.getHtml(  );
            for ( Recipient recipientUser : listRecipientTheme )
            {
                String strEmailWebmaster = recipientUser.getEmail(  );
                MailService.sendMailHtml( strEmailWebmaster, strPortal, strEmailSender, strSubject, strMessage );
            }            
        }

        // If the operation is successful, redirect towards the list of question/answer couples
        UrlItem url = new UrlItem( JSP_QUESTION_ANSWER_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );
        url.addParameter( PARAMETER_SUBJECT_ID, questionAnswer.getIdSubject(  ) );

        return url.getUrl(  );
    }

    /**
     * Returns the QuestionAnswer modification
     *
     * @param request The Http request
     * @return The Html template
     */
    public String getModifyQuestionAnswer( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return getQuestionAnswerList( request );
        }

        HashMap model = new HashMap(  );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_MODIFY_QUESTION_ANSWER );

        int nIdQuestionAnswer = Integer.parseInt( request.getParameter( PARAMETER_QUESTION_ID ) );

        QuestionAnswer questionAnswer = QuestionAnswerHome.findByPrimaryKey( nIdQuestionAnswer, getPlugin(  ) );

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_HTML_CONTENT, questionAnswer.getAnswer(  ) );
        model.put( MARK_FAQ_ID, faq.getId(  ) );
        model.put( MARK_SUBJECT_LIST,
            (Collection<Subject>) SubjectHome.getInstance(  ).findByIdFaq( faq.getId(  ), getPlugin(  ) ) );
        model.put( MARK_PLUGIN, getPlugin(  ) );

        questionAnswer.setAnswer( questionAnswer.getAnswer(  ) );

        if ( questionAnswer.isEnabled(  ) )
        {
            model.put( MARK_CHECKED, PROPERTY_CHECK );
        }
        else
        {
            model.put( MARK_CHECKED, PROPERTY_NULL );
        }

        model.put( MARK_QUESTION_ANSWER, questionAnswer );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_QUESTION_ANSWER, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes QuestionAnswer modification
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doModifyQuestionAnswer( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        int nIdQuestionAnswer = Integer.parseInt( request.getParameter( PARAMETER_QUESTION_ID ) );
        int nIdSubject = Integer.parseInt( request.getParameter( PARAMETER_SUBJECT_ID ) );
        String strQuestion = request.getParameter( PARAMETER_QUESTION );
        String strAnswer = request.getParameter( PARAMETER_CONTENT_HTML );

        int nStatus = 0;

        if ( request.getParameter( PARAMETER_STATUS ) != null )
        {
            nStatus = 1;
        }

        QuestionAnswer questionAnswer = QuestionAnswerHome.findByPrimaryKey(nIdQuestionAnswer, getPlugin( ));
        if( questionAnswer.getIdSubject( ) !=  nIdSubject )
        {
        	questionAnswer.setIdOrder( QuestionAnswerHome.FIRST_ORDER );
        	questionAnswer.setIdSubject( nIdSubject );
        }        
        questionAnswer.setQuestion( strQuestion );
        questionAnswer.setAnswer( strAnswer );
        questionAnswer.setStatus( nStatus );
        questionAnswer.setCreationDate( new Date(  ) );

        // Mandatory field
        if ( request.getParameter( PARAMETER_SUBJECT_ID ).equals( "" ) ||
                request.getParameter( ( PARAMETER_QUESTION ) ).equals( "" ) ||
                request.getParameter( PARAMETER_CONTENT_HTML ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        QuestionAnswerHome.update( questionAnswer, getPlugin(  ) );

        // If the operation is successfull, redirect towards the list of question/answer couples
        UrlItem url = new UrlItem( JSP_QUESTION_ANSWER_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );
        url.addParameter( PARAMETER_SUBJECT_ID, questionAnswer.getIdSubject(  ) );

        return url.getUrl(  );
    }
    /**
     * Processes QuestionAnswer action for multiselection
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doActionSelectionQuestionAnswer( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }
        String strAction = request.getParameter( PARAMETER_SELECTION );
        String[] strIdQuestionAnswers = (String[]) request.getParameterMap(  ).get( PARAMETER_QUESTION_SELECTION );
        _multiSelectionValues = strIdQuestionAnswers;
        String strIdSubject = request.getParameter( PARAMETER_SUBJECT_ID );

    	if( strAction.equals( CONSTANT_REMOVE ) )
    	{    		
            
    		UrlItem url = new UrlItem( JSP_DO_REMOVE_SELECTION );
            
            url.addParameter( PARAMETER_SUBJECT_ID, strIdSubject );
            url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

            return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_SELECTION, url.getUrl(  ),
                AdminMessage.TYPE_CONFIRMATION );
    	}
        
        for( String strIdQuestionAnswer : strIdQuestionAnswers )
        {
        	int nIdQuestionAnswer = Integer.parseInt( strIdQuestionAnswer );

            QuestionAnswer questionAnswer = QuestionAnswerHome.findByPrimaryKey( nIdQuestionAnswer, getPlugin(  ) );

            if ( questionAnswer != null )
            {
            	if( ( strAction.equals( CONSTANT_PUBLISH ) ) && ( ! questionAnswer.isEnabled( ) ) )
            	{
            		 questionAnswer.setStatus( QuestionAnswer.STATUS_PUBLISHED );
                     QuestionAnswerHome.update( questionAnswer, getPlugin(  ) );
            	}
            	else if( ( strAction.equals( CONSTANT_UNPUBLISH ) ) && (  questionAnswer.isEnabled( ) ) )
            	{
           		 	questionAnswer.setStatus( QuestionAnswer.STATUS_UNPUBLISHED );
                    QuestionAnswerHome.update( questionAnswer, getPlugin(  ) );
            	}
               
            }          
        }
        

        // If the operation is successfull, redirect towards the list of question/answer couples
        UrlItem url = new UrlItem( JSP_QUESTION_ANSWER_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );
        if ( strIdSubject != null )
        {
        	url.addParameter( PARAMETER_SUBJECT_ID, strIdSubject );
        }
        
      //reindex the subject
        //IndexationService.addIndexerAction( strIdSubject,
         //   AppPropertiesService.getProperty( HelpdeskIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_MODIFY );

        return url.getUrl(  );
    }
    /**
     * Processes QuestionAnswer publication
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doPublishQuestionAnswer( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        int nIdQuestionAnswer = Integer.parseInt( request.getParameter( PARAMETER_QUESTION_ID ) );

        QuestionAnswer questionAnswer = QuestionAnswerHome.findByPrimaryKey( nIdQuestionAnswer, getPlugin(  ) );

        if ( questionAnswer != null )
        {
            questionAnswer.setStatus( QuestionAnswer.STATUS_PUBLISHED );
            QuestionAnswerHome.update( questionAnswer, getPlugin(  ) );
        }

        // If the operation is successfull, redirect towards the list of question/answer couples
        UrlItem url = new UrlItem( JSP_QUESTION_ANSWER_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );
        url.addParameter( PARAMETER_SUBJECT_ID, questionAnswer.getIdSubject(  ) );

        //reindex the subject
        IndexationService.addIndexerAction( Integer.toString( questionAnswer.getIdSubject(  ) ),
            AppPropertiesService.getProperty( HelpdeskIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_MODIFY );

        HelpdeskIndexerUtils.addIndexerAction( Integer.toString( questionAnswer.getIdSubject(  ) ), IndexerAction.TASK_MODIFY, HelpdeskIndexerUtils.CONSTANT_SUBJECT_TYPE_RESOURCE );
        
        return url.getUrl(  );
    }

    /**
     * Processes QuestionAnswer unpublication
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doUnpublishQuestionAnswer( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        int nIdQuestionAnswer = Integer.parseInt( request.getParameter( PARAMETER_QUESTION_ID ) );

        QuestionAnswer questionAnswer = QuestionAnswerHome.findByPrimaryKey( nIdQuestionAnswer, getPlugin(  ) );

        if ( questionAnswer != null )
        {
            questionAnswer.setStatus( QuestionAnswer.STATUS_UNPUBLISHED );
            QuestionAnswerHome.update( questionAnswer, getPlugin(  ) );
        }

        // If the operation is successfull, redirect towards the list of question/answer couples
        UrlItem url = new UrlItem( JSP_QUESTION_ANSWER_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );
        url.addParameter( PARAMETER_SUBJECT_ID, questionAnswer.getIdSubject(  ) );

        //reindex the subject
        String strIdQuestionAnswer = Integer.toString( questionAnswer.getIdQuestionAnswer(  ) );
        IndexationService.addIndexerAction( strIdQuestionAnswer + "_" +
            HelpdeskIndexer.SHORT_NAME_QUESTION_ANSWER,
            AppPropertiesService.getProperty( HelpdeskIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );

        HelpdeskIndexerUtils.addIndexerAction( strIdQuestionAnswer, IndexerAction.TASK_DELETE, HelpdeskIndexerUtils.CONSTANT_QUESTION_ANSWER_TYPE_RESOURCE );
        
        return url.getUrl(  );
    }

    /**
     * Returns the subject removal form
     * @param request The Http request
     * @return The Html template
     */
    public String getConfirmRemoveQuestionAnswer( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        int strIdQuestionAnswer = Integer.parseInt( request.getParameter( PARAMETER_QUESTION_ID ) );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_QUESTION_ANSWER );
        url.addParameter( PARAMETER_QUESTION_ID, strIdQuestionAnswer );
        url.addParameter( PARAMETER_SUBJECT_ID, request.getParameter( PARAMETER_SUBJECT_ID ) );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_QUESTION_ANSWER, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Processes QuestionAnswer removal
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doRemoveQuestionAnswer( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strIdQuestionAnswer = request.getParameter( PARAMETER_QUESTION_ID );
        int nIdQuestionAnswer = Integer.parseInt( strIdQuestionAnswer );
        QuestionAnswerHome.remove( nIdQuestionAnswer, getPlugin(  ) );

        // If the operation is successfull, redirect towards the list of question/answer couples
        UrlItem url = new UrlItem( JSP_QUESTION_ANSWER_LIST );
        url.addParameter( PARAMETER_SUBJECT_ID, request.getParameter( PARAMETER_SUBJECT_ID ) );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return url.getUrl(  );
    }
    
    /**
     * Processes QuestionAnswer Selection removal
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doRemoveSelection( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        if( _multiSelectionValues != null )
        {
        	for( String strIdQuestionAnswer : _multiSelectionValues)
        	{        		
            	int nIdQuestionAnswer = Integer.parseInt( strIdQuestionAnswer );
            	QuestionAnswerHome.remove( nIdQuestionAnswer, getPlugin(  ) );
        	}
        	
        }      

        // If the operation is successfull, redirect towards the list of question/answer couples
        UrlItem url = new UrlItem( JSP_QUESTION_ANSWER_LIST );
        url.addParameter( PARAMETER_SUBJECT_ID, request.getParameter( PARAMETER_SUBJECT_ID ) );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return url.getUrl(  );
    }

    /**
     * Returns the visitor question modification form
     * @param request The Http request
     * @return The Html template
     */
    public String getModifyVisitorQuestion( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_VISITOR_QUESTIONS );

        if ( faq == null )
        {
            return getVisitorQuestionList( request );
        }

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_MODIFY_VISITOR_QUESTION );

        int nIdVisitorQuestion = Integer.parseInt( request.getParameter( PARAMETER_QUESTION_ID ) );
        VisitorQuestion visitorQuestion = VisitorQuestionHome.findByPrimaryKey( nIdVisitorQuestion, getPlugin(  ) );
        
        String strIdTheme = request.getParameter( PARAMETER_THEME_ID );
        
        model.put( MARK_VISITOR_QUESTION, visitorQuestion );
        model.put( MARK_FAQ_ID, faq.getId(  ) );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_ANSWER, visitorQuestion.getAnswer(  ) );        
        model.put( MARK_THEME_ID, strIdTheme );  
        
        HtmlTemplate template;

        if ( visitorQuestion.getAnswer(  ).equals( "" ) )
        {
            template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_VISITOR_QUESTION, getLocale(  ), model );
        }
        else
        {
            // The question has already been treated
            AdminUser helpdeskUser = AdminUserHome.findByPrimaryKey( visitorQuestion.getIdUser(  ) );
            List<QuestionAnswer> listQuestionAnswer = QuestionAnswerHome.findByKeywords( visitorQuestion.getAnswer(  ),
                    getPlugin(  ) );
            model.put( MARK_HELPDESK_USER, helpdeskUser );
            model.put( MARK_QUESTION_ANSWER_LIST, listQuestionAnswer );
            template = AppTemplateService.getTemplate( TEMPLATE_VIEW_VISITOR_QUESTION, getLocale(  ), model );
        }

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the answer selection
     * @param request The Http request
     * @return The Html template
     */
    public String getAnswerSelection( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_VISITOR_QUESTIONS ); //FIXME

        if ( faq == null )
        {
            return getManageHelpdesk( request );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        List<Subject> listSubject = (List<Subject>) SubjectHome.getInstance(  ).findAll( getPlugin(  ) );
        model.put( MARK_SUBJECT_LIST, listSubject );
        model.put( MARK_PLUGIN, getPlugin(  ) );
        model.put( MARK_FAQ_ID, faq.getId(  ) );
        model.put( MARK_QUESTION_LIST, QuestionAnswerHome.findAll( getPlugin(  ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ANSWER_SELECTION, getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Fetches a visitor's list of questions
     * @param request The HttpRequest
     * @return The result in a string form
     */
    public String getVisitorQuestionList( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_VISITOR_QUESTIONS );

        if ( faq == null )
        {
            return getManageHelpdesk( request );
        }

        String strIdTheme = request.getParameter( PARAMETER_THEME_ID );
        Theme theme = null;

        if ( ( strIdTheme != null ) && strIdTheme.matches( REGEX_ID ) )
        {
            int nIdTheme = Integer.parseInt( strIdTheme );
            theme = (Theme) ThemeHome.getInstance(  ).findByPrimaryKey( nIdTheme, getPlugin(  ) );
        }

        Collection<Theme> listThemes = (Collection<Theme>) ThemeHome.getInstance(  )
                                                                    .findByIdFaq( faq.getId(  ), getPlugin(  ) );

        if ( ( theme == null ) && ( listThemes.size(  ) > 0 ) )
        {
            theme = listThemes.iterator(  ).next(  );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_VISITOR_QUESTION_LIST );

        model.put( MARK_THEME_LIST, listThemes );
        model.put( MARK_THEME, theme );
        model.put( MARK_FAQ, faq );
        model.put( MARK_PLUGIN, getPlugin(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_VISITOR_QUESTION_LIST, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes visitor question modification
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doModifyVisitorQuestion( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_VISITOR_QUESTIONS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strIdVisitorQuestion = request.getParameter( PARAMETER_QUESTION_ID );
        int nIdVisitorQuestion = Integer.parseInt( strIdVisitorQuestion );
        String strLastname = request.getParameter( PARAMETER_LAST_NAME );
        String strFirstname = request.getParameter( PARAMETER_FIRST_NAME );
        String strEmail = request.getParameter( PARAMETER_EMAIL );
        String strQuestion = request.getParameter( PARAMETER_QUESTION );
        String strAnswer = request.getParameter( PARAMETER_ANSWER );
        String strSubject = request.getParameter( PARAMETER_MAIL_SUBJECT );
        String strIdTheme = request.getParameter( PARAMETER_THEME_ID );
        AdminUser user = getUser(  );

        VisitorQuestion visitorQuestion = new VisitorQuestion(  );
        visitorQuestion.setIdVisitorQuestion( nIdVisitorQuestion );
        visitorQuestion.setLastname( strLastname );
        visitorQuestion.setFirstname( strFirstname );
        visitorQuestion.setEmail( strEmail );
        visitorQuestion.setQuestion( strQuestion );
        visitorQuestion.setAnswer( strAnswer );
        visitorQuestion.setIdUser( user.getUserId(  ) );

        // Mandatory field
        if ( request.getParameter( PARAMETER_ANSWER ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        VisitorQuestionHome.update( visitorQuestion, getPlugin(  ) );

        String strPortalUrl = AppPathService.getBaseUrl( request );

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_QUESTION, visitorQuestion.getQuestion(  ) );
        model.put( MARK_ANSWER, visitorQuestion.getAnswer(  ) );
        model.put( MARK_PORTAL_URL, strPortalUrl );
        model.put( MARK_FAQ_ID, faq.getId(  ) );

        String strPortal = AppPropertiesService.getProperty( MESSAGE_PORTAL_NAME );        
        String strEmailWebmaster = AppPropertiesService.getProperty( MESSAGE_MAIL_HELPDESK_SENDER );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SEND_ANSWER, request.getLocale(  ), model );

        String strMessageText = template.getHtml(  );

        strSubject = AppPropertiesService.getProperty( PROPERTY_MAIL_SUBJECT_PREFIX )
    		+ CONSTANT_SPACE + strPortal + CONSTANT_MINUS + strSubject;
    	
        MailService.sendMailHtml( visitorQuestion.getEmail(  ), strPortal, strEmailWebmaster, strSubject, strMessageText );

        if ( request.getParameter( PARAMETER_ADD_QUESTION_ANSWER ) == null )
        {
            // If the operation is successfull, redirect towards the list of visitor's questions
            UrlItem url = new UrlItem( JSP_VISITOR_QUESTION_LIST );
            url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

            return url.getUrl(  );
        }
        else
        {        	
            UrlItem url = new UrlItem( JSP_CREATE_QUESTION_ANSWER );
            url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );
            url.addParameter( PARAMETER_QUESTION_ID, nIdVisitorQuestion );
            url.addParameter( PARAMETER_THEME_ID, strIdTheme );            
            return url.getUrl(  );
        }
    }

    /**
     * Returns the list of archived questions
     * @param request The Http request
     * @return The Html template
     */
    public String getArchivedQuestionList( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_VISITOR_QUESTIONS );

        if ( faq == null )
        {
            return getVisitorQuestionList( request );
        }

        Collection<Theme> listThemes = (Collection<Theme>) ThemeHome.getInstance(  )
                                                                    .findByIdFaq( faq.getId(  ), getPlugin(  ) );

        String strIdTheme = request.getParameter( PARAMETER_THEME_ID );
        Theme theme = null;

        if ( ( strIdTheme != null ) && strIdTheme.matches( REGEX_ID ) )
        {
            int nIdTheme = Integer.parseInt( strIdTheme );
            theme = (Theme) ThemeHome.getInstance(  ).findByPrimaryKey( nIdTheme, getPlugin(  ) );
        }

        if ( ( theme == null ) && ( listThemes.size(  ) > 0 ) )
        {
            theme = listThemes.iterator(  ).next(  );
        }

        Collection<VisitorQuestion> listArchivedQuestions = null;

        if ( theme != null )
        {
            listArchivedQuestions = VisitorQuestionHome.findArchivedQuestionsByTheme( theme.getId(  ), getPlugin(  ) );
        }

        HashMap model = new HashMap(  );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_ARCHIVED_QUESTION_LIST );

        model.put( MARK_THEME_LIST, listThemes );
        model.put( MARK_ARCHIVED_QUESTION_LIST, listArchivedQuestions );
        model.put( MARK_THEME, theme );
        model.put( MARK_FAQ, faq );
        model.put( MARK_PLUGIN, getPlugin(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_ARCHIVED_QUESTION_LIST, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes visitor question removal
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doRemoveVisitorQuestion( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_VISITOR_QUESTIONS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        int nIdQuestionAnswer = Integer.parseInt( request.getParameter( PARAMETER_QUESTION_ID ) );
        VisitorQuestionHome.remove( nIdQuestionAnswer, getPlugin(  ) );

        // If the operation is successfull, redirect towards the list of question/answer couples
        UrlItem url = new UrlItem( JSP_ARCHIVED_QUESTION_LIST );
        url.addParameter( PARAMETER_THEME_ID, request.getParameter( PARAMETER_THEME_ID ) );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return url.getUrl(  );
    }

    /**
     * Returns the list of archived questions
     * @param request The Http request
     * @return The Html template
     */
    public String getImportQuestionAnswerList( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_IMPORT_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return getManageHelpdesk( request );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_IMPORT_QUESTION_ANSWER_LIST );

        model.put( MARK_FAQ, faq );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_IMPORT_QUESTION_ANSWER_LIST, getLocale(  ),
                model );

        return getAdminPage( template.getHtml(  ) );
    }
    
    /**
     * Processes the insert of QuestionAnswer
     *
     * @param request The Http request
     * @return The jsp URL which displays the question answer list
     */
    private String insertQuestionAnswers( HttpServletRequest request,  List<String[]> listQuestionAnswers,  Faq faq
    		, boolean bDeleteList, Collection<QuestionAnswer> questionAnswerList)
    {
    	 int nRowNumber = 0;

    	 boolean bAvoidFirstColumn = Boolean.parseBoolean( AppPropertiesService.getProperty( PROPERTY_CSV_HEADER,
                 DEFAULT_CSV_HEADER ) );
    	 String strCsvColumnsList = AppPropertiesService.getProperty( PROPERTY_CSV_COLUMNS_LIST, DEFAULT_CSV_COLUMNS );
    	 String[] arrayCsvColumnsList = strCsvColumnsList.split( DEFAULT_CONSTANT_DELIMITER_COLUMNS_LIST );
         // For each row 
    	 if( !bDeleteList )
    	 {
    		 //reverse the list to keep the order when not deleting the list
    		 Collections.reverse( listQuestionAnswers );
    	 }
    	 
         for ( String[] strRow : listQuestionAnswers )
         {
             if ( !( ( nRowNumber == 0 ) && bAvoidFirstColumn ) )
             {
                 int nColumnId = 0;
                 QuestionAnswer questionAnswer = new QuestionAnswer(  );

                 if ( strRow.length != arrayCsvColumnsList.length )
                 {
                     return AdminMessageService.getMessageUrl( request, MESSAGE_CSV_COLUMNS_LIST_ERROR,
                         new String[] { String.valueOf( nRowNumber + 1 ), strCsvColumnsList },
                         AdminMessage.TYPE_ERROR );
                 }

                 // For each field
                 for ( String strField : strRow )
                 {
                     EnumColumns eColumn = EnumColumns.getByName( arrayCsvColumnsList[nColumnId] );

                     if ( eColumn != null )
                     {
                         switch ( eColumn )
                         {
                             case SUBJECT_ID:

                                 int nIdSubject = validateSubjectId( strField, faq );

                                 if ( nIdSubject < 0 )
                                 {
                                     return getAdminMessageError( request, nColumnId,
                                         arrayCsvColumnsList[nColumnId], nRowNumber );
                                 }

                                 questionAnswer.setIdSubject( nIdSubject );

                                 break;
                                 
                             case ORDER_ID:
                            	 
                            	 if( bDeleteList)
                            	 {
                            		 int nIdOrder = validateIdOrder( strField, faq );

                            		 if ( nIdOrder < 0 )
                            		 {
                            			 return getAdminMessageError( request, nColumnId,
                            					 arrayCsvColumnsList[nColumnId], nRowNumber );
                            		 }

                            		 questionAnswer.setIdOrder( nIdOrder );
                            		
                            	 }
                            	 break;

                             case QUESTION:

                                 String strQuestion = validateQuestion( strField );

                                 if ( strQuestion == null )
                                 {
                                     return getAdminMessageError( request, nColumnId,
                                         arrayCsvColumnsList[nColumnId], nRowNumber );
                                 }

                                 questionAnswer.setQuestion( strQuestion );

                                 break;

                             case ANSWER:

                                 String strAnswer = validateAnswer( strField );

                                 if ( strAnswer == null )
                                 {
                                     return getAdminMessageError( request, nColumnId,
                                         arrayCsvColumnsList[nColumnId], nRowNumber );
                                 }

                                 questionAnswer.setAnswer( strAnswer );

                                 break;

                             default:
                                 break;
                         }
                     }

                     nColumnId++;
                 }

                 questionAnswer.setCreationDate( new Date(  ) );
                 questionAnswer.setStatus( 1 ); //FIXME

                 // Create a new questionAnswer
                 questionAnswerList.add( questionAnswer );
             }

             nRowNumber++;
         }

         // delete the existing questionAnswer list
         if ( bDeleteList )
         {
             QuestionAnswerHome.removeAll( getPlugin(  ) );
         }

         // Save the list
         for ( QuestionAnswer questionAnswer : questionAnswerList )
         {
             QuestionAnswerHome.create( questionAnswer, getPlugin(  ) );
         }
         
         return null;
    }
    
    /**
     * Processes the import of question answer due to a csv file without deleting old question list
     *
     * @param request The Http request
     * @return The jsp URL which displays the question answer list
     */
    public String doConfirmImportQuestionAnswerList( HttpServletRequest request )
    {
    	Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_IMPORT_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }
        
        Collection<QuestionAnswer> questionAnswerList = new ArrayList<QuestionAnswer>(  );
        
        List<String[]> listQuestionAnswers = getRowsFromCsvFile( request, false );

        if ( listQuestionAnswers == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CSV_FILE_NOT_VALID, AdminMessage.TYPE_STOP );
        }

        // the file is empty
        if ( ( listQuestionAnswers == null ) || ( listQuestionAnswers.size(  ) == 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CSV_FILE_EMPTY, AdminMessage.TYPE_STOP );
        }
        
        String strInsertResult = insertQuestionAnswers(request, listQuestionAnswers, faq, false, questionAnswerList);
    	if ( strInsertResult != null )
    	{
    		return strInsertResult;
    	}
    	
    	 UrlItem url = new UrlItem( JSP_LIST_QUESTIONS_ANSWER );
         url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

         return AdminMessageService.getMessageUrl( request, MESSAGE_IMPORT_CSV_OK,
             new String[] { String.valueOf( questionAnswerList.size(  ) ) }, url.getUrl(  ), AdminMessage.TYPE_INFO );
    }

    /**
     * Processes the import of question answer due to a csv file
     *
     * @param request The Http request
     * @return The jsp URL which displays the question answer list
     */
    public String doImportQuestionAnswerList( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_IMPORT_QUESTIONS_ANSWERS );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        
        Collection<QuestionAnswer> questionAnswerList = new ArrayList<QuestionAnswer>(  );
        boolean bDeleteList = ( request.getParameter( PARAMETER_DELETE_LIST ) != null ) ? true : false;
        List<String[]> listQuestionAnswers = getRowsFromCsvFile( request, true );

        if ( listQuestionAnswers == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CSV_FILE_NOT_VALID, AdminMessage.TYPE_STOP );
        }

        // the file is empty
        if ( ( listQuestionAnswers == null ) || ( listQuestionAnswers.size(  ) == 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CSV_FILE_EMPTY, AdminMessage.TYPE_STOP );
        }
        else if ( bDeleteList )
        {
        	String strInsertResult = insertQuestionAnswers(request, listQuestionAnswers, faq, bDeleteList, questionAnswerList);
        	if ( strInsertResult != null )
        	{
        		return strInsertResult;
        	}
        }
        else
        {
        	MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;

            _csvItem = multi.getFile( PARAMETER_CSV_FILE );          
            
        	UrlItem url = new UrlItem( JSP_DO_IMPORT_CSV );
            url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );            

            return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_IMPORT_WITHOUT_DELETING
            		, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
        }

        UrlItem url = new UrlItem( JSP_LIST_QUESTIONS_ANSWER );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_IMPORT_CSV_OK,
            new String[] { String.valueOf( questionAnswerList.size(  ) ) }, url.getUrl(  ), AdminMessage.TYPE_INFO );
    }

    /**
     * Returns the help desk administration menu - the list of themes
     * @param request The Http request
     * @return The Html template
     */
    public String getManageHelpdeskAdmin( HttpServletRequest request )
    {
        HashMap model = new HashMap(  );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_THEMES );

        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_THEMES );

        if ( faq == null )
        {
            return getManageHelpdesk( request );
        }

        Collection<Theme> listTheme = (Collection<Theme>) ThemeHome.getInstance(  )
                                                                   .findByIdFaq( faq.getId(  ), getPlugin(  ) );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_STYLES_PER_PAGE, 10 ); //TODO no numbers in hard
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_MANAGE_HELPDESK_ADMIN );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        Paginator paginator = new Paginator( (List<Theme>) listTheme, _nItemsPerPage, url.getUrl(  ),
                Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        model.put( MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_THEME_LIST, paginator.getPageItems(  ) );
        model.put( MARK_PLUGIN, getPlugin(  ) );
        model.put( MARK_FAQ, faq );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_THEME_LIST, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the {@link Theme} removal form
     * @param request The Http request
     * @return The Html template
     */
    public String getConfirmRemoveTheme( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_THEMES );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        int nIdTheme = Integer.parseInt( request.getParameter( PARAMETER_THEME_ID ) );
        Theme theme = (Theme) ThemeHome.getInstance(  ).findByPrimaryKey( nIdTheme, getPlugin(  ) );
        Collection<VisitorQuestion> listQuestion = ThemeHome.findQuestion( nIdTheme, getPlugin(  ) );
        UrlItem url = new UrlItem( JSP_DO_REMOVE_THEME );
        url.addParameter( PARAMETER_THEME_ID, nIdTheme );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        if ( theme == null )
        {
            return JSP_MANAGE_HELPDESK_LIST;
        }

        if ( ( listQuestion.size(  ) != 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_DELETE_THEME_QUESTION_EXISTS,
                url.getUrl(  ), AdminMessage.TYPE_STOP );
        }

        if ( theme.getChilds( getPlugin(  ) ).size(  ) > 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_DELETE_THEME_SUB_THEME_EXISTS,
                AdminMessage.TYPE_STOP );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_THEME, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Processes theme removal
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doRemoveTheme( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_THEMES );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_MANAGE_HELPDESK_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        int nIdTheme = Integer.parseInt( request.getParameter( PARAMETER_THEME_ID ) );
        Theme theme = (Theme) ThemeHome.getInstance(  ).findByPrimaryKey( nIdTheme, getPlugin(  ) );
        Collection<VisitorQuestion> listQuestion = ThemeHome.findQuestion( nIdTheme, getPlugin(  ) );

        if ( theme == null )
        {
            return url.getUrl(  );
        }

        if ( ( listQuestion.size(  ) == 0 ) && ( theme.getChilds( getPlugin(  ) ).size(  ) == 0 ) )
        {
            if ( theme.getIdParent(  ) == 0 )
            {
                ThemeHome.getInstance(  ).removeLinkToFaq( theme.getId(  ), faq.getId(  ), getPlugin(  ) );
            }

            Collection<VisitorQuestion> visitorQuestionList = VisitorQuestionHome.findArchivedQuestionsByTheme( theme.getId(  ),
                    getPlugin(  ) );

            for ( VisitorQuestion visitorQuestion : visitorQuestionList )
            {
                VisitorQuestionHome.remove( visitorQuestion.getIdVisitorQuestion(  ), getPlugin(  ) );
            }

            ThemeHome.getInstance(  ).remove( nIdTheme, faq.getId(  ), getPlugin(  ) );
        }

        // If the operation is successful, redirect towards the list of themes
        return url.getUrl(  );
    }

    /**
     * Returns the theme creation form
     * @param request The Http request
     * @return The Html template
     */
    public String getCreateTheme( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_THEMES );

        if ( faq == null )
        {
            return getManageHelpdeskAdmin( request );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );

        setPageTitleProperty( MESSAGE_PAGE_TITLE_CREATE_THEME );

        Collection<Theme> listThemes = (Collection<Theme>) ThemeHome.getInstance(  )
                                                                    .findByIdFaq( faq.getId(  ), getPlugin(  ) );
        Theme rootTheme = ThemeHome.getVirtualRootTheme( getLocale(  ) );
        model.put( MARK_THEME_LIST, listThemes );
        model.put( MARK_PLUGIN, getPlugin(  ) );
        model.put( MARK_DEFAULT_VALUE, rootTheme.getId(  ) );
        model.put( MARK_FAQ_ID, request.getParameter( PARAMETER_FAQ_ID ) );

        ReferenceList listMailingLists = AdminMailingListService.getMailingLists( getUser(  ) );
        model.put( MARK_MAILINGLISTS_LIST, listMailingLists );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_THEME, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the theme modification form
     * @param request The Http request
     * @return The Html template
     */
    public String getModifyTheme( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_THEMES );

        if ( faq == null )
        {
            return getManageHelpdeskAdmin( request );
        }

        HashMap model = new HashMap(  );

        setPageTitleProperty( MESSAGE_PAGE_TITLE_MODIFY_THEME );

        String strIdTheme = request.getParameter( PARAMETER_THEME_ID );

        int nIdTheme = Integer.parseInt( strIdTheme );
        Theme theme = (Theme) ThemeHome.getInstance(  ).findByPrimaryKey( nIdTheme, getPlugin(  ) );

        if ( theme == null )
        {
            return getManageHelpdeskAdmin( request );
        }

        model.put( MARK_THEME, theme );
        model.put( MARK_PLUGIN, getPlugin(  ) );
        model.put( MARK_FAQ_ID, faq.getId(  ) );

        Collection<Theme> listTheme = (Collection<Theme>) ThemeHome.getInstance(  )
                                                                   .findByIdFaq( faq.getId(  ), getPlugin(  ) );
        model.put( MARK_THEME_LIST, listTheme );

        ReferenceList listMailingLists = AdminMailingListService.getMailingLists( getUser(  ) );
        model.put( MARK_MAILINGLISTS_LIST, listMailingLists );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_THEME, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes the creation of a theme
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doCreateTheme( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_THEMES );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strTheme = request.getParameter( PARAMETER_THEME );
        String strIdQuestionMailingList = request.getParameter( PARAMETER_QUESTION_MAILINGLIST );
        String strIdParent = request.getParameter( PARAMETER_PARENT_ID );

        // Mandatory field
        if ( ( strTheme == null ) || ( strIdParent == null ) || strTheme.trim(  ).equals( "" ) ||
                strIdParent.equals( "" ) || ( faq == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        //Message fort no mailing list created
        if ( ( strIdQuestionMailingList == null ) || strIdQuestionMailingList.trim(  ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_MAILING_LIST_EXIST_THEME,
                AdminMessage.TYPE_STOP );
        }

        int nIdQuestionMailingList = Integer.parseInt( strIdQuestionMailingList );
        int nIdParent = Integer.parseInt( strIdParent );

        Theme theme = new Theme(  );
        theme.setText( strTheme );
        theme.setIdMailingList( nIdQuestionMailingList );
        theme.setIdParent( nIdParent );
        theme = (Theme) ThemeHome.getInstance(  ).create( theme, faq.getId(  ), getPlugin(  ) );

        if ( theme.getIdParent(  ) == 0 )
        {
            ThemeHome.getInstance(  ).createLinkToFaq( theme.getId(  ), faq.getId(  ), getPlugin(  ) );
        }

        // If the operation is successful, redirect towards the list of themes
        UrlItem url = new UrlItem( JSP_MANAGE_HELPDESK_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return url.getUrl(  );
    }

    /**
     * Processes the modification of a theme
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doModifyTheme( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_THEMES );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strTheme = request.getParameter( PARAMETER_THEME );
        String strIdTheme = request.getParameter( PARAMETER_THEME_ID );
        String strIdQuestionMailingList = request.getParameter( PARAMETER_QUESTION_MAILINGLIST );
        String strIdParent = request.getParameter( PARAMETER_PARENT_ID );

        // Mandatory field
        if ( ( strTheme == null ) || ( strIdTheme == null ) || ( strIdParent == null ) || strIdTheme.equals( "" ) ||
                strTheme.equals( "" ) || strIdParent.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdTheme = Integer.parseInt( strIdTheme );
        int nIdParent = Integer.parseInt( strIdParent );

        //Message fort no mailing list created
        if ( ( strIdQuestionMailingList == null ) || strIdQuestionMailingList.trim(  ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_MAILING_LIST_EXIST_THEME,
                AdminMessage.TYPE_STOP );
        }

        int nIdQuestionMailingList = Integer.parseInt( strIdQuestionMailingList );

        Theme theme = new Theme(  );
        theme.setId( nIdTheme );
        theme.setText( strTheme );

        if ( theme.getIdParent(  ) == 0 )
        {
            ThemeHome.getInstance(  ).removeLinkToFaq( nIdTheme, faq.getId(  ), getPlugin(  ) );
        }

        if ( nIdParent == 0 )
        {
            ThemeHome.getInstance(  ).createLinkToFaq( nIdTheme, faq.getId(  ), getPlugin(  ) );
        }

        theme.setIdParent( nIdParent );
        theme.setIdMailingList( nIdQuestionMailingList );

        ThemeHome.getInstance(  ).update( theme, faq.getId(  ), getPlugin(  ) );

        // If the operation is successfull, redirect towards the list of themes
        UrlItem url = new UrlItem( JSP_MANAGE_HELPDESK_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return url.getUrl(  );
    }

    /**
     * Move the theme down
     * @param request The Http servlet request
     * @return The redirect url
     */
    public String doGoDownTheme( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_THEMES );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strIdTheme = request.getParameter( PARAMETER_THEME_ID );
        int nIdTheme = Integer.parseInt( strIdTheme );
        ThemeHome.getInstance(  ).goDown( nIdTheme, faq.getId(  ), getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_MANAGE_HELPDESK_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return url.getUrl(  );
    }

    /**
     * Move the theme up
     * @param request The Http servlet request
     * @return The redirect url
     */
    public String doGoUpTheme( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_THEMES );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strIdTheme = request.getParameter( PARAMETER_THEME_ID );
        int nIdTheme = Integer.parseInt( strIdTheme );
        ThemeHome.getInstance(  ).goUp( nIdTheme, faq.getId(  ), getPlugin(  ) );

        UrlItem url = new UrlItem( JSP_MANAGE_HELPDESK_LIST );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        return url.getUrl(  );
    }

    /**
     * Move the theme in a parent theme
     * @param request The Http servlet request
     * @return The redirect url
     */
    public String doGoInTheme( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_THEMES );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_MANAGE_HELPDESK_LIST );
        url.addParameter( PARAMETER_FAQ_ID, request.getParameter( PARAMETER_FAQ_ID ) );

        String strIdTheme = request.getParameter( PARAMETER_THEME_ID );
        int nIdTheme = Integer.parseInt( strIdTheme );
        Theme theme = (Theme) ThemeHome.getInstance(  ).findByPrimaryKey( nIdTheme, getPlugin(  ) );

        if ( theme == null )
        {
            return url.getUrl(  );
        }

        if ( theme.getIdParent(  ) == 0 )
        {
            ThemeHome.getInstance(  ).removeLinkToFaq( nIdTheme, faq.getId(  ), getPlugin(  ) );
        }

        ThemeHome.getInstance(  ).goIn( nIdTheme, faq.getId(  ), getPlugin(  ) );

        return url.getUrl(  );
    }

    /**
     * Move the theme out a parent theme
     * @param request The Http servlet request
     * @return The redirect url
     */
    public String doGoOutTheme( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MANAGE_THEMES );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_MANAGE_HELPDESK_LIST );
        url.addParameter( PARAMETER_FAQ_ID, request.getParameter( PARAMETER_FAQ_ID ) );

        String strIdTheme = request.getParameter( PARAMETER_THEME_ID );
        int nIdTheme = Integer.parseInt( strIdTheme );
        ThemeHome.getInstance(  ).goOut( nIdTheme, faq.getId(  ), getPlugin(  ) );

        Theme theme = (Theme) ThemeHome.getInstance(  ).findByPrimaryKey( nIdTheme, getPlugin(  ) );

        if ( theme == null )
        {
            return url.getUrl(  );
        }

        if ( theme.getIdParent(  ) == 0 )
        {
            ThemeHome.getInstance(  ).removeLinkToFaq( nIdTheme, faq.getId(  ), getPlugin(  ) );
            ThemeHome.getInstance(  ).createLinkToFaq( nIdTheme, faq.getId(  ), getPlugin(  ) );
        }

        return url.getUrl(  );
    }

    /**
     * Returns the list of F.A.Q.
     * @param request The Http request
     * @return The Html template
     */
    public String getManageHelpdesk( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        Collection<HashMap> subModelFaqList = new ArrayList<HashMap>(  );

        setPageTitleProperty( MESSAGE_PAGE_TITLE_FAQ_LIST );

        Collection<Faq> listFaq = FaqHome.findAll( getPlugin(  ) );
        listFaq = AdminWorkgroupService.getAuthorizedCollection( listFaq, getUser(  ) );

        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_STYLES_PER_PAGE, 10 );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        Paginator paginator = new Paginator( (List<Faq>) listFaq, _nItemsPerPage, JSP_HELPDESK_MANAGE,
                Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        for ( Faq faq : (List<Faq>) paginator.getPageItems(  ) )
        {
            HashMap subModelFaq = new HashMap(  );
            subModelFaq.put( MARK_FAQ, faq );

            for ( String strPermission : getAuthorizedActionCollection( faq ) )
            {
                subModelFaq.put( strPermission, strPermission );
            }

            subModelFaqList.add( subModelFaq );
        }

        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_FAQ_LIST, subModelFaqList );
        model.put( MARK_PLUGIN, getPlugin(  ) );

        if ( RBACService.isAuthorized( Faq.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FaqResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            model.put( FaqResourceIdService.PERMISSION_CREATE, FaqResourceIdService.PERMISSION_CREATE );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_HELPDESK, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the faq creation form
     * @param request The Http request
     * @return The Html template
     */
    public String getCreateFaq( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Faq.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FaqResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            return getManageHelpdesk( request );
        }

        //FIXME
        HashMap model = new HashMap(  );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_CREATE_FAQ );

        model.put( MARK_PLUGIN, getPlugin(  ) );
        model.put( MARK_ROLE_KEY_LIST, RoleHome.getRolesList(  ) );
        model.put( MARK_DEFAULT_VALUE_ROLE_KEY, Faq.ROLE_NONE );
        model.put( MARK_DEFAULT_VALUE_WORKGROUP_KEY, AdminWorkgroupService.ALL_GROUPS );
        model.put( MARK_WORKGROUP_KEY_LIST, AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_FAQ, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes faq creation
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doCreateFaq( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Faq.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FaqResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strName = request.getParameter( PARAMETER_NAME );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strWorkgroupKey = request.getParameter( PARAMETER_WORKGROUP_KEY );

        // Mandatory field
        if ( ( strName == null ) || strName.equals( "" ) || ( strDescription == null ) || strDescription.equals( "" ) ||
                ( strRoleKey == null ) || ( strWorkgroupKey == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Faq faq = new Faq(  );
        faq.setName( strName );
        faq.setDescription( strDescription );
        faq.setRoleKey( strRoleKey );
        faq.setWorkgroup( strWorkgroupKey );
        FaqHome.insert( faq, getPlugin(  ) );

        // If the operation is successful, redirect towards the list of faq
        return JSP_MANAGE_HELPDESK;
    }

    /**
     * Returns the faq modification form
     * @param request The Http request
     * @return The Html template
     */
    public String getModifyFaq( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MODIFY );

        if ( faq == null )
        {
            return getManageHelpdesk( request );
        }

        HashMap model = new HashMap(  );

        setPageTitleProperty( MESSAGE_PAGE_TITLE_MODIFY_FAQ );

        model.put( MARK_FAQ, faq );
        model.put( MARK_PLUGIN, getPlugin(  ) );
        model.put( MARK_ROLE_KEY_LIST, RoleHome.getRolesList(  ) );
        model.put( MARK_WORKGROUP_KEY_LIST, AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FAQ, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes subject modification
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doModifyFaq( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_MODIFY );

        if ( faq == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        String strName = request.getParameter( PARAMETER_NAME );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strWorkgroupKey = request.getParameter( PARAMETER_WORKGROUP_KEY );

        // Mandatory field
        if ( ( faq == null ) || ( strName == null ) || strName.equals( "" ) || ( strDescription == null ) ||
                strDescription.equals( "" ) || ( strRoleKey == null ) || ( strWorkgroupKey == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        //FIXME check if the parent space is a child space from current subject
        faq.setName( strName );
        faq.setDescription( strDescription );
        faq.setRoleKey( strRoleKey );
        faq.setWorkgroup( strWorkgroupKey );

        FaqHome.store( faq, getPlugin(  ) );

        // If the operation is successful, redirect towards the list of faq
        return JSP_MANAGE_HELPDESK;
    }

    /**
     * Returns the faq removal form
     * @param request The Http request
     * @return The Html template
     */
    public String getConfirmRemoveFaq( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_DELETE );

        //TODO check if there are subject and themes
        if ( ( faq == null ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_FAQ );
        url.addParameter( PARAMETER_FAQ_ID, faq.getId(  ) );

        if ( ( faq.getSubjectsList( getPlugin(  ) ).size(  ) > 0 ) ||
                ( faq.getThemesList( getPlugin(  ) ).size(  ) > 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_DELETE_FAQ, AdminMessage.TYPE_STOP );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_FAQ, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Processes faq removal
     * @param request The Http request
     * @return The URL to redirect to
     */
    public String doRemoveFaq( HttpServletRequest request )
    {
        Faq faq = getAuthorizedFaq( request, FaqResourceIdService.PERMISSION_DELETE );

        if ( ( faq == null ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        if ( ( faq.getSubjectsList( getPlugin(  ) ).size(  ) > 0 ) ||
                ( faq.getThemesList( getPlugin(  ) ).size(  ) > 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_DELETE_FAQ, AdminMessage.TYPE_STOP );
        }

        FaqHome.delete( faq.getId(  ), getPlugin(  ) );

        // If the operation is successful, redirect towards the list of subjects
        return JSP_MANAGE_HELPDESK;
    }

    /**
     * Return a negative value if the field is not valid or if subject_id does not concern the specified Faq, the subject id else
     *
     * @param strSubjectId The field to validate
     * @param faq The {@link Faq} The faq concerned by subjectId
     * @return The subject id or a negative value
     */
    private int validateSubjectId( String strSubjectId, Faq faq )
    {
        int nReturn = -1;
        AppLogService.debug( "subject id : " + strSubjectId );

        if ( !strSubjectId.matches( REGEX_ID ) )
        {
            return nReturn;
        }

        int nIdSubject = Integer.parseInt( strSubjectId );
        Subject subject = (Subject) SubjectHome.getInstance(  ).findByPrimaryKey( nIdSubject, getPlugin(  ) );

        if ( subject == null )
        {
            return nReturn;
        }

        while ( subject.getIdParent(  ) != 0 )
        {
            subject = subject.getParent( getPlugin(  ) );
        }

        for ( Subject subjectFaq : faq.getSubjectsList( getPlugin(  ) ) )
        {
            if ( subject.getId(  ) == subjectFaq.getId(  ) )
            {
                nReturn = nIdSubject;
            }
        }

        return nReturn;
    }
    
    /**
     * Return a negative value if the field is not valid
     *
     * @param strSubjectId The field to validate
     * @param faq The {@link Faq} The faq concerned by subjectId
     * @return The subject id or a negative value
     */
    private int validateIdOrder( String strIdOrder, Faq faq )
    {        
    	try
    	{
    		int nIdOrder = Integer.parseInt( strIdOrder );
    		return nIdOrder;
    	}
        catch( NumberFormatException e)
        {
        	return -1;
        }
    }
    
    /**
     * Return null if the field is not valid, the question else
     * @param strQuestion The field to validate
     * @return The question or null
     */
    private String validateQuestion( String strQuestion )
    {
        AppLogService.debug( "question : " + strQuestion );

        if ( strQuestion.equals( "" ) )
        {
            return null;
        }

        return strQuestion;
    }

    /**
     * Return null if the field is not valid, the answer else
     * @param strAnswer The field to validate
     * @return The answer or null
     */
    private String validateAnswer( String strAnswer )
    {
        AppLogService.debug( "answer : " + strAnswer );

        if ( strAnswer.equals( "" ) )
        {
            return null;
        }

        return strAnswer;
    }

    /**
     * Set an admin message with the specified parameters
     *
     * @param request The {@link HttpServletRequest}
     * @param nColumnId the column id of the csv file
     * @param strCsvColumnName The name of the columne
     * @param nRowNumber the number of the row
     * @return the AdminMessage url
     */
    private String getAdminMessageError( HttpServletRequest request, int nColumnId, String strCsvColumnName,
        int nRowNumber )
    {
        return AdminMessageService.getMessageUrl( request, MESSAGE_CSV_FIELD_ERROR,
            new String[] { String.valueOf( nColumnId + 1 ), strCsvColumnName, String.valueOf( nRowNumber + 1 ) },
            AdminMessage.TYPE_ERROR );
    }

    /**
     * Get the list or rows from the csv file
     *
     * @param request The {@link HttpServletRequest}
     * @return the list of array of String of null if the file is not valid
     */
    private List<String[]> getRowsFromCsvFile( HttpServletRequest request, boolean isMulti )
    {
        String strCsvFileExtension = AppPropertiesService.getProperty( PROPERTY_CSV_FILE_EXTENSION,
                DEFAULT_CSV_FILE_EXTENSION );
        String strCharset = AppPropertiesService.getProperty( PROPERTY_CSV_FILE_CHARSET, DEFAULT_CSV_FILE_CHARSET );
        char cImportDelimiter = AppPropertiesService.getProperty( PROPERTY_IMPORT_DELIMITER, DEFAULT_IMPORT_DELIMITER )
                                                    .charAt( 0 );

        String strMultiFileName = null;
        if( isMulti )
        {
        	// create the multipart request
            MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;

            _csvItem = multi.getFile( PARAMETER_CSV_FILE );            
        }
        
        strMultiFileName = UploadUtil.cleanFileName( _csvItem.getName(  ) );

        if ( strMultiFileName.equals( "" ) )
        {
            return null;
        }

        // test the extension of the file must be 'csv'
        String strExtension = strMultiFileName.substring( strMultiFileName.length(  ) - strCsvFileExtension.length(  ),
                strMultiFileName.length(  ) );

        if ( !strExtension.equals( strCsvFileExtension ) )
        {
            return null;
        }

        Reader fileReader;
        List<String[]> listQuestionAnswers = null;

        try
        {
            fileReader = new InputStreamReader( _csvItem.getInputStream(  ), strCharset );

            CSVReader csvReader = new CSVReader( fileReader, cImportDelimiter );

            listQuestionAnswers = csvReader.readAll(  );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ) );
            throw new RuntimeException( e.getMessage(  ) );
        }

        return listQuestionAnswers;
    }

    /**
     * Get the authorized Faq,  filtered by workgroup
     *
     * @param request The {@link HttpServletRequest}
     * @param strPermissionType The type of permission (see {@link FaqResourceIdService} class)
     * @return The faq or null if user have no access
     */
    private Faq getAuthorizedFaq( HttpServletRequest request, String strPermissionType )
    {
        String strIdFaq = request.getParameter( PARAMETER_FAQ_ID );

        if ( ( strIdFaq == null ) || !strIdFaq.matches( REGEX_ID ) )
        {
            return null;
        }

        int nIdFaq = Integer.parseInt( strIdFaq );
        Faq faq = FaqHome.load( nIdFaq, getPlugin(  ) );

        if ( ( faq == null ) || !AdminWorkgroupService.isAuthorized( faq, getUser(  ) ) ||
                !RBACService.isAuthorized( Faq.RESOURCE_TYPE, String.valueOf( faq.getId(  ) ), strPermissionType,
                    getUser(  ) ) )
        {
            return null;
        }

        return faq;
    }

    /**
     * Get the collection of authorized actions
     *
     * @param faq The authorized faq
     * @return The collection of authorized actions
     */
    private Collection<String> getAuthorizedActionCollection( Faq faq )
    {
        Collection<String> listActions = new ArrayList<String>(  );

        if ( RBACService.isAuthorized( Faq.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    FaqResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            listActions.add( FaqResourceIdService.PERMISSION_CREATE );
        }

        if ( RBACService.isAuthorized( Faq.RESOURCE_TYPE, String.valueOf( faq.getId(  ) ),
                    FaqResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            listActions.add( FaqResourceIdService.PERMISSION_DELETE );
        }

        if ( RBACService.isAuthorized( Faq.RESOURCE_TYPE, String.valueOf( faq.getId(  ) ),
                    FaqResourceIdService.PERMISSION_IMPORT_QUESTIONS_ANSWERS, getUser(  ) ) )
        {
            listActions.add( FaqResourceIdService.PERMISSION_IMPORT_QUESTIONS_ANSWERS );
        }

        if ( RBACService.isAuthorized( Faq.RESOURCE_TYPE, String.valueOf( faq.getId(  ) ),
                    FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS, getUser(  ) ) )
        {
            listActions.add( FaqResourceIdService.PERMISSION_MANAGE_QUESTIONS_ANSWERS );
        }

        if ( RBACService.isAuthorized( Faq.RESOURCE_TYPE, String.valueOf( faq.getId(  ) ),
                    FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS, getUser(  ) ) )
        {
            listActions.add( FaqResourceIdService.PERMISSION_MANAGE_SUBJECTS );
        }

        if ( RBACService.isAuthorized( Faq.RESOURCE_TYPE, String.valueOf( faq.getId(  ) ),
                    FaqResourceIdService.PERMISSION_MANAGE_THEMES, getUser(  ) ) )
        {
            listActions.add( FaqResourceIdService.PERMISSION_MANAGE_THEMES );
        }

        if ( RBACService.isAuthorized( Faq.RESOURCE_TYPE, String.valueOf( faq.getId(  ) ),
                    FaqResourceIdService.PERMISSION_MANAGE_VISITOR_QUESTIONS, getUser(  ) ) )
        {
            listActions.add( FaqResourceIdService.PERMISSION_MANAGE_VISITOR_QUESTIONS );
        }

        if ( RBACService.isAuthorized( Faq.RESOURCE_TYPE, String.valueOf( faq.getId(  ) ),
                    FaqResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            listActions.add( FaqResourceIdService.PERMISSION_MODIFY );
        }

        return listActions;
    }
    
    
}
