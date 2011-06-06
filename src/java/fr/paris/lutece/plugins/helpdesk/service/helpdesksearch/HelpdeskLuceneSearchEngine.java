/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.helpdesk.service.helpdesksearch;

import fr.paris.lutece.plugins.helpdesk.business.Subject;
import fr.paris.lutece.plugins.helpdesk.service.HelpdeskPlugin;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.misc.ChainedFilter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * LuceneSearchEngine
 */
public class HelpdeskLuceneSearchEngine implements HelpdeskSearchEngine
{
    private static final String OPEN_PARENTHESIS = "(";
    private static final String SPACE = " ";
    private static final String EMPTY_STRING = "";
    private static final String CLOSE_PARENTHESIS = ")";

    /**
     * Return search results
     * @param nIdFaq The id Faq
     * @param strContent The search query
     * @param dateBegin The date begin
     * @param dateEnd The date end
     * @param subject The {@link Subject}
     * @param bSearchSubSubjects true if the query must include sub-subjects
     * @param request The {@link HttpServletRequest}
     * @return Results as a collection of SearchResult
     */
    public List<SearchResult> getSearchResults( int nIdFaq, String strContent, Date dateBegin, Date dateEnd,
        Subject subject, boolean bSearchSubSubjects, HttpServletRequest request )
    {
        ArrayList<SearchItem> listResults = new ArrayList<SearchItem>(  );
        Searcher searcher = null;

        Filter filterRole = getFilterRoles( request );

        try
        {
            searcher = new IndexSearcher( IndexationService.getDirectoryIndex(  ), true );

            Collection<String> queries = new ArrayList<String>(  );
            Collection<String> fields = new ArrayList<String>(  );
            Collection<BooleanClause.Occur> flags = new ArrayList<BooleanClause.Occur>(  );

            //Faq Id
            if( nIdFaq != -1 )
            {
	            Query queryFaqId = new TermQuery( new Term( HelpdeskSearchItem.FIELD_FAQ_ID, String.valueOf( nIdFaq ) ) );
	            queries.add( queryFaqId.toString(  ) );
	            fields.add( HelpdeskSearchItem.FIELD_FAQ_ID );
	            flags.add( BooleanClause.Occur.MUST );
            }

            //Type (=helpdesk)
            PhraseQuery queryType = new PhraseQuery(  );
            queryType.add( new Term( HelpdeskSearchItem.FIELD_TYPE, HelpdeskPlugin.PLUGIN_NAME ) );
            queries.add( queryType.toString(  ) );
            fields.add( HelpdeskSearchItem.FIELD_TYPE );
            flags.add( BooleanClause.Occur.MUST );

            //Content
            if ( ( strContent != null ) && !strContent.equals( EMPTY_STRING ) )
            {
                Query queryContent = new TermQuery( new Term( HelpdeskSearchItem.FIELD_CONTENTS, strContent ) );
                queries.add( queryContent.toString(  ) );
                fields.add( HelpdeskSearchItem.FIELD_CONTENTS );
                flags.add( BooleanClause.Occur.MUST );
            }

            //Dates
            if ( ( dateBegin != null ) && ( dateEnd != null ) )
            {
                String strDateBegin = DateTools.dateToString( dateBegin, DateTools.Resolution.DAY );
                String strDateEnd = DateTools.dateToString( dateEnd, DateTools.Resolution.DAY );
                Query queryDate = new TermRangeQuery( HelpdeskSearchItem.FIELD_DATE, strDateBegin, strDateEnd, true, true );
                queries.add( queryDate.toString(  ) );
                fields.add( HelpdeskSearchItem.FIELD_DATE );
                flags.add( BooleanClause.Occur.MUST );
            }

            //Subjects
            if ( ( bSearchSubSubjects ) && ( subject != null ) )
            {
                Plugin plugin = PluginService.getPlugin( HelpdeskPlugin.PLUGIN_NAME );
                Collection<Term> listSubjects = new ArrayList<Term>(  );
                getListSubjects( listSubjects, subject, plugin );

                String strQuerySubject = OPEN_PARENTHESIS;

                for ( Term term : listSubjects )
                {
                    Query querySubject = new TermQuery( term );
                    strQuerySubject += ( querySubject.toString(  ) + SPACE );
                }

                strQuerySubject += CLOSE_PARENTHESIS;
                queries.add( strQuerySubject );
                fields.add( HelpdeskSearchItem.FIELD_SUBJECT );
                flags.add( BooleanClause.Occur.MUST );
            }
            else
            {
                if ( ( subject != null ) )
                {
                    Query querySubject = new TermQuery( new Term( HelpdeskSearchItem.FIELD_SUBJECT,
                                String.valueOf( subject.getId(  ) ) ) );
                    queries.add( querySubject.toString(  ) );
                    fields.add( HelpdeskSearchItem.FIELD_SUBJECT );
                    flags.add( BooleanClause.Occur.MUST );
                }
            }

            Query queryMulti = MultiFieldQueryParser.parse( IndexationService.LUCENE_INDEX_VERSION, (String[]) queries.toArray( new String[queries.size(  )] ),
                    (String[]) fields.toArray( new String[fields.size(  )] ),
                    (BooleanClause.Occur[]) flags.toArray( new BooleanClause.Occur[flags.size(  )] ),
                    IndexationService.getAnalyser(  ) );

            TopDocs topDocs = searcher.search( queryMulti, filterRole, LuceneSearchEngine.MAX_RESPONSES );

            ScoreDoc[] hits = topDocs.scoreDocs;

            for (int i = 0; i < hits.length; i++)
            {
            	int docId = hits[i].doc;
                Document document = searcher.doc(docId);
                SearchItem si = new SearchItem( document );
                listResults.add( si );
            }

            searcher.close(  );
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return convertList( listResults );
    }

    /**
     * Generate the Lutece role filter if necessary
     * @param request The {@link HttpServletRequest}
     * @return The {@link Filter} by Lutece Role
     */
    private Filter getFilterRoles( HttpServletRequest request )
    {
        Filter filterRole = null;
        boolean bFilterResult = false;
        LuteceUser user = null;

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            user = SecurityService.getInstance(  ).getRegisteredUser( request );

            Filter[] filtersRole = null;

            if ( user != null )
            {
                String[] userRoles = SecurityService.getInstance(  ).getRolesByUser( user );

                if ( userRoles != null )
                {
                    filtersRole = new Filter[userRoles.length + 1];

                    for ( int i = 0; i < userRoles.length; i++ )
                    {
                        Query queryRole = new TermQuery( new Term( SearchItem.FIELD_ROLE, userRoles[i] ) );
                        filtersRole[i] = new CachingWrapperFilter( new QueryWrapperFilter( queryRole ) );
                    }
                }
                else
                {
                    bFilterResult = true;
                }
            }
            else
            {
                filtersRole = new Filter[1];
            }

            if ( !bFilterResult )
            {
                Query queryRole = new TermQuery( new Term( SearchItem.FIELD_ROLE, Page.ROLE_NONE ) );
                filtersRole[filtersRole.length - 1] = new CachingWrapperFilter( new QueryWrapperFilter( queryRole ) );
                filterRole = new ChainedFilter( filtersRole, ChainedFilter.OR );
            }
        }

        return filterRole;
    }

