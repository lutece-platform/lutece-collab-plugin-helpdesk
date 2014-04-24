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
import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;


/**
 * Interface for Faq DAO
 */
public interface IFaqDAO
{
    /**
        * Calculate a new primary key to add a new Faq
        * @param plugin The Plugin using this data access service
        * @return The new key.
        */
    int newPrimaryKey( Plugin plugin );

    /**
     * Insert a new record in the table.
     * @param faq The Instance of the object Faq
     * @param plugin The Plugin using this data access service
     */
    void insert( Faq faq, Plugin plugin );

    /**
     * Delete a record from the table
     *
     * @param nId The indentifier of the object Faq
     * @param plugin The Plugin using this data access service
     */
    void delete( int nId, Plugin plugin );

    /**
     * load the data of Faq from the table
     *
     * @param nId The indentifier of the object Faq
     * @param plugin The Plugin using this data access service
     * @return The Instance of the object Faq
     */
    Faq load( int nId, Plugin plugin );

    /**
     * Update the record in the table
     * @param faq The instance of the Faq to update
     * @param plugin The Plugin using this data access service
     */
    void store( Faq faq, Plugin plugin );

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    Collection<Faq> findAll( Plugin plugin );

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A {@link ReferenceList}
     */
    ReferenceList findReferenceList( Plugin plugin );

    /**
     * Finds all authorized objects of this type specified by roleKey
     * @param plugin The Plugin using this data access service
     * @param arrayRoleKey The role key array
     * @return A collection of objects
     */
    Collection<Faq> findAuthorizedFaq( String[] arrayRoleKey, Plugin plugin );

    /**
     * Finds all objects of this type
     * @param strWorkgroupKey The workgroup key
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    Collection<Faq> findByWorkgroup( String[] strWorkgroupKey, Plugin plugin );

    /**
     * Find a faq containing the subject
     * @param nSubjectId subject id
     * @param plugin the plugin
     * @return the faq
     */
    Faq findBySubjectId( int nSubjectId, Plugin plugin );

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A ReferenceList of ReferenceItem
     */
    ReferenceList findListFaq( Plugin plugin );
}
