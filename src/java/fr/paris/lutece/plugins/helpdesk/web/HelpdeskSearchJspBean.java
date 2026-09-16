package fr.paris.lutece.plugins.helpdesk.web;

import fr.paris.lutece.plugins.helpdesk.business.Faq;
import fr.paris.lutece.plugins.helpdesk.business.FaqHome;
import fr.paris.lutece.plugins.helpdesk.business.QuestionAnswer;
import fr.paris.lutece.plugins.helpdesk.business.Subject;
import fr.paris.lutece.plugins.helpdesk.business.SubjectHome;
import fr.paris.lutece.plugins.helpdesk.service.HelpdeskPlugin;
import fr.paris.lutece.plugins.helpdesk.service.helpdesksearch.HelpdeskSearchService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import au.com.bytecode.opencsv.CSVWriter;


/**
 * HelpdeskSearchJspBean
 */
@SessionScoped
@Named
public class HelpdeskSearchJspBean extends PluginAdminPageJspBean
{
    @Inject
    private HelpdeskSearchService _helpdeskSearchService;

	//Templates
    private static final String TEMPLATE_RESULTS = "admin/plugins/helpdesk/helpdesksearch/search_results.html";  
   
    //Properties
    private static final String PROPERTY_PAGE_TITLE_SEARCH = "helpdesk.search_results.pageTitle";
    private static final String PROPERTY_SEARCH_PAGE_URL = "helpdesk.healpdesksearch.pageSearch.baseUrl";
    private static final String PROPERTY_RESULTS_PER_PAGE = "helpdesk.healpdesksearch.nb.docs.per.page";
    
    
    //Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_NB_ITEMS_PER_PAGE = "items_per_page";
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_DATE_START_QUERY = "date_start";
    private static final String PARAMETER_DATE_END_QUERY = "date_end";
    private static final String PARAMETER_FAQ_ID = "faq_id";
    private static final String PARAMETER_SUBJECT_ID = "form_search_subject_id";
    private static final String PARAMETER_SEARCH_SUB_SUBJECTS = "form_search_sub_subjects";
    
    //Markers
    private static final String MARK_FILTER_SUBJECT = "filter_subject_id";
    private static final String MARK_RESULTS_LIST = "results_list";
    private static final String MARK_QUERY = "query";
    private static final String MARK_DATE_START_QUERY = "date_start_query";
    private static final String MARK_DATE_END_QUERY = "date_end_query";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ERROR = "error";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_FAQ_ID = "faq_id";
    private static final String MARK_FAQ_NAME = "faq_name";
    private static final String MARK_PLUGIN = "plugin";
    private static final String MARK_PATH_LABEL = "path_label";
    private static final String MARK_SUBJECT_LIST = "helpdesk_subject_list";
    private static final String MARK_FILTER_SEARCH_SUB_SUBJECT = "filter_search_sub_subject";
    
    //Messages
    private static final String MESSAGE_INVALID_SEARCH_TERMS = "helpdesk.message.invalidSearchTerms";    
    private static final String MESSAGE_SEARCH_DATE_VALIDITY = "helpdesk.siteMessage.dateValidity";
    
    //Default values
    private static final String DEFAULT_RESULTS_PER_PAGE = "10";
    private static final String DEFAULT_PAGE_INDEX = "1";
    
    private static final String CONSTANT_EMPTY_STRING = "";
    private static final String CONSTANT_SEPARATOR = ";";
    private static final String REGEX_ID = "^[\\d]+$";
    
    private Collection<QuestionAnswer> _listResults;

    /**
     * Returns search results
     *
     * @param request The HTTP request.
     * @return The HTML code of the page.
     */
    public String getSearch( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_SEARCH );

        String strError = null;
        Locale locale = getLocale(  );
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        Plugin plugin = PluginService.getPlugin( HelpdeskPlugin.PLUGIN_NAME );

        String strIdFaq = request.getParameter( PARAMETER_FAQ_ID );
        Faq faq = null;
        int nIdFaq = -1;

        if ( ( strIdFaq != null ) && strIdFaq.matches( REGEX_ID ) )
        {
            nIdFaq = Integer.parseInt( strIdFaq );
            faq = FaqHome.load( nIdFaq, getPlugin(  ) );
        }

