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

import fr.paris.lutece.portal.service.mailinglist.MailingListRemovalListenerService;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Collection;


/**
 * This class represents a Theme object.
 */
public class Theme extends AbstractSubject
{
    private static ThemeMailingListRemovalListener _listenerMailinglist;
    private int _nIdMailingList;
    private Collection<VisitorQuestion> _questions;

    /**
     * Creates a new Theme object.
     */
    public Theme(  )
    {
    }

    /**
     * Initialize the theme
     */
    public static void init(  )
    {
        // Create removal listeners and register them
        if ( _listenerMailinglist == null )
        {
            _listenerMailinglist = new ThemeMailingListRemovalListener(  );
            MailingListRemovalListenerService.getService(  ).registerListener( _listenerMailinglist );
        }
    }

    /**
     * Returns the {@link VisitorQuestion} associated with the Theme
     * @return A List of {@link VisitorQuestion} objects.
     */
    public Collection<VisitorQuestion> getQuestions(  )
    {
        return _questions;
    }

    /**
     * Assigns the specified set of questions to the Theme
     * @param questions The new {@link Collection} of {@link VisitorQuestion}
     */
    public void setQuestions( Collection<VisitorQuestion> questions )
    {
        _questions = questions;
    }

    /**
     * Get the parent Theme
     * @param plugin The Plugin
     * @return The parent Theme
     */
    public Theme getParent( Plugin plugin )
    {
        return (Theme) ThemeHome.getInstance(  ).findByPrimaryKey( getIdParent(  ), plugin );
    }

    /**
     * Get the child Theme list
     * @param plugin The Plugin
     * @return A {@link Collection} of child {@link Theme}
     */
    public Collection<Theme> getChilds( Plugin plugin )
    {
        return (Collection<Theme>) ThemeHome.getInstance(  ).findByIdParent( getId(  ), plugin );
    }

    /**
     * Get the mailing list id
     * @return the mailingList id
     */
    public int getIdMailingList(  )
    {
        return _nIdMailingList;
    }

    /**
     * Set the mailing list id
     * @param nIdMailingList The mailinglist id
     */
    public void setIdMailingList( int nIdMailingList )
    {
        _nIdMailingList = nIdMailingList;
    }
}
