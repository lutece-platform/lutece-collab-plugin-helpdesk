/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.helpdesk.business.QuestionAnswer;
import fr.paris.lutece.plugins.helpdesk.business.QuestionAnswerHome;
import fr.paris.lutece.plugins.helpdesk.business.Subject;
import fr.paris.lutece.plugins.helpdesk.service.search.HelpdeskIndexer;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * DocumentSearchService
 */
public class HelpdeskSearchService
{
    private static final String BEAN_SEARCH_ENGINE = "helpdeskSearchEngine";
    private static final String REGEX_ID = "^[\\d]+_(" + HelpdeskIndexer.SHORT_NAME_SUBJECT + "|" +
        HelpdeskIndexer.SHORT_NAME_QUESTION_ANSWER + ")$";
    private static final String UNDERSCORE = "_";

    // Constants corresponding to the variables defined in the lutece.properties file
    private static HelpdeskSearchService _singleton;

    /**
     * Get the HelpdeskSearchService instance
     *
     * @return The {@link HelpdeskSearchService}
     */
    public static HelpdeskSearchService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new HelpdeskSearchService(  );
        }

        return _singleton;
    }

    /**
     * Return search results
     *
     * @param nIdFaq The Id Faq
     * @param strQuery The search query
     * @param dateBegin The date begin
     * @param dateEnd The date end
     * @param subject The {@link Subject}
     * @param bSearchSubSubjects true if research must be done in sub-subjects
     * @param request The {@link HttpServletRequest}
     * @param plugin The plugin
     * @return Results as a collection of {@link QuestionAnswer}
     */
    public Collection<QuestionAnswer> getSearchResults( int nIdFaq, String strQuery, Date dateBegin, Date dateEnd,
        Subject subject, boolean bSearchSubSubjects, HttpServletRequest request, Plugin plugin )
    {
        Collection<QuestionAnswer> listQuestionAnswer = new ArrayList<QuestionAnswer>(  );
        HelpdeskSearchEngine engine = (HelpdeskSearchEngine) SpringContextService.getPluginBean( plugin.getName(  ),
                BEAN_SEARCH_ENGINE );
        List<SearchResult> listResults = engine.getSearchResults( nIdFaq, strQuery, dateBegin, dateEnd, subject,
                bSearchSubSubjects, request );

        for ( SearchResult searchResult : listResults )
        {
            if ( ( searchResult.getId(  ) != null ) && searchResult.getId(  ).matches( REGEX_ID ) )
            {
                QuestionAnswer questionAnswer = QuestionAnswerHome.findByPrimaryKey( Integer.parseInt( 
                            searchResult.getId(  ).substring( 0, searchResult.getId(  ).indexOf( UNDERSCORE ) ) ),
                        plugin );
                if( questionAnswer != null )
                {
                	listQuestionAnswer.add( questionAnswer );
                }                
            }
        }

        return listQuestionAnswer;
    }
    
    /**
     * Return search results
     *
     * @param strQuery The search query
     * @param dateBegin The date begin
     * @param dateEnd The date end
     * @param subject The {@link Subject}
     * @param bSearchSubSubjects true if research must be done in sub-subjects
     * @param request The {@link HttpServletRequest}
     * @param plugin The plugin
     * @return Results as a collection of {@link QuestionAnswer}
     */
    public Collection<QuestionAnswer> getSearchResults( String strQuery, Date dateBegin, Date dateEnd,
    		HttpServletRequest request, Plugin plugin )
    {
    	return getSearchResults( -1, strQuery, dateBegin, dateEnd, null, false, request, plugin );
    }
}