        if ( faq == null )
        {
            strError = I18nService.getLocalizedString( MESSAGE_INVALID_SEARCH_TERMS, locale );
        }

        String strQuery = request.getParameter( PARAMETER_QUERY );
        String strSearchPageUrl = AppPropertiesService.getProperty( PROPERTY_SEARCH_PAGE_URL );

        // Check XSS characters
        if ( ( strError == null ) && ( strQuery != null ) && StringUtil.containsXssCharacters( strQuery ) )
        {
            strError = I18nService.getLocalizedString( MESSAGE_INVALID_SEARCH_TERMS, locale );
            strQuery = "";
        }

        String strDefaultNbItemPerPage = AppPropertiesService.getProperty( PROPERTY_RESULTS_PER_PAGE,
                DEFAULT_RESULTS_PER_PAGE );
        String strNbItemPerPage = request.getParameter( PARAMETER_NB_ITEMS_PER_PAGE );

        if ( ( strNbItemPerPage == null ) || !strNbItemPerPage.matches( REGEX_ID ) )
        {
            strNbItemPerPage = strDefaultNbItemPerPage;
        }

        int nNbItemsPerPage = Integer.parseInt( strNbItemPerPage );
        String strCurrentPageIndex = request.getParameter( PARAMETER_PAGE_INDEX );

        if ( ( strCurrentPageIndex == null ) || !strCurrentPageIndex.matches( REGEX_ID ) )
        {
            strCurrentPageIndex = DEFAULT_PAGE_INDEX;
        }

        int nPageIndex = Integer.parseInt( strCurrentPageIndex );
        Collection<QuestionAnswer> listResults = null;
        UrlItem url = new UrlItem( strSearchPageUrl );

        String strDateStartQuery = request.getParameter( PARAMETER_DATE_START_QUERY );
        String strDateEndQuery = request.getParameter( PARAMETER_DATE_END_QUERY );
        String strIdSubject = request.getParameter( PARAMETER_SUBJECT_ID );
        String strSearchSubSubjects = request.getParameter( PARAMETER_SEARCH_SUB_SUBJECTS );

        Subject subject = null;

        if ( ( strIdSubject != null ) && strIdSubject.matches( REGEX_ID ) )
        {
            subject = (Subject) SubjectHome.findByPrimaryKey( Integer.parseInt( strIdSubject ), plugin );
        }

        strIdSubject = ( strIdSubject == null ) ? CONSTANT_EMPTY_STRING : strIdSubject;

        Date dateStart = DateUtil.formatDate( strDateStartQuery, request.getLocale(  ) );
        Date dateEnd = DateUtil.formatDate( strDateEndQuery, request.getLocale(  ) );

        if ( ( strError == null ) && ( ( dateStart == null ) ^ ( dateEnd == null ) ) )
        {
            strError = I18nService.getLocalizedString( MESSAGE_SEARCH_DATE_VALIDITY, locale );
        }

        Boolean bSearchSubSubjects = false;

        if ( strSearchSubSubjects != null )
        {
            bSearchSubSubjects = Boolean.parseBoolean( strSearchSubSubjects );
        }

        if ( strError == null )
        {
            listResults = _helpdeskSearchService.getSearchResults( nIdFaq, strQuery, dateStart, dateEnd, subject,
                    bSearchSubSubjects, request, plugin );
            _listResults = listResults;
            url.addParameter( PARAMETER_QUERY, strQuery );
            url.addParameter( PARAMETER_NB_ITEMS_PER_PAGE, nNbItemsPerPage );
            url.addParameter( PARAMETER_FAQ_ID, nIdFaq );
            url.addParameter( PARAMETER_DATE_START_QUERY, strDateStartQuery );
            url.addParameter( PARAMETER_DATE_END_QUERY, strDateEndQuery );
            url.addParameter( PARAMETER_SEARCH_SUB_SUBJECTS, strSearchSubSubjects );
            url.addParameter( PARAMETER_SUBJECT_ID, strIdSubject );
            Paginator paginator = new Paginator( new ArrayList( listResults ), nNbItemsPerPage, url.getUrl(  ),
                    PARAMETER_PAGE_INDEX, strCurrentPageIndex );
            model.put( MARK_RESULTS_LIST, paginator.getPageItems(  ) );
            model.put( MARK_PAGINATOR, paginator );
        }

