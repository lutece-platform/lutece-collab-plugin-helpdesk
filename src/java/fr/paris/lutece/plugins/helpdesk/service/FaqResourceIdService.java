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
package fr.paris.lutece.plugins.helpdesk.service;

import fr.paris.lutece.plugins.helpdesk.business.Faq;
import fr.paris.lutece.plugins.helpdesk.business.FaqHome;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;


/**
 * Resource Id service for RBAC features to control access to portlet
 */
public class FaqResourceIdService extends ResourceIdService
{
    /** Permission for creating a faq */
    public static final String PERMISSION_CREATE = "CREATE";

    /** Permission for modifying a faq */
    public static final String PERMISSION_MODIFY = "MODIFY";

    /** Permission for deleting a faq */
    public static final String PERMISSION_DELETE = "DELETE";

    /** Permission for managing a subject's faq */
    public static final String PERMISSION_MANAGE_SUBJECTS = "MANAGE_SUBJECTS";

    /** Permission for managing a question answer faq */
    public static final String PERMISSION_MANAGE_QUESTIONS_ANSWERS = "MANAGE_QUESTIONS_ANSWERS";

    /** Permission for managing a question answer faq */
    public static final String PERMISSION_IMPORT_QUESTIONS_ANSWERS = "IMPORT_QUESTIONS_ANSWERS";

    /** Permission for managing a question answer faq */
    public static final String PERMISSION_MANAGE_THEMES = "MANAGE_THEMES";

    /** Permission for managing a question answer faq */
    public static final String PERMISSION_MANAGE_VISITOR_QUESTIONS = "MANAGE_VISITOR_QUESTIONS";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "helpdesk.resourceType";
    private static final String PROPERTY_LABEL_CREATE = "helpdesk.permission.create";
    private static final String PROPERTY_LABEL_MODIFY = "helpdesk.permission.modify";
    private static final String PROPERTY_LABEL_DELETE = "helpdesk.permission.delete";
    private static final String PROPERTY_LABEL_MANAGE_SUBJECTS = "helpdesk.permission.manageSubjects";
    private static final String PROPERTY_LABEL_MANAGE_QUESTIONS_ANSWERS = "helpdesk.permission.manageQuestionsAnwsers";
    private static final String PROPERTY_LABEL_IMPORT_QUESTIONS_ANSWERS = "helpdesk.permission.importQuestionsAnwsers";
    private static final String PROPERTY_LABEL_MANAGE_THEMES = "helpdesk.permission.manageThemes";
    private static final String PROPERTY_LABEL_MANAGE_VISITOR_QUESTIONS = "helpdesk.permission.manageVisitorQuestions";
    private static final String PLUGIN_NAME = "helpdesk";

    /**
     * Initializes the service
     */
    public void register(  )
    {
        ResourceType rt = new ResourceType(  );
        rt.setResourceIdServiceClass( FaqResourceIdService.class.getName(  ) );
        rt.setResourceTypeKey( Faq.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission(  );
        p.setPermissionKey( PERMISSION_CREATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MODIFY );
        p.setPermissionTitleKey( PROPERTY_LABEL_MODIFY );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_DELETE );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MANAGE_SUBJECTS );
        p.setPermissionTitleKey( PROPERTY_LABEL_MANAGE_SUBJECTS );
        rt.registerPermission( p );
        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MANAGE_QUESTIONS_ANSWERS );
        p.setPermissionTitleKey( PROPERTY_LABEL_MANAGE_QUESTIONS_ANSWERS );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_IMPORT_QUESTIONS_ANSWERS );
        p.setPermissionTitleKey( PROPERTY_LABEL_IMPORT_QUESTIONS_ANSWERS );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MANAGE_THEMES );
        p.setPermissionTitleKey( PROPERTY_LABEL_MANAGE_THEMES );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MANAGE_VISITOR_QUESTIONS );
        p.setPermissionTitleKey( PROPERTY_LABEL_MANAGE_VISITOR_QUESTIONS );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * Returns a list of resource ids
     * @param locale The current locale
     * @return A list of resource ids
     */
    public ReferenceList getResourceIdList( Locale locale )
    {
        return FaqHome.findReferenceList( PluginService.getPlugin( PLUGIN_NAME ) );
    }

    /**
     * Returns the Title of a given resource
     * @param strFaqId The Id of the resource
     * @param locale The current locale
     * @return The Title of a given resource
     */
    public String getTitle( String strFaqId, Locale locale )
    {
        Faq faq = FaqHome.load( Integer.parseInt( strFaqId ), PluginService.getPlugin( PLUGIN_NAME ) );

        return faq.getName(  );
    }
}
