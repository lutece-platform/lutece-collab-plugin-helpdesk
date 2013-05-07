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
import fr.paris.lutece.portal.service.role.RoleRemovalListenerService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;
import fr.paris.lutece.portal.service.workgroup.WorkgroupRemovalListenerService;

import java.util.Collection;


/**
 * This class represents a Faq object.
 */
public class Faq implements AdminWorkgroupResource
{
    private static FaqWorkgroupRemovalListener _listenerWorkgroup;
    private static FaqRoleRemovalListener _listenerRole;
    public static final String RESOURCE_TYPE = "HELPDESK_FAQ";
    public static final String ROLE_NONE = "none";
    private int _nId;
    private String _strName;
    private String _strDescription;
    private String _strRoleKey;
    private String _strWorkgroupKey;

    /**
     * Creates a new Faq object.
     */
    public Faq(  )
    {
    }

    /**
     * Initialize the Faq
     */
    public static void init(  )
    {
        // Create removal listeners and register them
        if ( _listenerWorkgroup == null )
        {
            _listenerWorkgroup = new FaqWorkgroupRemovalListener(  );
            WorkgroupRemovalListenerService.getService(  ).registerListener( _listenerWorkgroup );
        }

        if ( _listenerRole == null )
        {
            _listenerRole = new FaqRoleRemovalListener(  );
            RoleRemovalListenerService.getService(  ).registerListener( _listenerRole );
        }
    }

    /**
     * Get the Id
     *
     * @return the _nId
     */
    public int getId(  )
    {
        return _nId;
    }

    /**
     * Set the Id
     *
     * @param nId the _nId to set
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Get the name
     *
     * @return the _strName
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * Set the name
     *
     * @param strName the _strName to set
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Get the Description
     *
     * @return the _strDescription
     */
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * Set the Description
     *
     * @param strDescription the _strDescription to set
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Get the Lutece role key
     *
     * @return the _strRoleKey
     */
    public String getRoleKey(  )
    {
        return _strRoleKey;
    }

    /**
     * Set the Lutece role key
     *
     * @param strRoleKey the _strRoleKey to set
     */
    public void setRoleKey( String strRoleKey )
    {
        _strRoleKey = strRoleKey;
    }

    /**
     * Get the list of subjects <strong>directly</strong> linked to {@link Faq} (not sub-subjects)
     * @param plugin The {@link Plugin}
     * @return The collection of {@link Subject}
     */
    public Collection<Subject> getSubjectsList( Plugin plugin )
    {
        return (Collection<Subject>) SubjectHome.getInstance(  ).findByIdFaq( getId(  ), plugin );
    }

    /**
     * Get the list of themes <strong>directly</strong> linked to {@link Faq} (not sub-themes)
     *
     * @param plugin The {@link Plugin}
     * @return The collection of {@link Theme}
     */
    public Collection<Theme> getThemesList( Plugin plugin )
    {
        return (Collection<Theme>) ThemeHome.getInstance(  ).findByIdFaq( getId(  ), plugin );
    }

    /**
     * Set the workgroup key for {@link Faq}
     *
     * @param strWorkgroupKey the _strWorkgroupKey to set
     */
    public void setWorkgroup( String strWorkgroupKey )
    {
        this._strWorkgroupKey = strWorkgroupKey;
    }

    /**
     * Get the workgroup key for {@link Faq}
     *
     * @return the _strWorkgroupKey
     */
    public String getWorkgroup(  )
    {
        return _strWorkgroupKey;
    }
}
