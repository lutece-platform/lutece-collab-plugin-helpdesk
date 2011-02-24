/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This class provides Data Access methods for Subject objects
 */
public final class SubjectDAO implements ISubjectDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_subject ) FROM helpdesk_subject";
    private static final String SQL_QUERY_SELECT = " SELECT id_subject, subject, id_parent, id_order FROM helpdesk_subject WHERE id_subject = ?";
    private static final String SQL_QUERY_INSERT = " INSERT INTO helpdesk_subject ( id_subject, subject, id_parent, id_order ) VALUES ( ?, ?, ?, ?)";
    private static final String SQL_QUERY_DELETE = " DELETE FROM helpdesk_subject WHERE id_subject = ?";
    private static final String SQL_QUERY_UPDATE = " UPDATE helpdesk_subject SET subject = ?, id_parent = ?, id_order = ? WHERE id_subject = ?";
    private static final String SQL_QUERY_SELECTALL = " SELECT id_subject, subject, id_parent, id_order FROM helpdesk_subject ORDER BY id_order ";
    private static final String SQL_QUERY_SELECT_BY_PARENT_ID = " SELECT id_subject, subject, id_order FROM helpdesk_subject WHERE id_parent = ? ORDER BY id_order ";
    private static final String SQL_QUERY_SELECT_BY_ORDER_ID = " SELECT id_subject, subject FROM helpdesk_subject WHERE id_parent = ? AND id_order = ? ORDER BY id_order ";
    private static final String SQL_QUERY_SELECT_QUESTION = "  SELECT id_question_answer, question, answer, id_subject, status, creation_date, id_order FROM helpdesk_question_answer WHERE id_subject = ? ORDER BY id_order";
    private static final String SQL_QUERY_COUNT_QUESTION = " SELECT count(id_question_answer) FROM helpdesk_question_answer WHERE id_subject = ? ";
    private static final String SQL_QUERY_MAX_ORDER = " SELECT max(id_order) FROM helpdesk_subject WHERE id_parent = ? ";
    private static final String SQL_QUERY_SELECT_BY_FAQ_ID = " SELECT DISTINCT s.id_subject, s.subject, s.id_parent, s.id_order FROM helpdesk_subject s, helpdesk_ln_faq_subject lnfs WHERE s.id_subject = lnfs.id_subject AND lnfs.id_faq = ? ORDER BY s.id_order ";
    private static final String SQL_QUERY_SELECT_BY_FAQ_ORDER_ID = " SELECT DISTINCT s.id_subject, s.subject, s.id_parent, s.id_order FROM helpdesk_subject s, helpdesk_ln_faq_subject lnfs WHERE s.id_subject = lnfs.id_subject AND lnfs.id_faq = ? AND s.id_order = ? ORDER BY s.id_order ";

    // ln subject faq
    private static final String SQL_QUERY_INSERT_LN_FAQ = " INSERT INTO helpdesk_ln_faq_subject ( id_subject, id_faq ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_DELETE_LN_FAQ = " DELETE FROM helpdesk_ln_faq_subject WHERE id_subject = ? AND id_faq = ? ";
    private static final String SQL_QUERY_DELETE_ALL_LN_FAQ = " DELETE FROM helpdesk_ln_faq_subject WHERE id_subject = ? ";

    ///////////////////////////////////////////////////////////////////////////////////////
    //Access methods to data

    /**
     * Calculate a new primary key to add a new Subject
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
     * @param abstractSubject The Instance of the object Subject
     * @param plugin The Plugin using this data access service
     */
    public synchronized void insert( AbstractSubject abstractSubject, Plugin plugin )
    {
        Subject subject = (Subject) abstractSubject;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        subject.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, subject.getId(  ) );
        daoUtil.setString( 2, subject.getText(  ) );
        daoUtil.setInt( 3, subject.getIdParent(  ) );
        daoUtil.setInt( 4, subject.getIdOrder(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete a record from the table
     *
     * @param nIdSubject The indentifier of the object Subject
     * @param plugin The Plugin using this data access service
     */
    public void delete( int nIdSubject, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdSubject );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * load the data of Subject from the table
     *
     * @param nIdSubject The indentifier of the object Subject
     * @param plugin The Plugin using this data access service
     * @return The Instance of the object Subject
     */
    public Subject load( int nIdSubject, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdSubject );
        daoUtil.executeQuery(  );

        Subject subject = null;

        if ( daoUtil.next(  ) )
        {
            subject = new Subject(  );
            subject.setId( daoUtil.getInt( 1 ) );
            subject.setText( daoUtil.getString( 2 ) );
            subject.setIdParent( daoUtil.getInt( 3 ) );
            subject.setIdOrder( daoUtil.getInt( 4 ) );
            // Load questions
            subject.setQuestions( findQuestions( nIdSubject, plugin ) );
        }

        daoUtil.free(  );

        return subject;
    }

    /**
     * Update the record in the table
     *
     * @param abstractSubject The instance of the Subject to update
     * @param plugin The Plugin using this data access service
     */
    public void store( AbstractSubject abstractSubject, Plugin plugin )
    {
        Subject subject = (Subject) abstractSubject;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( 1, subject.getText(  ) );
        daoUtil.setInt( 2, subject.getIdParent(  ) );
        daoUtil.setInt( 3, subject.getIdOrder(  ) );
        daoUtil.setInt( 4, subject.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    public List<?extends AbstractSubject> findAll( Plugin plugin )
    {
        List<Subject> list = new ArrayList<Subject>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Subject subject = new Subject(  );
            subject.setId( daoUtil.getInt( 1 ) );
            subject.setText( daoUtil.getString( 2 ) );
            subject.setIdParent( daoUtil.getInt( 3 ) );
            subject.setIdOrder( daoUtil.getInt( 4 ) );
            subject.setQuestions( findQuestions( daoUtil.getInt( 1 ), plugin ) );
            list.add( subject );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    public ReferenceList findSubject( Plugin plugin )
    {
        ReferenceList list = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Finds all {@link Subject} specified by the parent id
     * @param nIdParent The parent subject id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link Subject}
     */
    public Collection<?extends AbstractSubject> findByIdParent( int nIdParent, Plugin plugin )
    {
        Collection<Subject> listSubjects = new ArrayList<Subject>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PARENT_ID, plugin );
        daoUtil.setInt( 1, nIdParent );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Subject subject = new Subject(  );
            subject.setId( daoUtil.getInt( 1 ) );
            subject.setText( daoUtil.getString( 2 ) );
            subject.setIdParent( nIdParent );
            subject.setIdOrder( daoUtil.getInt( 3 ) );
            subject.setQuestions( findQuestions( daoUtil.getInt( 1 ), plugin ) );
            listSubjects.add( subject );
        }

        daoUtil.free(  );

        return listSubjects;
    }

    /**
     * Finds all {@link Subject} specified by the Faq id
     * @param nIdFaq The faq subject id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link Subject}
     */
    public Collection<?extends AbstractSubject> findByIdFaq( int nIdFaq, Plugin plugin )
    {
        Collection<Subject> listSubjects = new ArrayList<Subject>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_FAQ_ID, plugin );
        daoUtil.setInt( 1, nIdFaq );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Subject subject = new Subject(  );
            subject.setId( daoUtil.getInt( 1 ) );
            subject.setText( daoUtil.getString( 2 ) );
            subject.setIdParent( daoUtil.getInt( 3 ) );
            subject.setIdOrder( daoUtil.getInt( 4 ) );
            subject.setQuestions( findQuestions( daoUtil.getInt( 1 ), plugin ) );
            listSubjects.add( subject );
        }

        daoUtil.free(  );

        return listSubjects;
    }

    /**
     * Returns all questions on a subject
     * @param nIdSubject The identifier of the subject
     * @param plugin The Plugin using this data access service
     * @return A collection of questions
     */
    public List<QuestionAnswer> findQuestions( int nIdSubject, Plugin plugin )
    {
        List<QuestionAnswer> list = new ArrayList<QuestionAnswer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_QUESTION, plugin );
        daoUtil.setInt( 1, nIdSubject );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            QuestionAnswer question = new QuestionAnswer(  );
            question.setIdQuestionAnswer( daoUtil.getInt( 1 ) );
            question.setQuestion( daoUtil.getString( 2 ) );
            question.setAnswer( daoUtil.getString( 3 ) );
            question.setIdSubject( daoUtil.getInt( 4 ) );
            question.setStatus( daoUtil.getInt( 5 ) );
            question.setCreationDate( daoUtil.getTimestamp( 6 ) );
            question.setIdOrder( daoUtil.getInt( 7 ) );
            list.add( question );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * return the count of all announce for Field
     * @param plugin The current plugin using this method
     * @param nIdSubject The subject ID
     * @return count of announce for Field
     */
    public int countQuestion( int nIdSubject, Plugin plugin )
    {
        int nCount = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_QUESTION, plugin );
        daoUtil.setInt( 1, nIdSubject );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nCount = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nCount;
    }

    /**
     * Get the max order of a parent subject
     * @param nIdParent The id of the parent subject
     * @param plugin The {@link Plugin}
     * @return the max order
     */
    public int getMaxOrder( int nIdParent, Plugin plugin )
    {
        int nMaxOrder = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_MAX_ORDER, plugin );
        daoUtil.setInt( 1, nIdParent );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nMaxOrder = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nMaxOrder;
    }

    /**
     * Find a subject with the parent id and the order.
     * @param nIdParent The parent Id
     * @param nOrder The order
     * @param plugin The {@link Plugin}
     * @return the {@link Subject}
     */
    public Subject findByOrder( int nIdParent, int nOrder, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ORDER_ID, plugin );
        daoUtil.setInt( 1, nIdParent );
        daoUtil.setInt( 2, nOrder );
        daoUtil.executeQuery(  );

        Subject subject = null;

        if ( daoUtil.next(  ) )
        {
            subject = new Subject(  );
            subject.setId( daoUtil.getInt( 1 ) );
            subject.setText( daoUtil.getString( 2 ) );
            subject.setIdParent( nIdParent );
            subject.setIdOrder( nOrder );
            // Load questions
            subject.setQuestions( findQuestions( daoUtil.getInt( 1 ), plugin ) );
        }

        daoUtil.free(  );

        return subject;
    }

    /**
     * Find a subject with the faq id and the order.
     * @param nIdFaq The faq Id
     * @param nOrder The order
     * @param plugin The {@link Plugin}
     * @return the {@link Subject}
     */
    public Subject findByFaqOrder( int nIdFaq, int nOrder, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_FAQ_ORDER_ID, plugin );
        daoUtil.setInt( 1, nIdFaq );
        daoUtil.setInt( 2, nOrder );
        daoUtil.executeQuery(  );

        Subject subject = null;

        if ( daoUtil.next(  ) )
        {
            subject = new Subject(  );
            subject.setId( daoUtil.getInt( 1 ) );
            subject.setText( daoUtil.getString( 2 ) );
            subject.setIdParent( daoUtil.getInt( 3 ) );
            subject.setIdOrder( daoUtil.getInt( 4 ) );
            // Load questions
            subject.setQuestions( findQuestions( daoUtil.getInt( 1 ), plugin ) );
        }

        daoUtil.free(  );

        return subject;
    }

    /**
     * Insert a new record in the table.
     *
     * @param nIdAbstractSubject The id of the object Subject
     * @param nIdFaq The parent id of the object Faq
     * @param plugin The Plugin using this data access service
     */
    public synchronized void insertLinkToFaq( int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_LN_FAQ, plugin );

        daoUtil.setInt( 1, nIdAbstractSubject );
        daoUtil.setInt( 2, nIdFaq );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * delete a record in the table.
     *
     * @param nIdAbstractSubject The id of the object Subject
     * @param nIdFaq The parent id of the object Faq
     * @param plugin The Plugin using this data access service
     */
    public synchronized void deleteLinkToFaq( int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_LN_FAQ, plugin );

        daoUtil.setInt( 1, nIdAbstractSubject );
        daoUtil.setInt( 2, nIdFaq );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * delete a record in the table.
     *
     * @param nIdAbstractSubject The id of the object Subject
     * @param plugin The Plugin using this data access service
     */
    public synchronized void deleteAllLinksToFaq( int nIdAbstractSubject, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL_LN_FAQ, plugin );

        daoUtil.setInt( 1, nIdAbstractSubject );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
