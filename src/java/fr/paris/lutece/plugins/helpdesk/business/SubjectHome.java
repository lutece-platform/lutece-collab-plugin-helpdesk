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

import fr.paris.lutece.portal.service.plugin.Plugin;
import jakarta.enterprise.inject.spi.CDI;

import java.util.Collection;
import java.util.List;


/**
 * This class provides instances management methods (create, find, ...)
 * for Subject objects
 */
public final class SubjectHome extends AbstractSubjectHome
{
    // Static variable pointed at the DAO instance
    private static ISubjectDAO _dao = CDI.current( ).select( ISubjectDAO.class ).get( );

    /**
     * Private constructor
     */
    private SubjectHome( )
    {
    }

    /**
     * Creation of an instance of a {@link Subject}
     *
     * @param abstractSubject An instance of the {@link Subject} which contains the informations to store
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     * @return The instance of the {@link Subject} which has been created
     */
    public static AbstractSubject create( AbstractSubject abstractSubject, int nIdFaq, Plugin plugin )
    {
        return AbstractSubjectHome.create( _dao, abstractSubject, nIdFaq, plugin );
    }

    /**
     * Updates of the {@link Subject} instance specified in parameter
     *
     * @param abstractSubject An instance of the {@link Subject} which contains the informations to store
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     * @return The instance of the {@link Subject} which has been updated.
     */
    public static AbstractSubject update( AbstractSubject abstractSubject, int nIdFaq, Plugin plugin )
    {
        return AbstractSubjectHome.update( _dao, abstractSubject, nIdFaq, plugin );
    }

    /**
     * Deletes the {@link Subject} instance whose identifier is specified in parameter
     *
     * @param nIdAbstractSubject The identifier of the {@link Subject} to delete in the database
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     */
    public static void remove( int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        AbstractSubjectHome.remove( _dao, nIdAbstractSubject, nIdFaq, plugin );
    }

    /**
     * Returns an instance of the {@link Subject} whose identifier is specified in parameter
     *
     * @param nKey The primary key of the {@link Subject} to find in the database
     * @param plugin The current plugin using this method
     * @return An instance of the {@link Subject} which corresponds to the key
     */
    public static AbstractSubject findByPrimaryKey( int nKey, Plugin plugin )
    {
        return AbstractSubjectHome.findByPrimaryKey( _dao, nKey, plugin );
    }

    /**
     * Returns {@link Subject} list
     *
     * @param plugin The current plugin using this method
     * @return the list of the {@link Subject} of the database in form of a Subject Collection object
     */
    public static List<?extends AbstractSubject> findAll( Plugin plugin )
    {
        return AbstractSubjectHome.findAll( _dao, plugin );
    }

    /**
     * Finds all {@link Subject} specified by the parent id
     * @param nIdParent The parent {@link Subject} id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link Subject}
     */
    public static Collection<?extends AbstractSubject> findByIdParent( int nIdParent, Plugin plugin )
    {
        return AbstractSubjectHome.findByIdParent( _dao, nIdParent, plugin );
    }

    /**
     * Finds all {@link Subject} specified by the Faq id
     * @param nIdFaq The faq subject id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link Subject}
     */
    public static Collection<?extends AbstractSubject> findByIdFaq( int nIdFaq, Plugin plugin )
    {
        return AbstractSubjectHome.findByIdFaq( _dao, nIdFaq, plugin );
    }

    /**
     * Returns an instance of the {@link Subject} whose identifier is specified in parameter
     *
     * @param nIdParent The primary key of the parent {@link Subject}
     * @param nOrder The order id
     * @param plugin The current plugin using this method
     * @return An instance of the {@link Subject} which corresponds to the parent id and order id
     */
    public static AbstractSubject findByOrder( int nIdParent, int nOrder, Plugin plugin )
    {
        return AbstractSubjectHome.findByOrder( _dao, nIdParent, nOrder, plugin );
    }

    /**
     * Returns an instance of the {@link Subject} whose identifier is specified in parameter
     *
     * @param nIdFaq The primary key of the faq {@link Subject}
     * @param nOrder The order id
     * @param plugin The current plugin using this method
     * @return An instance of the {@link Subject} which corresponds to the faq id and order id
     */
    public static AbstractSubject findByFaqOrder( int nIdFaq, int nOrder, Plugin plugin )
    {
        return AbstractSubjectHome.findByFaqOrder( _dao, nIdFaq, nOrder, plugin );
    }

    /**
     * Get the max order of a parent {@link Subject}
     * @param nIdParent The id of the parent {@link Subject}
     * @param plugin The {@link Plugin}
     * @return the max order
     */
    public static int getMaxOrder( int nIdParent, Plugin plugin )
    {
        return AbstractSubjectHome.getMaxOrder( _dao, nIdParent, plugin );
    }

    /**
    * Move down a {@link Subject} into the list
    * @param nId The id of the {@link Subject}
    * @param nIdFaq The {@link Faq} Id
    * @param plugin The plugin
    */
    public static void goDown( int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubjectHome.goDown( _dao, nId, nIdFaq, plugin );
    }

    /**
     * Move up a {@link Subject} into the list
     * @param nId The id of the {@link Subject}
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The plugin
     */
    public static void goUp( int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubjectHome.goUp( _dao, nId, nIdFaq, plugin );
    }

    /**
     * Set the {@link Subject} into another parent {@link Subject}
     * @param nId The {@link Subject} to move
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The plugin
     */
    public static void goIn( int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubjectHome.goIn( _dao, nId, nIdFaq, plugin );
    }

    /**
     * Set the {@link Subject} out of another parent {@link Subject}
     * @param nId The {@link Subject} to move
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
     * @param nIdAbstractSubject The id of the object Subject
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
     * @param nIdAbstractSubject The id of the object Subject
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
     * @param nIdAbstractSubject The id of the object Subject
     * @param plugin The Plugin using this data access service
     */
    public static void removeAllLinksToFaq( int nIdAbstractSubject, Plugin plugin )
    {
        AbstractSubjectHome.removeAllLinksToFaq( _dao, nIdAbstractSubject, plugin );
    }

    /**
     * Returns Question list for Subject
     * @param plugin The current plugin using this method
     * @param nIdSubject The Subject ID
     * @return the list of the Question of subject
     */
    public static List<QuestionAnswer> findQuestion( int nIdSubject, Plugin plugin )
    {
        return _dao.findQuestions( nIdSubject, plugin );
    }

    /**
     * return the count of all announce for Field
     * @param plugin The current plugin using this method
     * @param nIdSubject The subject ID
     * @return count of announce for Field
     */
    public static int countQuestionSubject( int nIdSubject, Plugin plugin )
    {
        return _dao.countQuestion( nIdSubject, plugin );
    }
}
