/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.helpdesk.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import fr.paris.lutece.util.string.StringUtil;

import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class provides Data Access methods for QuestionAnswerAnswer objects
 */
public final class QuestionAnswerDAO implements IQuestionAnswerDAO
{
    private static final String SQL_QUERY_SELECT = " SELECT id_question_answer, question, answer, id_subject, status, creation_date,id_order FROM helpdesk_question_answer WHERE id_question_answer = ?";
    private static final String SQL_QUERY_INSERT = " INSERT INTO helpdesk_question_answer ( id_question_answer, question, answer, id_subject, status, creation_date, id_order  ) VALUES ( ?, ?, ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM helpdesk_question_answer WHERE id_question_answer = ?";
    private static final String SQL_QUERY_DELETE_ALL = " DELETE FROM helpdesk_question_answer ";
    private static final String SQL_QUERY_DELETE_BY_SUBJECT = " DELETE FROM helpdesk_question_answer WHERE id_subject = ?";
    private static final String SQL_QUERY_UPDATE = " UPDATE helpdesk_question_answer SET id_question_answer = ?, question = ?, answer = ?, id_subject = ?, status = ?, creation_date = ?, id_order = ? WHERE id_question_answer = ?";
    private static final String SQL_QUERY_SELECTALL = " SELECT id_question_answer, question, answer, id_subject, status, creation_date, id_order FROM helpdesk_question_answer ";
    private static final String SQL_QUERY_SELECT_BY_KEYWORDS = " SELECT id_question_answer, question, answer, id_subject, status, creation_date, id_order FROM helpdesk_question_answer ";
    private static final String SQL_QUERY_SELECT_BY_ID_SUBJECT = " SELECT id_question_answer, question, answer, id_subject, status, creation_date, id_order FROM helpdesk_question_answer WHERE id_subject = ? ";
    private static final String SQL_QUERY_SELECT_COUNT = " SELECT count(id_question_answer) FROM helpdesk_question_answer WHERE id_subject = ? ";
    private static final String SQL_QUERY_MAX_ORDER = " SELECT max(id_order) FROM helpdesk_question_answer WHERE id_subject = ? ";

    // /////////////////////////////////////////////////////////////////////////////////////
    // Access methods to data

