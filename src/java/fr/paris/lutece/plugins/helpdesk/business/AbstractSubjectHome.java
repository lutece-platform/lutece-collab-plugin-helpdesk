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

import java.util.Collection;
import java.util.List;

import fr.paris.lutece.plugins.helpdesk.service.search.HelpdeskIndexer;
import fr.paris.lutece.plugins.helpdesk.utils.HelpdeskIndexerUtils;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * This class provides instances management methods (create, find, ...)
 * for AbstractSubject objects
 */
public abstract class AbstractSubjectHome
{
    public static final int FIRST_ORDER = 0;
    protected static final int STEP = 1;

    /**
     * Creation of an instance of an article {@link AbstractSubject}
     *
     * @param dao The DAO to use
     * @param abstractSubject An instance of the {@link AbstractSubject} which contains the informations to store
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     * @return The instance of the {@link AbstractSubject} which has been created
     */
    protected static AbstractSubject create( IAbstractSubjectDAO dao, AbstractSubject abstractSubject, int nIdFaq, Plugin plugin )
    {
        //Move down all orders in new list
        for ( AbstractSubject subjectChangeOrder : ( abstractSubject.getIdParent(  ) == 0 )
            ? findByIdFaq( dao, nIdFaq, plugin ) : findByIdParent( dao, abstractSubject.getIdParent(  ), plugin ) )
        {
            if ( subjectChangeOrder.getIdOrder(  ) >= abstractSubject.getIdOrder(  ) )
            {
                subjectChangeOrder.setIdOrder( subjectChangeOrder.getIdOrder(  ) + STEP );
                dao.store( subjectChangeOrder, plugin );
            }
        }

        dao.insert( abstractSubject, plugin );

        //index the subject
        IndexationService.addIndexerAction( Integer.toString( abstractSubject.getId(  ) ),
            AppPropertiesService.getProperty( HelpdeskIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_CREATE );
        HelpdeskIndexerUtils.addIndexerAction( Integer.toString( abstractSubject.getId(  ) ), IndexerAction.TASK_CREATE, HelpdeskIndexerUtils.CONSTANT_SUBJECT_TYPE_RESOURCE );

        return abstractSubject;
    }

    /**
     * Updates of the {@link AbstractSubject} instance specified in parameter
     *
     * @param dao The DAO to use
     * @param abstractSubject An instance of the {@link AbstractSubject} which contains the informations to store
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     * @return The instance of the {@link AbstractSubject} which has been updated.
     */
    protected static AbstractSubject update( IAbstractSubjectDAO dao, AbstractSubject abstractSubject, int nIdFaq, Plugin plugin )
    {
        if ( abstractSubject == null )
        {
            return null;
        }

        AbstractSubject subjectOld = findByPrimaryKey( dao, abstractSubject.getId(  ), plugin );

        if ( subjectOld == null )
        {
            return null;
        }

        //Move up all orders in old list
        for ( AbstractSubject subjectChangeOrder : ( subjectOld.getIdParent(  ) == 0 ) ? findByIdFaq( dao, nIdFaq, plugin )
                                                                                       : findByIdParent( dao,
                subjectOld.getIdParent(  ), plugin ) )
        {
            if ( subjectChangeOrder.getIdOrder(  ) > subjectOld.getIdOrder(  ) )
            {
                subjectChangeOrder.setIdOrder( subjectChangeOrder.getIdOrder(  ) - STEP );
                dao.store( subjectChangeOrder, plugin );
            }
        }

        //Move down all orders in new list
        for ( AbstractSubject subjectChangeOrder : ( abstractSubject.getIdParent(  ) == 0 )
            ? findByIdFaq( dao, nIdFaq, plugin ) : findByIdParent( dao, abstractSubject.getIdParent(  ), plugin ) )
        {
            if ( subjectChangeOrder.getIdOrder(  ) >= abstractSubject.getIdOrder(  ) )
            {
                subjectChangeOrder.setIdOrder( subjectChangeOrder.getIdOrder(  ) + STEP );
                dao.store( subjectChangeOrder, plugin );
            }
        }

        //Update abstract subject
        dao.store( abstractSubject, plugin );

        //reindex the subject
        IndexationService.addIndexerAction( Integer.toString( abstractSubject.getId(  ) ),
            AppPropertiesService.getProperty( HelpdeskIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_MODIFY );
        HelpdeskIndexerUtils.addIndexerAction( Integer.toString( abstractSubject.getId(  ) ), IndexerAction.TASK_MODIFY, HelpdeskIndexerUtils.CONSTANT_SUBJECT_TYPE_RESOURCE );

        return abstractSubject;
    }

    /**
     * Deletes the {@link AbstractSubject} instance whose identifier is specified in parameter
     *
     * @param dao The DAO to use
     * @param nIdAbstractSubject The identifier of the article {@link AbstractSubject} to delete in the database
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     */
    protected static void remove( IAbstractSubjectDAO dao, int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        AbstractSubject subjectOld = findByPrimaryKey( dao, nIdAbstractSubject, plugin );

        //Move up all orders in old list
        for ( AbstractSubject subjectChangeOrder : ( subjectOld.getIdParent(  ) == 0 ) ? findByIdFaq( dao, nIdFaq, plugin )
                                                                                       : findByIdParent( dao,
                subjectOld.getIdParent(  ), plugin ) )
        {
            if ( subjectChangeOrder.getIdOrder(  ) > subjectOld.getIdOrder(  ) )
            {
                subjectChangeOrder.setIdOrder( subjectChangeOrder.getIdOrder(  ) - STEP );
                dao.store( subjectChangeOrder, plugin );
            }
        }

        dao.delete( nIdAbstractSubject, plugin );
        //unindex the subject
        String strIdAbstractSubject = Integer.toString( nIdAbstractSubject );
        IndexationService.addIndexerAction( strIdAbstractSubject + "_" +
            HelpdeskIndexer.SHORT_NAME_SUBJECT,
            AppPropertiesService.getProperty( HelpdeskIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );
        HelpdeskIndexerUtils.addIndexerAction( strIdAbstractSubject, IndexerAction.TASK_DELETE, HelpdeskIndexerUtils.CONSTANT_SUBJECT_TYPE_RESOURCE );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of the {@link AbstractSubject} whose identifier is specified in parameter
     *
     * @param dao The DAO to use
     * @param nKey The primary key of the {@link AbstractSubject} to find in the database
     * @param plugin The current plugin using this method
     * @return An instance of the {@link AbstractSubject} which corresponds to the key
     */
    protected static AbstractSubject findByPrimaryKey( IAbstractSubjectDAO dao, int nKey, Plugin plugin )
    {
        return dao.load( nKey, plugin );
    }

    /**
     * Returns {@link AbstractSubject} list
     *
     * @param dao The DAO to use
     * @param plugin The current plugin using this method
     * @return the list of the {@link AbstractSubject} of the database in form of a Subject Collection object
     */
    protected static List<?extends AbstractSubject> findAll( IAbstractSubjectDAO dao, Plugin plugin )
    {
        return dao.findAll( plugin );
    }

    /**
     * Finds all {@link AbstractSubject} specified by the parent id
     * @param dao The DAO to use
     * @param nIdParent The parent {@link AbstractSubject} id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link AbstractSubject}
     */
    protected static Collection<?extends AbstractSubject> findByIdParent( IAbstractSubjectDAO dao, int nIdParent, Plugin plugin )
    {
        return dao.findByIdParent( nIdParent, plugin );
    }

    /**
     * Finds all {@link Subject} specified by the Faq id
     * @param dao The DAO to use
     * @param nIdFaq The faq subject id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link Subject}
     */
    protected static Collection<?extends AbstractSubject> findByIdFaq( IAbstractSubjectDAO dao, int nIdFaq, Plugin plugin )
    {
        return dao.findByIdFaq( nIdFaq, plugin );
    }

    /**
     * Returns an instance of the {@link AbstractSubject} whose identifier is specified in parameter
     *
     * @param dao The DAO to use
     * @param nIdParent The primary key of the parent {@link AbstractSubject}
     * @param nOrder The order id
     * @param plugin The current plugin using this method
     * @return An instance of the {@link AbstractSubject} which corresponds to the parent id and order id
     */
    protected static AbstractSubject findByOrder( IAbstractSubjectDAO dao, int nIdParent, int nOrder, Plugin plugin )
    {
        return dao.findByOrder( nIdParent, nOrder, plugin );
    }

    /**
     * Returns an instance of the {@link AbstractSubject} whose identifier is specified in parameter
     *
     * @param dao The DAO to use
     * @param nIdFaq The primary key of the faq {@link AbstractSubject}
     * @param nOrder The order id
     * @param plugin The current plugin using this method
     * @return An instance of the {@link AbstractSubject} which corresponds to the faq id and order id
     */
    protected static AbstractSubject findByFaqOrder( IAbstractSubjectDAO dao, int nIdFaq, int nOrder, Plugin plugin )
    {
        return dao.findByFaqOrder( nIdFaq, nOrder, plugin );
    }

    /**
     * Get the max order of a parent {@link AbstractSubject}
     * @param dao The DAO to use
     * @param nIdParent The id of the parent {@link AbstractSubject}
     * @param plugin The {@link Plugin}
     * @return the max order
     */
    protected static int getMaxOrder( IAbstractSubjectDAO dao, int nIdParent, Plugin plugin )
    {
        return dao.getMaxOrder( nIdParent, plugin );
    }

    /**
    * Move down an {@link AbstractSubject} into the list
    * @param dao The DAO to use
    * @param nId The id of the {@link AbstractSubject}
    * @param nIdFaq The {@link Faq} Id
    * @param plugin The plugin
    */
    protected static void goDown( IAbstractSubjectDAO dao, int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubject abstractSubjectDown = findByPrimaryKey( dao, nId, plugin );

        if ( abstractSubjectDown == null )
        {
            return;
        }

        int nMaxOrder = getMaxOrder( dao, abstractSubjectDown.getIdParent(  ), plugin );

        if ( abstractSubjectDown.getIdOrder(  ) >= nMaxOrder )
        {
            return;
        }

        abstractSubjectDown.setIdOrder( abstractSubjectDown.getIdOrder(  ) + STEP );

        //Commit
        update( dao, abstractSubjectDown, nIdFaq, plugin );
    }

    /**
     * Move up an {@link AbstractSubject} into the list
     * @param dao The DAO to use
     * @param nId The id of the {@link AbstractSubject}
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The plugin
     */
    protected static void goUp( IAbstractSubjectDAO dao, int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubject abstractSubjectUp = findByPrimaryKey( dao, nId, plugin );

        if ( ( abstractSubjectUp == null ) || ( abstractSubjectUp.getIdOrder(  ) <= FIRST_ORDER ) )
        {
            return;
        }

        abstractSubjectUp.setIdOrder( abstractSubjectUp.getIdOrder(  ) - STEP );

        //Commit
        update( dao, abstractSubjectUp, nIdFaq, plugin );
    }

    /**
     * Set the {@link AbstractSubject} into another parent {@link AbstractSubject}
     * @param dao The DAO to use
     * @param nId The {@link AbstractSubject} to move
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The plugin
     */
    protected static void goIn( IAbstractSubjectDAO dao, int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubject abstractSubjectIn = findByPrimaryKey( dao, nId, plugin );

        if ( abstractSubjectIn == null )
        {
            return;
        }

        AbstractSubject abstractSubjectParent = ( abstractSubjectIn.getIdParent(  ) == 0 )
            ? findByFaqOrder( dao, nIdFaq, abstractSubjectIn.getIdOrder(  ) + STEP, plugin )
            : findByOrder( dao, abstractSubjectIn.getIdParent(  ), abstractSubjectIn.getIdOrder(  ) + STEP, plugin );

        if ( ( abstractSubjectParent == null ) )
        {
            return;
        }

        abstractSubjectIn.setIdOrder( FIRST_ORDER );
        abstractSubjectIn.setIdParent( abstractSubjectParent.getId(  ) );
        update( dao, abstractSubjectIn, nIdFaq, plugin );
    }

    /**
     * Set the {@link AbstractSubject} out of another parent {@link AbstractSubject}
     * @param dao The DAO to use
     * @param nId The {@link AbstractSubject} to move
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The plugin
     */
    protected static void goOut( IAbstractSubjectDAO dao, int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubject abstractSubjectOut = findByPrimaryKey( dao, nId, plugin );

        if ( abstractSubjectOut == null )
        {
            return;
        }

        AbstractSubject abstractSubjectParent = findByPrimaryKey( dao, abstractSubjectOut.getIdParent(  ), plugin );

        if ( ( abstractSubjectParent == null ) )
        {
            return;
        }

        abstractSubjectOut.setIdOrder( abstractSubjectParent.getIdOrder(  ) );
        abstractSubjectOut.setIdParent( abstractSubjectParent.getIdParent(  ) );
        update( dao, abstractSubjectOut, nIdFaq, plugin );
    }

    /**
     * Create a new record in the table.
     *
     * @param dao The DAO to use
     * @param nIdAbstractSubject The id of the object Subject
     * @param nIdFaq The parent id of the object Faq
     * @param plugin The Plugin using this data access service
     */
    protected static void createLinkToFaq( IAbstractSubjectDAO dao, int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        dao.insertLinkToFaq( nIdAbstractSubject, nIdFaq, plugin );
    }

    /**
     * Remove a record in the table.
     *
     * @param dao The DAO to use
     * @param nIdAbstractSubject The id of the object Subject
     * @param nIdFaq The parent id of the object Faq
     * @param plugin The Plugin using this data access service
     */
    protected static void removeLinkToFaq( IAbstractSubjectDAO dao, int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        dao.deleteLinkToFaq( nIdAbstractSubject, nIdFaq, plugin );
    }

    /**
     * Remove a record in the table.
     *
     * @param dao The DAO to use
     * @param nIdAbstractSubject The id of the object Subject
     * @param plugin The Plugin using this data access service
     */
    protected static void removeAllLinksToFaq( IAbstractSubjectDAO dao, int nIdAbstractSubject, Plugin plugin )
    {
        dao.deleteAllLinksToFaq( nIdAbstractSubject, plugin );
    }
}
