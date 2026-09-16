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
package fr.paris.lutece.plugins.helpdesk.business;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import jakarta.enterprise.inject.spi.CDI;

import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * This class provides instances management methods (create, find, ...)
 * for Theme objects
 */
public final class ThemeHome extends AbstractSubjectHome
{
    // Static variable pointed at the DAO instance
    private static IThemeDAO _dao = CDI.current( ).select( IThemeDAO.class ).get( );
    private static final String ROOT_THEME_NAME = "helpdesk.subjects.rootSubjectName";
    private static final int ROOT_THEME_ID = 0;

    /**
     * Private constructor
     */
    private ThemeHome( )
    {
    }

    /**
     * Creation of an instance of a {@link Theme}
     *
     * @param abstractSubject An instance of the {@link Theme} which contains the informations to store
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     * @return The instance of the {@link Theme} which has been created
     */
    public static AbstractSubject create( AbstractSubject abstractSubject, int nIdFaq, Plugin plugin )
    {
        return AbstractSubjectHome.create( _dao, abstractSubject, nIdFaq, plugin );
    }

    /**
     * Updates of the {@link Theme} instance specified in parameter
     *
     * @param abstractSubject An instance of the {@link Theme} which contains the informations to store
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     * @return The instance of the {@link Theme} which has been updated.
     */
    public static AbstractSubject update( AbstractSubject abstractSubject, int nIdFaq, Plugin plugin )
    {
        return AbstractSubjectHome.update( _dao, abstractSubject, nIdFaq, plugin );
    }

    /**
     * Deletes the {@link Theme} instance whose identifier is specified in parameter
     *
     * @param nIdAbstractSubject The identifier of the {@link Theme} to delete in the database
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     */
    public static void remove( int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        AbstractSubjectHome.remove( _dao, nIdAbstractSubject, nIdFaq, plugin );
    }

    /**
     * Returns an instance of the {@link Theme} whose identifier is specified in parameter
     *
     * @param nKey The primary key of the {@link Theme} to find in the database
     * @param plugin The current plugin using this method
     * @return An instance of the {@link Theme} which corresponds to the key
     */
    public static AbstractSubject findByPrimaryKey( int nKey, Plugin plugin )
    {
        return AbstractSubjectHome.findByPrimaryKey( _dao, nKey, plugin );
    }

    /**
     * Returns {@link Theme} list
     *
     * @param plugin The current plugin using this method
     * @return the list of the {@link Theme} of the database in form of a Theme Collection object
     */
    public static List<?extends AbstractSubject> findAll( Plugin plugin )
    {
        return AbstractSubjectHome.findAll( _dao, plugin );
    }

    /**
     * Finds all {@link Theme} specified by the parent id
     * @param nIdParent The parent {@link Theme} id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link Theme}
     */
    public static Collection<?extends AbstractSubject> findByIdParent( int nIdParent, Plugin plugin )
    {
        return AbstractSubjectHome.findByIdParent( _dao, nIdParent, plugin );
    }

    /**
     * Finds all {@link Theme} specified by the Faq id
     * @param nIdFaq The faq theme id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link Theme}
     */
    public static Collection<?extends AbstractSubject> findByIdFaq( int nIdFaq, Plugin plugin )
    {
        return AbstractSubjectHome.findByIdFaq( _dao, nIdFaq, plugin );
    }

    /**
     * Returns an instance of the {@link Theme} whose identifier is specified in parameter
     *
     * @param nIdParent The primary key of the parent {@link Theme}
     * @param nOrder The order id
     * @param plugin The current plugin using this method
     * @return An instance of the {@link Theme} which corresponds to the parent id and order id
     */
    public static AbstractSubject findByOrder( int nIdParent, int nOrder, Plugin plugin )
    {
        return AbstractSubjectHome.findByOrder( _dao, nIdParent, nOrder, plugin );
    }

    /**
     * Returns an instance of the {@link Theme} whose identifier is specified in parameter
     *
     * @param nIdFaq The primary key of the faq {@link Theme}
     * @param nOrder The order id
     * @param plugin The current plugin using this method
     * @return An instance of the {@link Theme} which corresponds to the faq id and order id
     */
    public static AbstractSubject findByFaqOrder( int nIdFaq, int nOrder, Plugin plugin )
    {
        return AbstractSubjectHome.findByFaqOrder( _dao, nIdFaq, nOrder, plugin );
    }