        Collection<Subject> listSubject = ( faq != null )
                ? (Collection<Subject>) SubjectHome.findByIdFaq( nIdFaq, plugin ) : new ArrayList<Subject>(  );

        model.put( MARK_SUBJECT_LIST, listSubject );
        model.put( MARK_QUERY, strQuery );
        model.put( MARK_DATE_START_QUERY, strDateStartQuery );
        model.put( MARK_DATE_END_QUERY, strDateEndQuery );
        model.put( MARK_FILTER_SUBJECT, strIdSubject );
        model.put( MARK_FILTER_SEARCH_SUB_SUBJECT, bSearchSubSubjects );
        model.put( MARK_NB_ITEMS_PER_PAGE, strNbItemPerPage );
        model.put( MARK_ERROR, ( strError == null ) ? null : new MVCMessage( strError ) );
        model.put( MARK_LOCALE, request.getLocale(  ) );
        model.put( MARK_FAQ_ID, nIdFaq );
        model.put( MARK_PLUGIN, plugin );
        model.put( MARK_PATH_LABEL, HelpdeskPlugin.PLUGIN_NAME );
        model.put( MARK_FAQ_NAME, ( faq != null ) ? faq.getName(  ) : "" );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RESULTS, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }
    
    /**
     * Return csv file from the selected questions
     * @param request la requete Http
     * @param response la reponse
     */
    public void doExportQuestionCSV( HttpServletRequest request, HttpServletResponse response )
    {
        List<String[]> listToCSVWriter = getCSVListFromRequest( request );

        if ( listToCSVWriter != null )
        {
            String strCsvSeparator = CONSTANT_SEPARATOR;
            StringWriter strWriter = new StringWriter(  );

            CSVWriter csvWriter = new CSVWriter( strWriter, strCsvSeparator.toCharArray(  )[0] );
            csvWriter.writeAll( listToCSVWriter );

            try
            {
                response.setHeader( "Content-Disposition", "attachment; filename=\"helpdesk_question_answer.csv\"" );
                response.setHeader( "Pragma", "public" );
                response.setHeader( "Expires", "0" );
                response.setHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );
                response.setContentType( "text/csv" );
                response.setCharacterEncoding( "UTF-8" );

                response.getWriter(  ).write( strWriter.toString(  ) );
                response.flushBuffer(  );

                csvWriter.close(  );
                strWriter.close(  );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
        }
    }

    /**
     * Format the selected question to fit the csv format
     * @param request la requete Http
     * @param response la reponse
     * @return listToCSVWriter the list of formated question
     */
    private List<String[]> getCSVListFromRequest(HttpServletRequest request) 
    {
    	Plugin plugin = getPlugin(  );   	
    	List<String> listField = null;
    	if ( _listResults != null && _listResults.size( ) > 0)
    	{    	
			//on crée les titres des colonnes du CSV
			List<String[]> listToCSVWriter = new ArrayList<String[]>(  );
			
			for ( QuestionAnswer questionAnswer : _listResults )
			{
				listField = new ArrayList<String>(  );
				listField.add(Integer.toString( questionAnswer.getIdSubject( ) ) );
				listField.add(Integer.toString( questionAnswer.getIdOrder( ) ) );
				listField.add( questionAnswer.getQuestion( ) );
				listField.add( questionAnswer.getAnswer( ) );
				listToCSVWriter.add( transformListToTab( listField ) );
			}
			
			return listToCSVWriter;    		
    	}

    	return null;
    }
    /**
     * Transform a String List in String table
     * @param listField  the String list 
     * @return line the String table
     */
    private static String[] transformListToTab( List<String> listField )
    {
        int i = 0;
        String[] line = new String[listField.size(  )];

        for ( String strField : listField )
        {
            if ( strField != null )
            {
                line[i] = strField;
            }
            else
            {
                line[i] = CONSTANT_EMPTY_STRING;
            }

            i++;
        }

        return line;
    }
}
