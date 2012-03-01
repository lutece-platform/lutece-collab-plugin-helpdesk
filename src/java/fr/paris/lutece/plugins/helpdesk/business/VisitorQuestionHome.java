/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;


/**
 * This class provides instances management methods (create, find, ...)
 * for VisitorQuestion objects
 */
public final class VisitorQuestionHome
{
    // Static variable pointed at the DAO instance
    private static IVisitorQuestionDAO _dao = (IVisitorQuestionDAO) SpringContextService.getPluginBean( "helpdesk",
            "visitorQuestionDAO" );

    /**
     * Private constructor - this class need not be instantiated.
     */
    private VisitorQuestionHome(  )
    {
    }

    /**
     * Creation of an instance of an article VisitorQuestion
     *
     * @param visitorQuestion An instance of the VisitorQuestion which contains the informations to store
     * @param plugin The current plugin using this method
     * @return The instance of the VisitorQuestion which has been created
     */
    public static VisitorQuestion create( VisitorQuestion visitorQuestion, Plugin plugin )
    {
        _dao.insert( visitorQuestion, plugin );

        return visitorQuestion;
    }

    /**
     * Updates of the VisitorQuestion instance specified in parameter
     *
     * @param visitorQuestion An instance of the VisitorQuestion which contains the informations to store
     * @param plugin The current plugin using this method
     * @return The instance of the VisitorQuestion which has been updated.
     */
    public static VisitorQuestion update( VisitorQuestion visitorQuestion, Plugin plugin )
    {
        _dao.store( visitorQuestion, plugin );

        return visitorQuestion;
    }

    /**
     * Deletes the VisitorQuestion instance whose identifier is specified in parameter
     *
     * @param nIdVisitorQuestion The identifier of the article VisitorQuestion to delete in the database
     * @param plugin The current plugin using this method
     */
    public static void remove( int nIdVisitorQuestion, Plugin plugin )
    {
        _dao.delete( nIdVisitorQuestion, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of the article VisitorQuestion whose identifier is specified in parameter
     *
     * @param nKey The primary key of the article to find in the database
     * @param plugin The current plugin using this method
     * @return An instance of the VisitorQuestion which corresponds to the key
     */
    public static VisitorQuestion findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Returns VisitorQuestion list
     *
     * @param plugin The current plugin using this method
     * @return the list of the VisitorQuestion of the database in form of a VisitorQuestion Collection object
     */
    public static Collection<VisitorQuestion> findAll( Plugin plugin )
    {
        return _dao.findAll( plugin );
    }

    /**
     * Returns VisitorQuestion list
     * @param nIdUser The User ID
     * @param plugin The current plugin using this method
     * @return the list of the VisitorQuestion of the database in form of a VisitorQuestion Collection object
     */
    public static Collection<VisitorQuestion> findByUser( int nIdUser, Plugin plugin )
    {
        return _dao.findByUser( nIdUser, plugin );
    }

    /**
     * Returns VisitorQuestion list
     * @param nIdTheme The Theme ID
     * @param plugin The current plugin using this method
     * @return the list of the VisitorQuestion of the database in form of a VisitorQuestion Collection object
     */
    public static Collection<VisitorQuestion> findByTheme( int nIdTheme, Plugin plugin )
    {
        return _dao.findByTheme( nIdTheme, plugin );
    }

    /**
     * Returns VisitorQuestion list
     * @param nIdTheme The Theme ID
     * @param plugin The current plugin using this method
     * @return the list of the VisitorQuestion of the database in form of a VisitorQuestion Collection object
     */
    public static Collection<VisitorQuestion> findArchivedQuestionsByTheme( int nIdTheme, Plugin plugin )
    {
        return _dao.findArchivedQuestionsByTheme( nIdTheme, plugin );
    }
}