    /**
     * Get the max order of a parent {@link Theme}
     * @param nIdParent The id of the parent {@link Theme}
     * @param plugin The {@link Plugin}
     * @return the max order
     */
    public static int getMaxOrder( int nIdParent, Plugin plugin )
    {
        return AbstractSubjectHome.getMaxOrder( _dao, nIdParent, plugin );
    }

    /**
    * Move down a {@link Theme} into the list
    * @param nId The id of the {@link Theme}
    * @param nIdFaq The {@link Faq} Id
    * @param plugin The plugin
    */
    public static void goDown( int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubjectHome.goDown( _dao, nId, nIdFaq, plugin );
    }

    /**
     * Move up a {@link Theme} into the list
     * @param nId The id of the {@link Theme}
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The plugin
     */
    public static void goUp( int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubjectHome.goUp( _dao, nId, nIdFaq, plugin );
    }

    /**
     * Set the {@link Theme} into another parent {@link Theme}
     * @param nId The {@link Theme} to move
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The plugin
     */
    public static void goIn( int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubjectHome.goIn( _dao, nId, nIdFaq, plugin );
    }

    /**
     * Set the {@link Theme} out of another parent {@link Theme}
     * @param nId The {@link Theme} to move
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The plugin
     */
    public static void goOut( int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubjectHome.goOut( _dao, nId, nIdFaq, plugin );
    }

    /**
     * Create a new record in the table.
     *
     * @param nIdAbstractSubject The id of the object Theme
     * @param nIdFaq The parent id of the object Faq
     * @param plugin The Plugin using this data access service
     */
    public static void createLinkToFaq( int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        AbstractSubjectHome.createLinkToFaq( _dao, nIdAbstractSubject, nIdFaq, plugin );
    }

    /**
     * Remove a record in the table.
     *
     * @param nIdAbstractSubject The id of the object Theme
     * @param nIdFaq The parent id of the object Faq
     * @param plugin The Plugin using this data access service
     */
    public static void removeLinkToFaq( int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        AbstractSubjectHome.removeLinkToFaq( _dao, nIdAbstractSubject, nIdFaq, plugin );
    }

    /**
     * Remove a record in the table.
     *
     * @param nIdAbstractSubject The id of the object Theme
     * @param plugin The Plugin using this data access service
     */
    public static void removeAllLinksToFaq( int nIdAbstractSubject, Plugin plugin )
    {
        AbstractSubjectHome.removeAllLinksToFaq( _dao, nIdAbstractSubject, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns Theme list
     *
     * @param plugin The current plugin using this method
     * @return the {@link ReferenceList} of the Theme of the database in form of {@link ReferenceList}
     */
    public static ReferenceList findAllReferenceList( Plugin plugin )
    {
        ReferenceList list = new ReferenceList(  );

        for ( AbstractSubject abstractSubject : findAll( plugin ) )
        {
            Theme theme = (Theme) abstractSubject;
            list.addItem( theme.getId(  ), theme.getText(  ) );
        }

        return list;
    }

    /**
     * Returns Question list for Theme
     * @param plugin The current plugin using this method
     * @param nIdTheme The Theme ID
     * @return the {@link Collection} of the {@link VisitorQuestion} of Theme
     */
    public static Collection<VisitorQuestion> findQuestion( int nIdTheme, Plugin plugin )
    {
        return _dao.findQuestions( nIdTheme, plugin );
    }

    /**
     * Counts the pending visitor questions of a Theme
     * @param nIdTheme The Theme ID
     * @param plugin The current plugin using this method
     * @return The number of pending questions
     */
    public static int countQuestionTheme( int nIdTheme, Plugin plugin )
    {
        return _dao.countQuestion( nIdTheme, plugin );
    }

    /**
     * Create the virtual root {@link Theme} to initialize the tree
     * @param locale The locale
     * @return The virtual root {@link Theme}
     */
    public static Theme getVirtualRootTheme( Locale locale )
    {
        Theme rootTheme = new Theme(  );
        rootTheme.setId( ROOT_THEME_ID );
        rootTheme.setText( I18nService.getLocalizedString( ROOT_THEME_NAME, locale ) );
        rootTheme.setIdParent( ROOT_THEME_ID );

        return rootTheme;
    }
}
