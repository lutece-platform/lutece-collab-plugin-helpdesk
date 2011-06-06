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
package fr.paris.lutece.plugins.helpdesk.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/*  ajouter ici : import fr.paris.lutece.(nom_projet).util.*; */

/**
 * This class provides Data Access methods for VisitorQuestionAnswer objects
 */
public final class VisitorQuestionDAO implements IVisitorQuestionDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_visitor_question ) FROM helpdesk_visitor_question ";
    private static final String SQL_QUERY_SELECT = " SELECT id_visitor_question, last_name, first_name, email, question, answer, date_visitor_question, id_user, id_theme FROM helpdesk_visitor_question WHERE id_visitor_question = ?";
    private static final String SQL_QUERY_INSERT = " INSERT INTO helpdesk_visitor_question ( id_visitor_question, last_name, first_name, email, question, answer, date_visitor_question, id_user, id_theme ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM helpdesk_visitor_question WHERE id_visitor_question = ?";
    private static final String SQL_QUERY_UPDATE = " UPDATE helpdesk_visitor_question SET id_visitor_question = ?, last_name = ?, first_name = ?, email = ?, question = ?, answer = ?, id_user = ? WHERE id_visitor_question = ?";
    private static final String SQL_QUERY_SELECTALL = " SELECT id_visitor_question, last_name, first_name, email, question, answer, date_visitor_question, id_user, id_theme FROM helpdesk_visitor_question ";
    private static final String SQL_QUERY_SELECT_BY_USER = " SELECT id_visitor_question, last_name, first_name, email, question, answer, id_question_type, date_visitor_question, id_user, id_question_topic FROM helpdesk_visitor_question WHERE id_user = ?";
    private static final String SQL_QUERY_SELECT_BY_THEME = " SELECT id_visitor_question, last_name, first_name, email, question, answer, date_visitor_question, id_user, id_theme FROM helpdesk_visitor_question WHERE id_theme = ? ORDER BY date_visitor_question DESC ";
    private static final String SQL_QUERY_SELECT_ARHIVED_BY_THEME = " SELECT id_visitor_question, last_name, first_name, email, question, answer, date_visitor_question, id_user, id_theme FROM helpdesk_visitor_question WHERE answer != ? AND id_theme = ? ORDER BY date_visitor_question DESC ";

    /** This class implements the Singleton design pattern. */
    private static VisitorQuestionDAO _dao = new VisitorQuestionDAO(  );

    /**
     * Creates a new VisitorQuestionDAO object.
     */
    private VisitorQuestionDAO(  )
    {
    }

    /**
     * Returns the unique instance of the singleton.
     *
     * @return the instance
     */
    static VisitorQuestionDAO getInstance(  )
    {
        return _dao;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Access methods to data

    /**
     * Calculate a new primary key to add a new VisitorQuestion
     *
     * @param plugin The Plugin using this data access service
     * @return The new key.
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;

        daoUtil.free(  );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     *
     * @param visitorQuestion The Instance of the object VisitorQuestion
     * @param plugin The Plugin using this data access service
     */
    public synchronized void insert( VisitorQuestion visitorQuestion, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        visitorQuestion.setIdVisitorQuestion( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, visitorQuestion.getIdVisitorQuestion(  ) );
        daoUtil.setString( 2, visitorQuestion.getLastname(  ) );
        daoUtil.setString( 3, visitorQuestion.getFirstname(  ) );
        daoUtil.setString( 4, visitorQuestion.getEmail(  ) );
        daoUtil.setString( 5, visitorQuestion.getQuestion(  ) );
        daoUtil.setString( 6, visitorQuestion.getAnswer(  ) );
        daoUtil.setDate( 7, visitorQuestion.getDate(  ) );
        daoUtil.setInt( 8, visitorQuestion.getIdUser(  ) );
        daoUtil.setInt( 9, visitorQuestion.getIdTheme(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete a record from the table
     *
     * @param nIdVisitorQuestion The indentifier of the object VisitorQuestion
     * @param plugin The Plugin using this data access service
     */
    public void delete( int nIdVisitorQuestion, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdVisitorQuestion );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * load the data of VisitorQuestion from the table
     *
     * @param nIdVisitorQuestion The indentifier of the object VisitorQuestion
     * @param plugin The Plugin using this data access service
     * @return The Instance of the object VisitorQuestion
     */
    public VisitorQuestion load( int nIdVisitorQuestion, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdVisitorQuestion );
        daoUtil.executeQuery(  );

        VisitorQuestion visitorQuestion = null;

        if ( daoUtil.next(  ) )
        {
            visitorQuestion = new VisitorQuestion(  );
            visitorQuestion.setIdVisitorQuestion( daoUtil.getInt( 1 ) );
            visitorQuestion.setLastname( daoUtil.getString( 2 ) );
            visitorQuestion.setFirstname( daoUtil.getString( 3 ) );
            visitorQuestion.setEmail( daoUtil.getString( 4 ) );
            visitorQuestion.setQuestion( daoUtil.getString( 5 ) );
            visitorQuestion.setAnswer( daoUtil.getString( 6 ) );
            visitorQuestion.setDate( daoUtil.getDate( 7 ) );
            visitorQuestion.setIdUser( daoUtil.getInt( 8 ) );
            visitorQuestion.setIdTheme( daoUtil.getInt( 9 ) );
        }

        daoUtil.free(  );

        return visitorQuestion;
    }

    /**
     * Update the record in the table
     *
     * @param visitorQuestion The instance of the VisitorQuestion to update
     * @param plugin The Plugin using this data access service
     */
    public void store( VisitorQuestion visitorQuestion, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, visitorQuestion.getIdVisitorQuestion(  ) );
        daoUtil.setString( 2, visitorQuestion.getLastname(  ) );
        daoUtil.setString( 3, visitorQuestion.getFirstname(  ) );
        daoUtil.setString( 4, visitorQuestion.getEmail(  ) );
        daoUtil.setString( 5, visitorQuestion.getQuestion(  ) );
        daoUtil.setString( 6, visitorQuestion.getAnswer(  ) );
        daoUtil.setInt( 7, visitorQuestion.getIdUser(  ) );
        daoUtil.setInt( 8, visitorQuestion.getIdVisitorQuestion(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Find all objects
     * @param plugin The Plugin using this data access service
     * @return A Collection of objects
     */
    public Collection<VisitorQuestion> findAll( Plugin plugin )
    {
        Collection<VisitorQuestion> list = new ArrayList<VisitorQuestion>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            VisitorQuestion visitorQuestion = new VisitorQuestion(  );
            visitorQuestion.setIdVisitorQuestion( daoUtil.getInt( 1 ) );
            visitorQuestion.setLastname( daoUtil.getString( 2 ) );
            visitorQuestion.setFirstname( daoUtil.getString( 3 ) );
            visitorQuestion.setEmail( daoUtil.getString( 4 ) );
            visitorQuestion.setQuestion( daoUtil.getString( 5 ) );
            visitorQuestion.setAnswer( daoUtil.getString( 6 ) );
            visitorQuestion.setDate( daoUtil.getDate( 7 ) );
            visitorQuestion.setIdUser( daoUtil.getInt( 8 ) );
            visitorQuestion.setIdTheme( daoUtil.getInt( 9 ) );
            list.add( visitorQuestion );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Find all objects
     * @param nIdUser The User ID
     * @param plugin The Plugin using this data access service
     * @return A Collection of objects
     */
    public Collection<VisitorQuestion> findByUser( int nIdUser, Plugin plugin )
    {
        Collection<VisitorQuestion> list = new ArrayList<VisitorQuestion>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_USER, plugin );
        daoUtil.setInt( 1, nIdUser );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            VisitorQuestion visitorQuestion = new VisitorQuestion(  );
            visitorQuestion.setIdVisitorQuestion( daoUtil.getInt( 1 ) );
            visitorQuestion.setLastname( daoUtil.getString( 2 ) );
            visitorQuestion.setFirstname( daoUtil.getString( 3 ) );
            visitorQuestion.setEmail( daoUtil.getString( 4 ) );
            visitorQuestion.setQuestion( daoUtil.getString( 5 ) );
            visitorQuestion.setAnswer( daoUtil.getString( 6 ) );
            visitorQuestion.setDate( daoUtil.getDate( 7 ) );
            visitorQuestion.setIdUser( daoUtil.getInt( 8 ) );
            visitorQuestion.setIdTheme( daoUtil.getInt( 9 ) );
            list.add( visitorQuestion );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Find all objects
     * @param nIdTheme The theme ID
     * @param plugin The Plugin using this data access service
     * @return A Collection of objects
     */
    public Collection<VisitorQuestion> findByTheme( int nIdTheme, Plugin plugin )
    {
        Collection<VisitorQuestion> list = new ArrayList<VisitorQuestion>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_THEME, plugin );
        daoUtil.setInt( 1, nIdTheme );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            VisitorQuestion visitorQuestion = new VisitorQuestion(  );
            visitorQuestion.setIdVisitorQuestion( daoUtil.getInt( 1 ) );
            visitorQuestion.setLastname( daoUtil.getString( 2 ) );
            visitorQuestion.setFirstname( daoUtil.getString( 3 ) );
            visitorQuestion.setEmail( daoUtil.getString( 4 ) );
            visitorQuestion.setQuestion( daoUtil.getString( 5 ) );
            visitorQuestion.setAnswer( daoUtil.getString( 6 ) );
            visitorQuestion.setDate( daoUtil.getDate( 7 ) );
            visitorQuestion.setIdUser( daoUtil.getInt( 8 ) );
            visitorQuestion.setIdTheme( daoUtil.getInt( 9 ) );
            list.add( visitorQuestion );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Find all archived questions by Theme
     * @param nIdTheme The Theme id
     * @param plugin The Plugin using this data access service
     * @return A Collection of objects
     */
    public Collection<VisitorQuestion> findArchivedQuestionsByTheme( int nIdTheme, Plugin plugin )
    {
        Collection<VisitorQuestion> list = new ArrayList<VisitorQuestion>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ARHIVED_BY_THEME, plugin );
        daoUtil.setString( 1, "" ); //answer != "" -> archived question
        daoUtil.setInt( 2, nIdTheme );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            VisitorQuestion visitorQuestion = new VisitorQuestion(  );
            visitorQuestion.setIdVisitorQuestion( daoUtil.getInt( 1 ) );
            visitorQuestion.setLastname( daoUtil.getString( 2 ) );
            visitorQuestion.setFirstname( daoUtil.getString( 3 ) );
            visitorQuestion.setEmail( daoUtil.getString( 4 ) );
            visitorQuestion.setQuestion( daoUtil.getString( 5 ) );
            visitorQuestion.setAnswer( daoUtil.getString( 6 ) );
            visitorQuestion.setDate( daoUtil.getDate( 7 ) );
            visitorQuestion.setIdUser( daoUtil.getInt( 8 ) );
            visitorQuestion.setIdTheme( daoUtil.getInt( 9 ) );
            list.add( visitorQuestion );
        }

        daoUtil.free(  );

        return list;
    }
}
