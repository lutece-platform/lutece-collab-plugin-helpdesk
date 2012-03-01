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

import java.util.Collection;
import java.util.List;


/**
 *  This interface represents the business object of the {@link AbstractSubject}
 */
public interface IAbstractSubjectDAO
{
    /**
    * Calculate a new primary key to add a new AbstractSubject
    * @param plugin The Plugin using this data access service
    * @return The new key.
    */
    int newPrimaryKey( Plugin plugin );

    /**
     * Insert a new record in the table.
     * @param abstractSubject The Instance of the object AbstractSubject
     * @param plugin The Plugin using this data access service
     */
    void insert( AbstractSubject abstractSubject, Plugin plugin );

    /**
     * Delete a record from the table
     *
     * @param nId The indentifier of the object AbstractSubject
     * @param plugin The Plugin using this data access service
     */
    void delete( int nId, Plugin plugin );

    /**
     * load the data of AbstractSubject from the table
     *
     * @param nId The indentifier of the object AbstractSubject
     * @param plugin The Plugin using this data access service
     * @return The Instance of the object AbstractSubject
     */
    AbstractSubject load( int nId, Plugin plugin );

    /**
     * Update the record in the table
     * @param abstractSubject The instance of the AbstractSubject to update
     * @param plugin The Plugin using this data access service
     */
    void store( AbstractSubject abstractSubject, Plugin plugin );

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    List<?extends AbstractSubject> findAll( Plugin plugin );

    /**
     * Finds all {@link AbstractSubject} specified by the parent id
     * @param nIdParent The parent abstractSubject id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link AbstractSubject}
     */
    Collection<?extends AbstractSubject> findByIdParent( int nIdParent, Plugin plugin );

    /**
     * Get the max order of a parent abstractSubject
     * @param nIdParent The id of the parent abstractSubject
     * @param plugin The {@link Plugin}
     * @return the max order
     */
    int getMaxOrder( int nIdParent, Plugin plugin );

    /**
     * Find a abstractSubject with the parent id and the order.
     * @param nIdParent The parent Id
     * @param nOrder The order
     * @param plugin The {@link Plugin}
     * @return the {@link AbstractSubject}
     */
    AbstractSubject findByOrder( int nIdParent, int nOrder, Plugin plugin );

    /**
     * Find a abstractSubject with the faq id and the order.
     * @param nIdFaq The faq Id
     * @param nOrder The order
     * @param plugin The {@link Plugin}
     * @return the {@link AbstractSubject}
     */
    AbstractSubject findByFaqOrder( int nIdFaq, int nOrder, Plugin plugin );

    /**
     * Finds all {@link AbstractSubject} specified by the Faq id
     * @param nIdFaq The Faq abstractSubject id
     * @param plugin The Plugin using this data access service
     * @return A collection of {@link AbstractSubject}
     */
    Collection<?extends AbstractSubject> findByIdFaq( int nIdFaq, Plugin plugin );

    /**
     * Insert a new record in the table.
     *
     * @param nIdAbstractSubject The id of the object Subject
     * @param nIdFaq The parent id of the object Faq
     * @param plugin The Plugin using this data access service
     */
    void insertLinkToFaq( int nIdAbstractSubject, int nIdFaq, Plugin plugin );

    /**
     * delete a record in the table.
     *
     * @param nIdAbstractSubject The id of the object Subject
     * @param nIdFaq The parent id of the object Faq
     * @param plugin The Plugin using this data access service
     */
    void deleteLinkToFaq( int nIdAbstractSubject, int nIdFaq, Plugin plugin );

    /**
     * delete a record in the table.
     *
     * @param nIdAbstractSubject The id of the object Subject
     * @param plugin The Plugin using this data access service
     */
    void deleteAllLinksToFaq( int nIdAbstractSubject, Plugin plugin );
}