    /**
     * Insert a new record in the table.
     *
     * @param questionAnswer
     *            The Instance of the object QuestionAnswer
     * @param plugin
     *            The Plugin using this data access service
     */
    public void insert( QuestionAnswer questionAnswer, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            daoUtil.setString( 2, questionAnswer.getQuestion( ) );
            daoUtil.setString( 3, questionAnswer.getAnswer( ) );
            daoUtil.setInt( 4, questionAnswer.getIdSubject( ) );

            if ( questionAnswer.isEnabled( ) )
            {
                daoUtil.setInt( 5, 1 );
            }

            else
            {
                daoUtil.setInt( 5, 0 );
            }

            daoUtil.setTimestamp( 6, new Timestamp( questionAnswer.getCreationDate( ).getTime( ) ) );
            daoUtil.setInt( 7, questionAnswer.getIdOrder( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Delete a record from the table
     *
     * @param nIdQuestionAnswer
     *            The indentifier of the object QuestionAnswer
     * @param plugin
     *            The Plugin using this data access service
     */
    public void delete( int nIdQuestionAnswer, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdQuestionAnswer );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Delete all records from the table
     *
     * @param plugin
     *            The Plugin using this data access service
     */
    public void deleteAll( Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL, plugin ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Delete a record from the table
     *
     * @param nIdSubject
     *            The indentifier of the object QuestionAnswer
     * @param plugin
     *            The Plugin using this data access service
     */
    public void deleteBySubject( int nIdSubject, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_SUBJECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdSubject );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * load the data of QuestionAnswer from the table
     *
     * @param nIdQuestionAnswer
     *            The indentifier of the object QuestionAnswer
     * @param plugin
     *            The Plugin using this data access service
     * @return The Instance of the object QuestionAnswer
     */
    public QuestionAnswer load( int nIdQuestionAnswer, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdQuestionAnswer );
            daoUtil.executeQuery( );

            QuestionAnswer questionanswer = null;

            if ( daoUtil.next( ) )
            {
                questionanswer = new QuestionAnswer( );
                questionanswer.setIdQuestionAnswer( daoUtil.getInt( 1 ) );
                questionanswer.setQuestion( daoUtil.getString( 2 ) );
                questionanswer.setAnswer( daoUtil.getString( 3 ) );
                questionanswer.setIdSubject( daoUtil.getInt( 4 ) );
                questionanswer.setStatus( daoUtil.getInt( 5 ) );
                questionanswer.setCreationDate( daoUtil.getTimestamp( 6 ) );
                questionanswer.setIdOrder( daoUtil.getInt( 7 ) );
            }
            return questionanswer;
        }
    }

    /**
     * Update the record in the table
     *
     * @param questionAnswer
     *            The instance of the QuestionAnswer to update
     * @param plugin
     *            The Plugin using this data access service
     */
    public void store( QuestionAnswer questionAnswer, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            daoUtil.setInt( 1, questionAnswer.getIdQuestionAnswer( ) );
            daoUtil.setString( 2, questionAnswer.getQuestion( ) );
            daoUtil.setString( 3, questionAnswer.getAnswer( ) );
            daoUtil.setInt( 4, questionAnswer.getIdSubject( ) );

            if ( questionAnswer.isEnabled( ) )
            {
                daoUtil.setInt( 5, 1 );
            }
            else
            {
                daoUtil.setInt( 5, 0 );
            }

            daoUtil.setTimestamp( 6, new Timestamp( questionAnswer.getCreationDate( ).getTime( ) ) );
            daoUtil.setInt( 7, questionAnswer.getIdOrder( ) );
            daoUtil.setInt( 8, questionAnswer.getIdQuestionAnswer( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Find all objects.
     *
     * @param plugin
     *            The Plugin using this data access service
     * @return A Collection of objects
     */
    public List<QuestionAnswer> findAll( Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            List<QuestionAnswer> listQuestionAnswer = new ArrayList<>( );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                QuestionAnswer questionanswer = new QuestionAnswer( );
                questionanswer.setIdQuestionAnswer( daoUtil.getInt( 1 ) );
                questionanswer.setQuestion( daoUtil.getString( 2 ) );
                questionanswer.setAnswer( daoUtil.getString( 3 ) );
                questionanswer.setIdSubject( daoUtil.getInt( 4 ) );
                questionanswer.setStatus( daoUtil.getInt( 5 ) );
                questionanswer.setCreationDate( daoUtil.getTimestamp( 6 ) );
                questionanswer.setIdOrder( daoUtil.getInt( 7 ) );
                listQuestionAnswer.add( questionanswer );
            }
            return listQuestionAnswer;
        }
    }

    /**
     * load the data of QuestionAnswer from the table
     *
     * @param strKeywords
     *            The keywords which are searched in question/answer
     * @param plugin
     *            The Plugin using this data access service
     * @return The collection of QuestionAnswer object
     */
    public List<QuestionAnswer> findByKeywords( String strKeywords, Plugin plugin )
    {
        // Temporary variable to avoid reassigning strKeywords :
        String strKeywordsEscaped = StringUtil.substitute( strKeywords, "\\'", "'" );
        List<QuestionAnswer> listQuestionAnswer = new ArrayList<>( );

        StringTokenizer st = new StringTokenizer( strKeywordsEscaped );

        int counter = 0;
        StringBuilder sB = new StringBuilder( );
        sB.append( SQL_QUERY_SELECT_BY_KEYWORDS );

        while ( st.hasMoreTokens( ) )
        {
            String motActuel = st.nextToken( );

            if ( counter == 0 )
            {
                sB.append( " WHERE status = 1 AND (question like '%" + motActuel + "%' OR answer like '%" + motActuel + "%')" );
            }
            else
            {
                sB.append( " AND (question like '%" + motActuel + "%' OR answer like '%" + motActuel + "%')" );
            }

            counter++;
        }

        sB.append( " order by id_subject " );

        try ( DAOUtil daoUtil = new DAOUtil( sB.toString( ), plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                QuestionAnswer questionanswer = new QuestionAnswer( );
                questionanswer.setIdQuestionAnswer( daoUtil.getInt( 1 ) );
                questionanswer.setQuestion( daoUtil.getString( 2 ) );
                questionanswer.setAnswer( daoUtil.getString( 3 ) );
                questionanswer.setIdSubject( daoUtil.getInt( 4 ) );
                questionanswer.setStatus( daoUtil.getInt( 5 ) );
                questionanswer.setCreationDate( daoUtil.getTimestamp( 6 ) );
                questionanswer.setIdOrder( daoUtil.getInt( 7 ) );
                listQuestionAnswer.add( questionanswer );
            }

            return listQuestionAnswer;
        }
    }

    /**
     * Find questions specified by id subject
     * 
     * @param nIdSubject
     *            The Id of the subject
     * @param plugin
     *            The current plugin using this method
     * @return A Collection containing the results
     */
    public Collection<QuestionAnswer> findByIdSubject( int nIdSubject, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_SUBJECT, plugin ) )
        {
            Collection<QuestionAnswer> listQuestionAnswer = new ArrayList<>( );
            daoUtil.setInt( 1, nIdSubject );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                QuestionAnswer questionanswer = new QuestionAnswer( );
                questionanswer.setIdQuestionAnswer( daoUtil.getInt( 1 ) );
                questionanswer.setQuestion( daoUtil.getString( 2 ) );
                questionanswer.setAnswer( daoUtil.getString( 3 ) );
                questionanswer.setIdSubject( daoUtil.getInt( 4 ) );
                questionanswer.setStatus( daoUtil.getInt( 5 ) );
                questionanswer.setCreationDate( daoUtil.getTimestamp( 6 ) );
                questionanswer.setIdOrder( daoUtil.getInt( 7 ) );
                listQuestionAnswer.add( questionanswer );
            }

            return listQuestionAnswer;
        }
    }

    /**
     *
     * @param plugin
     *            The Plugin using this data access service
     * @param nIdSubject
     *            The Subject ID
     * @return count
     */
    public int countbySubject( int nIdSubject, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_COUNT, plugin ) )
        {
            int count = 0;
            daoUtil.setInt( 1, nIdSubject );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                count = daoUtil.getInt( 1 );
            }

            return count;
        }
    }

    /**
     * Get the max order of a given subject
     * 
     * @param nIdSubject
     *            The id of the Subject
     * @param plugin
     *            The {@link Plugin}
     * @return the max order
     */
    public int getMaxOrder( int nIdSubject, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_MAX_ORDER, plugin ) )
        {
            int nMaxOrder = 0;
            daoUtil.setInt( 1, nIdSubject );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nMaxOrder = daoUtil.getInt( 1 );
            }

            return nMaxOrder;
        }
    }
}
