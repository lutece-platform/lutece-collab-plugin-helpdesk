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


/**
 * This class represents a visitor's question.
 */
public class VisitorQuestion
{
    private int _nIdVisitorQuestion;
    private String _strLastname;
    private String _strFirstname;
    private String _strEmail;
    private String _strQuestion;
    private String _strAnswer;
    private java.sql.Date _dateVQ;
    private int _nIdUser;
    private int _nIdTheme;

    /**
     * Creates a new VisitorQuestion object.
     */
    public VisitorQuestion(  )
    {
    }

    /**
     * Returns the identifier of the object
     * @return The identifier
     */
    public int getIdVisitorQuestion(  )
    {
        return _nIdVisitorQuestion;
    }

    /**
     * Sets the identifier of the object to the specified value
     * @param nIdVisitorQuestion The new value
     */
    public void setIdVisitorQuestion( int nIdVisitorQuestion )
    {
        _nIdVisitorQuestion = nIdVisitorQuestion;
    }

    /**
     * Returns the last name of the visitor
     * @return The last name
     */
    public String getLastname(  )
    {
        return _strLastname;
    }

    /**
     * Sets the last name of the visitor to the specified value
     * @param strLastname The new value
     */
    public void setLastname( String strLastname )
    {
        _strLastname = strLastname;
    }

    /**
     * Returns the first name of the visitor
     * @return The first name
     */
    public String getFirstname(  )
    {
        return _strFirstname;
    }

    /**
     * Sets the first name of the visitor to the specified value
     * @param strFirstname The new value
     */
    public void setFirstname( String strFirstname )
    {
        _strFirstname = strFirstname;
    }

    /**
     * Returns the email address of the visitor
     * @return The email address
     */
    public String getEmail(  )
    {
        return _strEmail;
    }

    /**
     * Sets the email address of the visitor to the specified value
     * @param strEmail The new value
     */
    public void setEmail( String strEmail )
    {
        _strEmail = strEmail;
    }

    /**
     * Returns the question
     * @return A string literal containing the question
     */
    public String getQuestion(  )
    {
        return _strQuestion;
    }

    /**
     * Sets the question to the specified value
     * @param strQuestion The new value
     */
    public void setQuestion( String strQuestion )
    {
        _strQuestion = strQuestion;
    }

    /**
     * Returns the answer
     * @return A string literal containing the answer
     */
    public String getAnswer(  )
    {
        return _strAnswer;
    }

    /**
     * Sets the answer to the specified value
     * @param strAnswer The new value
     */
    public void setAnswer( String strAnswer )
    {
        _strAnswer = strAnswer;
    }

    /**
     * Sets the date of the visitor question
     *
     * @param dateVQ The date
     */
    public void setDate( java.sql.Date dateVQ )
    {
        _dateVQ = dateVQ;
    }

    /**
     * Returns the date of the visitor question
     *
     * @return The date
     */
    public java.sql.Date getDate(  )
    {
        return _dateVQ;
    }

    /**
     * Returns the identifier of the HelpdeskUser.
     *
     * @return the HelpdeskUser identifier
     */
    public int getIdUser(  )
    {
        return _nIdUser;
    }

    /**
     * Sets the identifier of the HelpdeskUser to the specified int.
     *
     * @param nIdUser the new identifier
     */
    public void setIdUser( int nIdUser )
    {
        _nIdUser = nIdUser;
    }

    /**
     * Returns the theme
     * @return The theme
     */
    public int getIdTheme(  )
    {
        return _nIdTheme;
    }

    /**
     * Sets the theme to the specified value
     * @param nIdTheme The new value
     */
    public void setIdTheme( int nIdTheme )
    {
        _nIdTheme = nIdTheme;
    }
}
