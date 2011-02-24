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


/**
 * This class represent a generic helpdesk subject
 *
 */
public abstract class AbstractSubject
{
    private int _nId;
    private String _strText;
    private int _nIdParent;
    private int _nIdOrder;

    /**
     * Returns the identifier of the abstract subject
     * @return The identifier of the abstract subject
     */
    public int getId(  )
    {
        return _nId;
    }

    /**
     * Sets the identifier of the abstract subject to the specified value
     * @param nId The new value
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the abstract subject string
     * @return The abstract subject string
     */
    public String getText(  )
    {
        return _strText;
    }

    /**
     * Sets the abstract subject string to the specified value
     * @param strText The new value
     */
    public void setText( String strText )
    {
        _strText = strText;
    }

    /**
     * @return the _nIdParent
     */
    public int getIdParent(  )
    {
        return _nIdParent;
    }

    /**
     * @param nIdParent the _nIdParent to set
     */
    public void setIdParent( int nIdParent )
    {
        _nIdParent = nIdParent;
    }

    /**
     * Get the parent abstract subject
     * @param plugin The Plugin
     * @return The parent abstract subject
     */
    public abstract AbstractSubject getParent( Plugin plugin );

    /**
     * Get the child abstract subject list
     * @param plugin The Plugin
     * @return A {@link Collection} of childs
     */
    public abstract Collection<?extends AbstractSubject> getChilds( Plugin plugin );

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
