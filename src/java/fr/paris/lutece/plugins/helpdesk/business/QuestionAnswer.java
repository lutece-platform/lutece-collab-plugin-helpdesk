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

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Date;


/**
 * This class represents question and answers on the help desk
 */
public class QuestionAnswer
{
    public static final int STATUS_PUBLISHED = 1;
    public static final int STATUS_UNPUBLISHED = 0;
    private int _nIdQuestionAnswer;
    private String _strQuestion;
    private String _strAnswer;
    private int _nIdSubject;
    private int _nIdOrder;
    private boolean _bIsEnabled;
    private Date _dateCreation;

    /**
     * Creates a new QuestionAnswer object.
     */
    public QuestionAnswer(  )
    {
    }

    /**
     * Returns the identifier of the object
     * @return The identifier
     */
    public int getIdQuestionAnswer(  )
    {
        return _nIdQuestionAnswer;
    }

    /**
     * Sets the identifier of the object to the specified value
     * @param nIdQuestionAnswer The new value
     */
    public void setIdQuestionAnswer( int nIdQuestionAnswer )
    {
        _nIdQuestionAnswer = nIdQuestionAnswer;
    }

    /**
     * Returns the question associated with this object
     * @return A string literal containing the question
     */
    public String getQuestion(  )
    {
        return _strQuestion;
    }

    /**
     * Associates a new question with this object
     * @param strQuestion The new value
     */
    public void setQuestion( String strQuestion )
    {
        _strQuestion = strQuestion;
    }

    /**
         * Returns the answer associated with this object
         * @return A string literal containing the answer
     */
    public String getAnswer(  )
    {
        return _strAnswer;
    }

    /**
         * Associates a new answer with this object
         * @param strAnswer The new value
     */
    public void setAnswer( String strAnswer )
    {
        _strAnswer = strAnswer;
    }

    /**
     * Returns the identifier of the subject
     * @return The identifier
     */
    public int getIdSubject(  )
    {
        return _nIdSubject;
    }

    /**
     * Return the {@link Subject}
     * @param plugin The plugin
     * @return The {@link Subject}
     */
    public Subject getSubject( Plugin plugin )
    {
        return (Subject) SubjectHome.getInstance(  ).findByPrimaryKey( getIdSubject(  ), plugin );
    }

    /**
     * Assigns this QuestionAnswer to a subject
     * @param nIdSubject The identifier of the subject
     */
    public void setIdSubject( int nIdSubject )
    {
        _nIdSubject = nIdSubject;
    }

    /**
     * Returns the status of the object
     * @return true if the object is enabled
     */
    public boolean isEnabled(  )
    {
        return _bIsEnabled;
    }

    /**
     * Sets the status of the object
     * @param status 1 to enable the object, any other value to disable it
     */
    public void setStatus( int status )
    {
        if ( status == 1 )
        {
            _bIsEnabled = true;
        }
        else
        {
            _bIsEnabled = false;
        }
    }

    /**
     * @return the _dateCreation
     */
    public Date getCreationDate(  )
    {
        return _dateCreation;
    }

    /**
     * @param dateCreationDate the _dateCreation to set
     */
    public void setCreationDate( Date dateCreationDate )
    {
        _dateCreation = dateCreationDate;
    }
    
    /**
     * @return the _nIdOrder
     */
    public int getIdOrder(  )
    {
        return _nIdOrder;
    }

    /**
     * @param nIdOrder the _nIdOrder to set
     */
    public void setIdOrder( int nIdOrder )
    {
        _nIdOrder = nIdOrder;
    }
}