    /**
     * Convert the SearchItem list on SearchResult list
     * @param listSource The source list
     * @return The result list
     */
    private List<SearchResult> convertList( List<SearchItem> listSource )
    {
        List<SearchResult> listDest = new ArrayList<SearchResult>(  );

        for ( SearchItem item : listSource )
        {
            SearchResult result = new SearchResult(  );
            result.setId( item.getId(  ) );

            try
            {
                result.setDate( DateTools.stringToDate( item.getDate(  ) ) );
            }
            catch ( ParseException e )
            {
                AppLogService.error( "Bad Date Format for indexed item \"" + item.getTitle(  ) + "\" : " +
                    e.getMessage(  ) );
            }

            result.setUrl( item.getUrl(  ) );
            result.setTitle( item.getTitle(  ) );
            result.setSummary( item.getSummary(  ) );
            result.setType( item.getType(  ) );
            listDest.add( result );
        }

        return listDest;
    }

    /**
     * Generate the list of terms with the subjects and sub-subjects
     * @param listTerms The list of terms
     * @param subject The parent subject
     * @param plugin The plugin
     */
    private void getListSubjects( Collection<Term> listTerms, Subject subject, Plugin plugin )
    {
        if ( subject != null )
        {
            listTerms.add( new Term( HelpdeskSearchItem.FIELD_SUBJECT, String.valueOf( subject.getId(  ) ) ) );

            for ( Subject childSubject : subject.getChilds( plugin ) )
            {
                getListSubjects( listTerms, childSubject, plugin );
            }
        }
    }
}
