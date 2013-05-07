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
public abstract class AbstractSubjectHome implements AbstractSubjectHomeInterface
{
    public static final int FIRST_ORDER = 0;
    protected static final int STEP = 1;

    /**
     * Creation of an instance of an article {@link AbstractSubject}
     *
     * @param abstractSubject An instance of the {@link AbstractSubject} which contains the informations to store
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     * @return The instance of the {@link AbstractSubject} which has been created
     */
    public AbstractSubject create( AbstractSubject abstractSubject, int nIdFaq, Plugin plugin )
    {
        //Move down all orders in new list
        for ( AbstractSubject subjectChangeOrder : ( abstractSubject.getIdParent(  ) == 0 )
            ? findByIdFaq( nIdFaq, plugin ) : findByIdParent( abstractSubject.getIdParent(  ), plugin ) )
        {
            if ( subjectChangeOrder.getIdOrder(  ) >= abstractSubject.getIdOrder(  ) )
            {
                subjectChangeOrder.setIdOrder( subjectChangeOrder.getIdOrder(  ) + STEP );
                getDAO(  ).store( subjectChangeOrder, plugin );
            }
        }

        getDAO(  ).insert( abstractSubject, plugin );

        //index the subject
        IndexationService.addIndexerAction( Integer.toString( abstractSubject.getId(  ) ),
            AppPropertiesService.getProperty( HelpdeskIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_CREATE );
        HelpdeskIndexerUtils.addIndexerAction( Integer.toString( abstractSubject.getId(  ) ), IndexerAction.TASK_CREATE, HelpdeskIndexerUtils.CONSTANT_SUBJECT_TYPE_RESOURCE );

        return abstractSubject;
    }

    /**
     * Updates of the {@link AbstractSubject} instance specified in parameter
     *
     * @param abstractSubject An instance of the {@link AbstractSubject} which contains the informations to store
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     * @return The instance of the {@link AbstractSubject} which has been updated.
     */
    public AbstractSubject update( AbstractSubject abstractSubject, int nIdFaq, Plugin plugin )
    {
        if ( abstractSubject == null )
        {
            return null;
        }

        AbstractSubject subjectOld = findByPrimaryKey( abstractSubject.getId(  ), plugin );

        if ( subjectOld == null )
        {
            return null;
        }

        //Move up all orders in old list
        for ( AbstractSubject subjectChangeOrder : ( subjectOld.getIdParent(  ) == 0 ) ? findByIdFaq( nIdFaq, plugin )
                                                                                       : findByIdParent( 
                subjectOld.getIdParent(  ), plugin ) )
        {
            if ( subjectChangeOrder.getIdOrder(  ) > subjectOld.getIdOrder(  ) )
            {
                subjectChangeOrder.setIdOrder( subjectChangeOrder.getIdOrder(  ) - STEP );
                getDAO(  ).store( subjectChangeOrder, plugin );
            }
        }

        //Move down all orders in new list
        for ( AbstractSubject subjectChangeOrder : ( abstractSubject.getIdParent(  ) == 0 )
            ? findByIdFaq( nIdFaq, plugin ) : findByIdParent( abstractSubject.getIdParent(  ), plugin ) )
        {
            if ( subjectChangeOrder.getIdOrder(  ) >= abstractSubject.getIdOrder(  ) )
            {
                subjectChangeOrder.setIdOrder( subjectChangeOrder.getIdOrder(  ) + STEP );
                getDAO(  ).store( subjectChangeOrder, plugin );
            }
        }

        //Update abstract subject
        getDAO(  ).store( abstractSubject, plugin );

        //reindex the subject
        IndexationService.addIndexerAction( Integer.toString( abstractSubject.getId(  ) ),
            AppPropertiesService.getProperty( HelpdeskIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_MODIFY );
        HelpdeskIndexerUtils.addIndexerAction( Integer.toString( abstractSubject.getId(  ) ), IndexerAction.TASK_MODIFY, HelpdeskIndexerUtils.CONSTANT_SUBJECT_TYPE_RESOURCE );
        
        return abstractSubject;
    }

    /**
     * Deletes the {@link AbstractSubject} instance whose identifier is specified in parameter
     *
     * @param nIdAbstractSubject The identifier of the article {@link AbstractSubject} to delete in the database
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The current plugin using this method
     */
    public void remove( int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        AbstractSubject subjectOld = findByPrimaryKey( nIdAbstractSubject, plugin );

        //Move up all orders in old list
        for ( AbstractSubject subjectChangeOrder : ( subjectOld.getIdParent(  ) == 0 ) ? findByIdFaq( nIdFaq, plugin )
                                                                                       : findByIdParent( 
                subjectOld.getIdParent(  ), plugin ) )
        {
            if ( subjectChangeOrder.getIdOrder(  ) > subjectOld.getIdOrder(  ) )
            {
                subjectChangeOrder.setIdOrder( subjectChangeOrder.getIdOrder(  ) - STEP );
                getDAO(  ).store( subjectChangeOrder, plugin );
            }
        }

        getDAO(  ).delete( nIdAbstractSubject, plugin );
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
     * @param nKey The primary key of the {@link AbstractSubject} to find in the database
     * @param plugin The current plugin using this method
     * @return An instance of the {@link AbstractSubject} which corresponds to the key
     */
    public AbstractSubject findByPrimaryKey( int nKey, Plugin plugin )
    {
        return getDAO(  ).load( nKey, plugin );
    }

    /**
     * Returns {@link AbstractSubject} list
     *
     * @param plugin The current plugin using this method
     * @return the list of the {@link AbstractSubject} of the database in form of a Subject Collection object
     */
    public List<?extends AbstractSubject> findAll( Plugin plugin )
    {
        return getDAO(  ).findAll( plugin );
    }

    /**
     * Finds all {@link AbstractSubject} specified by the parent id
     * @param nIdParent The parent {@link AbstractSubject} id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link AbstractSubject}
     */
    public Collection<?extends AbstractSubject> findByIdParent( int nIdParent, Plugin plugin )
    {
        return getDAO(  ).findByIdParent( nIdParent, plugin );
    }

    /**
     * Finds all {@link Subject} specified by the Faq id
     * @param nIdFaq The faq subject id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link Subject}
     */
    public Collection<?extends AbstractSubject> findByIdFaq( int nIdFaq, Plugin plugin )
    {
        return getDAO(  ).findByIdFaq( nIdFaq, plugin );
    }

    /**
     * Returns an instance of the {@link AbstractSubject} whose identifier is specified in parameter
     *
     * @param nIdParent The primary key of the parent {@link AbstractSubject}
     * @param nOrder The order id
     * @param plugin The current plugin using this method
     * @return An instance of the {@link AbstractSubject} which corresponds to the parent id and order id
     */
    public AbstractSubject findByOrder( int nIdParent, int nOrder, Plugin plugin )
    {
        return getDAO(  ).findByOrder( nIdParent, nOrder, plugin );
    }

    /**
     * Returns an instance of the {@link AbstractSubject} whose identifier is specified in parameter
     *
     * @param nIdFaq The primary key of the faq {@link AbstractSubject}
     * @param nOrder The order id
     * @param plugin The current plugin using this method
     * @return An instance of the {@link AbstractSubject} which corresponds to the faq id and order id
     */
    public AbstractSubject findByFaqOrder( int nIdFaq, int nOrder, Plugin plugin )
    {
        return getDAO(  ).findByFaqOrder( nIdFaq, nOrder, plugin );
    }

    /**
     * Get the max order of a parent {@link AbstractSubject}
     * @param nIdParent The id of the parent {@link AbstractSubject}
     * @param plugin The {@link Plugin}
     * @return the max order
     */
    public int getMaxOrder( int nIdParent, Plugin plugin )
    {
        return getDAO(  ).getMaxOrder( nIdParent, plugin );
    }

    /**
    * Move down an {@link AbstractSubject} into the list
    * @param nId The id of the {@link AbstractSubject}
    * @param nIdFaq The {@link Faq} Id
    * @param plugin The plugin
    */
    public void goDown( int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubject abstractSubjectDown = findByPrimaryKey( nId, plugin );

        if ( abstractSubjectDown == null )
        {
            return;
        }

        int nMaxOrder = getMaxOrder( abstractSubjectDown.getIdParent(  ), plugin );

        if ( abstractSubjectDown.getIdOrder(  ) >= nMaxOrder )
        {
            return;
        }

        abstractSubjectDown.setIdOrder( abstractSubjectDown.getIdOrder(  ) + STEP );

        //Commit
        update( abstractSubjectDown, nIdFaq, plugin );
    }

    /**
     * Move up an {@link AbstractSubject} into the list
     * @param nId The id of the {@link AbstractSubject}
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The plugin
     */
    public void goUp( int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubject abstractSubjectUp = findByPrimaryKey( nId, plugin );

        if ( ( abstractSubjectUp == null ) || ( abstractSubjectUp.getIdOrder(  ) <= FIRST_ORDER ) )
        {
            return;
        }

        abstractSubjectUp.setIdOrder( abstractSubjectUp.getIdOrder(  ) - STEP );

        //Commit
        update( abstractSubjectUp, nIdFaq, plugin );
    }

    /**
     * Set the {@link AbstractSubject} into another parent {@link AbstractSubject}
     * @param nId The {@link AbstractSubject} to move
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The plugin
     */
    public void goIn( int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubject abstractSubjectIn = findByPrimaryKey( nId, plugin );

        if ( abstractSubjectIn == null )
        {
            return;
        }

        AbstractSubject abstractSubjectParent = ( abstractSubjectIn.getIdParent(  ) == 0 )
            ? findByFaqOrder( nIdFaq, abstractSubjectIn.getIdOrder(  ) + STEP, plugin )
            : findByOrder( abstractSubjectIn.getIdParent(  ), abstractSubjectIn.getIdOrder(  ) + STEP, plugin );

        if ( ( abstractSubjectParent == null ) )
        {
            return;
        }

        abstractSubjectIn.setIdOrder( FIRST_ORDER );
        abstractSubjectIn.setIdParent( abstractSubjectParent.getId(  ) );
        update( abstractSubjectIn, nIdFaq, plugin );
    }

    /**
     * Set the {@link AbstractSubject} out of another parent {@link AbstractSubject}
     * @param nId The {@link AbstractSubject} to move
     * @param nIdFaq The {@link Faq} Id
     * @param plugin The plugin
     */
    public void goOut( int nId, int nIdFaq, Plugin plugin )
    {
        AbstractSubject abstractSubjectOut = findByPrimaryKey( nId, plugin );

        if ( abstractSubjectOut == null )
        {
            return;
        }

        AbstractSubject abstractSubjectParent = findByPrimaryKey( abstractSubjectOut.getIdParent(  ), plugin );

        if ( ( abstractSubjectParent == null ) )
        {
            return;
        }

        abstractSubjectOut.setIdOrder( abstractSubjectParent.getIdOrder(  ) );
        abstractSubjectOut.setIdParent( abstractSubjectParent.getIdParent(  ) );
        update( abstractSubjectOut, nIdFaq, plugin );
    }

    /**
     * Create a new record in the table.
     *
     * @param nIdAbstractSubject The id of the object Subject
     * @param nIdFaq The parent id of the object Faq
     * @param plugin The Plugin using this data access service
     */
    public void createLinkToFaq( int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        getDAO(  ).insertLinkToFaq( nIdAbstractSubject, nIdFaq, plugin );
    }

    /**
     * Remove a record in the table.
     *
     * @param nIdAbstractSubject The id of the object Subject
     * @param nIdFaq The parent id of the object Faq
     * @param plugin The Plugin using this data access service
     */
    public void removeLinkToFaq( int nIdAbstractSubject, int nIdFaq, Plugin plugin )
    {
        getDAO(  ).deleteLinkToFaq( nIdAbstractSubject, nIdFaq, plugin );
    }

    /**
     * Remove a record in the table.
     *
     * @param nIdAbstractSubject The id of the object Subject
     * @param plugin The Plugin using this data access service
     */
    public void removeAllLinksToFaq( int nIdAbstractSubject, Plugin plugin )
    {
        getDAO(  ).deleteAllLinksToFaq( nIdAbstractSubject, plugin );
    }
}
