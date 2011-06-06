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
import java.util.List;


/**
 * This class provides Data Access methods for Theme objects
 */
public final class ThemeDAO implements IThemeDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_theme ) FROM helpdesk_theme";
    private static final String SQL_QUERY_SELECT = " SELECT theme, id_mailing_list, id_parent, id_order FROM helpdesk_theme WHERE id_theme = ?";
    private static final String SQL_QUERY_INSERT = " INSERT INTO helpdesk_theme ( id_theme, theme, id_mailing_list, id_parent, id_order ) VALUES ( ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM helpdesk_theme WHERE id_theme = ?";
    private static final String SQL_QUERY_UPDATE = " UPDATE helpdesk_theme SET theme = ?, id_mailing_list = ?, id_parent = ?, id_order = ? WHERE id_theme = ?";
    private static final String SQL_QUERY_SELECTALL = " SELECT id_theme, theme, id_mailing_list, id_parent, id_order FROM helpdesk_theme ORDER BY id_order";
    private static final String SQL_QUERY_SELECT_BY_PARENT_ID = " SELECT id_theme, theme, id_mailing_list, id_order FROM helpdesk_theme WHERE id_parent = ? ORDER BY id_order ";
    private static final String SQL_QUERY_SELECT_BY_ORDER_ID = " SELECT id_theme, theme, id_mailing_list FROM helpdesk_theme WHERE id_parent = ? AND id_order = ? ORDER BY id_order ";
    private static final String SQL_QUERY_SELECT_QUESTION = " SELECT id_visitor_question, last_name, first_name, email, question, answer, date_visitor_question, id_user FROM helpdesk_visitor_question WHERE answer = ? AND id_theme = ? ORDER BY date_visitor_question DESC ";
    private static final String SQL_QUERY_MAX_ORDER = " SELECT max(id_order) FROM helpdesk_theme WHERE id_parent = ? ";
    private static final String SQL_QUERY_SELECT_BY_FAQ_ID = " SELECT DISTINCT t.id_theme, t.theme, t.id_mailing_list, t.id_parent, t.id_order FROM helpdesk_theme t, helpdesk_ln_faq_theme lnft WHERE t.id_theme = lnft.id_theme AND lnft.id_faq = ? ORDER BY t.id_order ";
    private static final String SQL_QUERY_SELECT_BY_FAQ_ORDER_ID = " SELECT DISTINCT t.id_theme, t.theme, t.id_mailing_list, t.id_parent, t.id_order FROM helpdesk_theme t, helpdesk_ln_faq_theme lnft WHERE t.id_theme = lnft.id_theme AND lnft.id_faq = ? AND t.id_order = ? ORDER BY t.id_order ";

    // ln theme faq
    private static final String SQL_QUERY_INSERT_LN_FAQ = " INSERT INTO helpdesk_ln_faq_theme ( id_theme, id_faq ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_DELETE_LN_FAQ = " DELETE FROM helpdesk_ln_faq_theme WHERE id_theme = ? AND id_faq = ? ";
    private static final String SQL_QUERY_DELETE_ALL_LN_FAQ = " DELETE FROM helpdesk_ln_faq_theme WHERE id_theme = ? ";

    ///////////////////////////////////////////////////////////////////////////////////////
    //Access methods to data

    /**
     * Calculate a new primary key to add a new Theme
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
     * @param abstractSubject The Instance of the object theme
     * @param plugin The Plugin using this data access service
     */
    public synchronized void insert( AbstractSubject abstractSubject, Plugin plugin )
    {
        Theme theme = (Theme) abstractSubject;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        theme.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, theme.getId(  ) );
        daoUtil.setString( 2, theme.getText(  ) );
        daoUtil.setInt( 3, theme.getIdMailingList(  ) );
        daoUtil.setInt( 4, theme.getIdParent(  ) );
        daoUtil.setInt( 5, theme.getIdOrder(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete a record from the table
     *
     * @param nIdTheme The indentifier of the object Theme
     * @param plugin The Plugin using this data access service
     */
    public void delete( int nIdTheme, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdTheme );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * load the data of Theme from the table
     *
     * @param nIdTheme The indentifier of the object Theme
     * @param plugin The Plugin using this data access service
     * @return The Instance of the object Theme
     */
    public Theme load( int nIdTheme, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdTheme );
        daoUtil.executeQuery(  );

        Theme theme = null;

        if ( daoUtil.next(  ) )
        {
            theme = new Theme(  );
            theme.setId( nIdTheme );
            theme.setText( daoUtil.getString( 1 ) );
            theme.setIdMailingList( daoUtil.getInt( 2 ) );
            theme.setIdParent( daoUtil.getInt( 3 ) );
            theme.setIdOrder( daoUtil.getInt( 4 ) );
            // Load questions
            theme.setQuestions( findQuestions( nIdTheme, plugin ) );
        }

        daoUtil.free(  );

        return theme;
    }

    /**
     * Update the record in the table
     *
     * @param abstractSubject The instance of the Theme to update
     * @param plugin The Plugin using this data access service
     */
    public void store( AbstractSubject abstractSubject, Plugin plugin )
    {
        Theme theme = (Theme) abstractSubject;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( 1, theme.getText(  ) );
        daoUtil.setInt( 2, theme.getIdMailingList(  ) );
        daoUtil.setInt( 3, theme.getIdParent(  ) );
        daoUtil.setInt( 4, theme.getIdOrder(  ) );
        daoUtil.setInt( 5, theme.getId(  ) );

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
        List<Theme> list = new ArrayList<Theme>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Theme theme = new Theme(  );
            theme.setId( daoUtil.getInt( 1 ) );
            theme.setText( daoUtil.getString( 2 ) );
            theme.setIdMailingList( daoUtil.getInt( 3 ) );
            theme.setIdParent( daoUtil.getInt( 4 ) );
            theme.setIdOrder( daoUtil.getInt( 5 ) );
            theme.setQuestions( findQuestions( daoUtil.getInt( 1 ), plugin ) );
            list.add( theme );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Finds all {@link Theme} specified by the parent id
     * @param nIdParent The parent Theme id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link Theme}
     */
    public Collection<?extends AbstractSubject> findByIdParent( int nIdParent, Plugin plugin )
    {
        Collection<Theme> listThemes = new ArrayList<Theme>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PARENT_ID, plugin );
        daoUtil.setInt( 1, nIdParent );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Theme theme = new Theme(  );
            theme.setId( daoUtil.getInt( 1 ) );
            theme.setText( daoUtil.getString( 2 ) );
            theme.setIdMailingList( daoUtil.getInt( 3 ) );
            theme.setIdParent( nIdParent );
            theme.setIdOrder( daoUtil.getInt( 4 ) );
            theme.setQuestions( findQuestions( daoUtil.getInt( 1 ), plugin ) );
            listThemes.add( theme );
        }

        daoUtil.free(  );

        return listThemes;
    }

    /**
     * Finds all {@link Theme} specified by the Faq id
     * @param nIdFaq The faq id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link Theme}
     */
    public Collection<?extends AbstractSubject> findByIdFaq( int nIdFaq, Plugin plugin )
    {
        Collection<Theme> listThemes = new ArrayList<Theme>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_FAQ_ID, plugin );
        daoUtil.setInt( 1, nIdFaq );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Theme theme = new Theme(  );
            theme.setId( daoUtil.getInt( 1 ) );
            theme.setText( daoUtil.getString( 2 ) );
            theme.setIdMailingList( daoUtil.getInt( 3 ) );
            theme.setIdParent( daoUtil.getInt( 4 ) );
            theme.setIdOrder( daoUtil.getInt( 5 ) );
            theme.setQuestions( findQuestions( daoUtil.getInt( 1 ), plugin ) );
            listThemes.add( theme );
        }

        daoUtil.free(  );

        return listThemes;
    }

    /**
     * Returns all questions on a Theme
     * @param nIdTheme The identifier of the Theme
     * @param plugin The Plugin using this data access service
     * @return A collection of questions
     */
    public Collection<VisitorQuestion> findQuestions( int nIdTheme, Plugin plugin )
    {
        Collection<VisitorQuestion> listVisitorQuestion = new ArrayList<VisitorQuestion>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_QUESTION, plugin );
        daoUtil.setString( 1, "" ); // answer == "" -> not archived question
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
            visitorQuestion.setIdTheme( nIdTheme );
            listVisitorQuestion.add( visitorQuestion );
        }

        daoUtil.free(  );

        return listVisitorQuestion;
    }

    /**
     * Get the max order of a parent theme
     * @param nIdParent The id of the parent theme
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
     * Find a theme with the parent id and the order.
     * @param nIdParent The parent Id
     * @param nOrder The order
     * @param plugin The {@link Plugin}
     * @return the {@link Theme}
     */
    public Theme findByOrder( int nIdParent, int nOrder, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ORDER_ID, plugin );
        daoUtil.setInt( 1, nIdParent );
        daoUtil.setInt( 2, nOrder );
        daoUtil.executeQuery(  );

        Theme theme = null;

        if ( daoUtil.next(  ) )
        {
            theme = new Theme(  );
            theme.setId( daoUtil.getInt( 1 ) );
            theme.setText( daoUtil.getString( 2 ) );
            theme.setIdMailingList( daoUtil.getInt( 3 ) );
            theme.setIdParent( nIdParent );
            theme.setIdOrder( nOrder );
        }

        daoUtil.free(  );

        return theme;
    }

    /**
     * Find a theme with the faq id and the order.
     * @param nIdFaq The faq Id
     * @param nOrder The order
     * @param plugin The {@link Plugin}
     * @return the {@link Theme}
     */
    public Theme findByFaqOrder( int nIdFaq, int nOrder, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_FAQ_ORDER_ID, plugin );
        daoUtil.setInt( 1, nIdFaq );
        daoUtil.setInt( 2, nOrder );
        daoUtil.executeQuery(  );

        Theme theme = null;

        if ( daoUtil.next(  ) )
        {
            theme = new Theme(  );
            theme.setId( daoUtil.getInt( 1 ) );
            theme.setText( daoUtil.getString( 2 ) );
            theme.setIdMailingList( daoUtil.getInt( 3 ) );
            theme.setIdParent( daoUtil.getInt( 4 ) );
            theme.setIdOrder( daoUtil.getInt( 5 ) );
        }

        daoUtil.free(  );

        return theme;
    }

    /**
     * Insert a new record in the table.
     *
     * @param nIdAbstractSubject The id of the object Theme
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
     * @param nIdAbstractSubject The id of the object Theme
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
     * @param nIdAbstractSubject The id of the object Theme
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
