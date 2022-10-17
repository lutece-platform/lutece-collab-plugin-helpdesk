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

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;
import java.util.Locale;

/**
 * This class provides instances management methods (create, find, ...) for Theme objects
 */
public final class ThemeHome extends AbstractSubjectHome
{
    // Static variable pointed at the DAO instance
    private static IThemeDAO _dao = (IThemeDAO) SpringContextService.getPluginBean( "helpdesk", "themeDAO" );
    private static final String ROOT_THEME_NAME = "helpdesk.subjects.rootSubjectName";
    private static final int ROOT_THEME_ID = 0;

    /* This class implements the Singleton design pattern. */
    private static ThemeHome _singleton;

    /**
     * Constructor
     */
    public ThemeHome( )
    {
        if ( _singleton == null )
        {
            _singleton = this;
        }
    }

    /**
     * Returns the instance of ThemeHome
     *
     * @return the ThemeHome instance
     */
    public static ThemeHome getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = new ThemeHome( );
        }

        return _singleton;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns Theme list
     *
     * @param plugin
     *            The current plugin using this method
     * @return the {@link ReferenceList} of the Theme of the database in form of {@link ReferenceList}
     */
    public static ReferenceList findAllReferenceList( Plugin plugin )
    {
        ReferenceList list = new ReferenceList( );

        for ( AbstractSubject abstractSubject : getInstance( ).findAll( plugin ) )
        {
            Theme theme = (Theme) abstractSubject;
            list.addItem( theme.getId( ), theme.getText( ) );
        }

        return list;
    }

    /**
     * Returns Question list for Theme
     * 
     * @param plugin
     *            The current plugin using this method
     * @param nIdTheme
     *            The Theme ID
     * @return the {@link Collection} of the {@link VisitorQuestion} of Theme
     */
    public static Collection<VisitorQuestion> findQuestion( int nIdTheme, Plugin plugin )
    {
        return _dao.findQuestions( nIdTheme, plugin );
    }

    /**
     * Create the virtual root {@link Theme} to initialize the tree
     * 
     * @param locale
     *            The locale
     * @return The virtual root {@link Theme}
     */
    public static Theme getVirtualRootTheme( Locale locale )
    {
        Theme rootTheme = new Theme( );
        rootTheme.setId( ROOT_THEME_ID );
        rootTheme.setText( I18nService.getLocalizedString( ROOT_THEME_NAME, locale ) );
        rootTheme.setIdParent( ROOT_THEME_ID );

        return rootTheme;
    }

    /**
     * Return the used DAO
     * 
     * @return the used DAO
     */
    public IAbstractSubjectDAO getDAO( )
    {
        return _dao;
    }
}
