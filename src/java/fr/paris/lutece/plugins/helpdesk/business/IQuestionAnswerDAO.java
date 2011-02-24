/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
 *  This interface represents the business object of the answer
 */
public interface IQuestionAnswerDAO
{
    /**
     * Calculate a new primary key to add a new QuestionAnswer
     * @param plugin The Plugin using this data access service
     * @return The new key.
     */
    int newPrimaryKey( Plugin plugin );

    /**
     * Insert a new record in the table.
     * @param questionAnswer The Instance of the object QuestionAnswer
     * @param plugin The Plugin using this data access service
     */
    void insert( QuestionAnswer questionAnswer, Plugin plugin );

    /**
     * Delete a record from the table
     * @param nIdQuestionAnswer The indentifier of the object QuestionAnswer
     * @param plugin The Plugin using this data access service
     */
    void delete( int nIdQuestionAnswer, Plugin plugin );

    /**
     * Delete all records from the table
     * @param plugin The Plugin using this data access service
     */
    void deleteAll( Plugin plugin );

    /**
     * Delete a record from the table
     *
     * @param nIdSubject The indentifier of the object QuestionAnswer
     * @param plugin The Plugin using this data access service
     */
    void deleteBySubject( int nIdSubject, Plugin plugin );

    /**
     * load the data of QuestionAnswer from the table
     * @param nIdQuestionAnswer The indentifier of the object QuestionAnswer
     * @param plugin The Plugin using this data access service
     * @return The Instance of the object QuestionAnswer
     */
    QuestionAnswer load( int nIdQuestionAnswer, Plugin plugin );

    /**
     * Update the record in the table
     * @param questionAnswer The instance of the QuestionAnswer to update
     * @param plugin The Plugin using this data access service
     */
    void store( QuestionAnswer questionAnswer, Plugin plugin );

    /**
     * Find all objects.
     * @param plugin The Plugin using this data access service
     * @return A List of objects
     */
    List<QuestionAnswer> findAll( Plugin plugin );

    /**
     * load the data of QuestionAnswer from the table
     * @param strKeywords The keywords which are searched in question/answer
     * @param plugin The Plugin using this data access service
     * @return The collection of QuestionAnswer object
     */
    List<QuestionAnswer> findByKeywords( String strKeywords, Plugin plugin );

    /**
     * Find questions specified by id subject
     * @param nIdSubject The Id of the subject
     * @param plugin The current plugin using this method
     * @return A Collection containing the results
     */
    Collection<QuestionAnswer> findByIdSubject( int nIdSubject, Plugin plugin );

    /**
     *
     * @param plugin The Plugin using this data access service
     * @param nIdSubject The Subject ID
     * @return count
     */
    int countbySubject( int nIdSubject, Plugin plugin );
    
    /**
     * Get the max order of a given subject
     * @param nIdSubject The id of the Subject
     * @param plugin The {@link Plugin}
     * @return the max order
     */
    int getMaxOrder( int nIdSubject, Plugin plugin );
}
