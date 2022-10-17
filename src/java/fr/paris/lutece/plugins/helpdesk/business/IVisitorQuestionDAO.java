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

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Collection;

/**
 *
 * Interface for Visitor DAO
 */
public interface IVisitorQuestionDAO
{
    /**
     * Delete a record from the table
     *
     *
     * @param nIdVisitorQuestion
     *            The indentifier of the object VisitorQuestion
     * @param plugin
     *            The Plugin using this data access service
     */
    void delete( int nIdVisitorQuestion, Plugin plugin );

    /**
     * Find all objects
     *
     * @param plugin
     *            The Plugin using this data access service
     * @return A Collection of objects
     */
    Collection<VisitorQuestion> findAll( Plugin plugin );

    /**
     * Insert a new record in the table.
     *
     *
     * @param visitorQuestion
     *            The Instance of the object VisitorQuestion
     * @param plugin
     *            The Plugin using this data access service
     */
    void insert( VisitorQuestion visitorQuestion, Plugin plugin );

    /**
     * load the data of VisitorQuestion from the table
     *
     *
     * @param nIdVisitorQuestion
     *            The indentifier of the object VisitorQuestion
     * @param plugin
     *            The Plugin using this data access service
     * @return The Instance of the object VisitorQuestion
     */
    VisitorQuestion load( int nIdVisitorQuestion, Plugin plugin );

    /**
     * Update the record in the table
     *
     *
     * @param visitorQuestion
     *            The instance of the VisitorQuestion to update
     * @param plugin
     *            The Plugin using this data access service
     */
    void store( VisitorQuestion visitorQuestion, Plugin plugin );

    /**
     * Find all objects
     * 
     * @param nIdUser
     *            The User ID
     * @param plugin
     *            The Plugin using this data access service
     * @return A Collection of objects
     */
    Collection<VisitorQuestion> findByUser( int nIdUser, Plugin plugin );

    /**
     * Find all objects
     * 
     * @param nIdTheme
     *            The Theme id
     * @param plugin
     *            The Plugin using this data access service
     * @return A Collection of objects
     */
    Collection<VisitorQuestion> findByTheme( int nIdTheme, Plugin plugin );

    /**
     * Find all archived questions by Theme
     * 
     * @param nIdTheme
     *            The Theme id
     * @param plugin
     *            The Plugin using this data access service
     * @return A Collection of objects
     */
    Collection<VisitorQuestion> findArchivedQuestionsByTheme( int nIdTheme, Plugin plugin );
}
