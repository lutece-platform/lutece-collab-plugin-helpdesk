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

import fr.paris.lutece.plugins.helpdesk.service.search.HelpdeskIndexer;
import fr.paris.lutece.plugins.helpdesk.utils.HelpdeskIndexerUtils;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.ReferenceList;

import java.util.Collection;


/**
 * This class provides instances management methods (create, find, ...)
 * for Subject objects
 */
public final class FaqHome
{
    // Static variable pointed at the DAO instance
    private static IFaqDAO _dao = (IFaqDAO) SpringContextService.getPluginBean( "helpdesk", "faqDAO" );

    /**
     * Insert a new record in the table.
     * @param faq The Instance of the object Faq
     * @param plugin The Plugin using this data access service
     */
    public static void insert( Faq faq, Plugin plugin )
    {
        _dao.insert( faq, plugin );
    }

    /**
     * Delete a record from the table
     *
     * @param nId The indentifier of the object Faq
     * @param plugin The Plugin using this data access service
     */
    public static void delete( int nId, Plugin plugin )
    {
        _dao.delete( nId, plugin );
    }

    /**
     * load the data of Faq from the table
     *
     * @param nId The indentifier of the object Faq
     * @param plugin The Plugin using this data access service
     * @return The Instance of the object Faq
     */
    public static Faq load( int nId, Plugin plugin )
    {
        return _dao.load( nId, plugin );
    }

    /**
     * Recursive method for reindexing all sub-subject
     * @param subject the subject to reindex
     * @param plugin the plugin
     */
    private static void reindexSubject( Subject subject, Plugin plugin )
    {
        IndexationService.addIndexerAction( Integer.toString( subject.getId(  ) ),
            AppPropertiesService.getProperty( HelpdeskIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_MODIFY );
        
        HelpdeskIndexerUtils.addIndexerAction( Integer.toString( subject.getId(  ) ), IndexerAction.TASK_MODIFY, HelpdeskIndexerUtils.CONSTANT_SUBJECT_TYPE_RESOURCE );
        
        for ( Subject subjectChild : subject.getChilds( plugin ) )
        {
            reindexSubject( subjectChild, plugin );
        }
    }

    /**
     * Update the record in the table
     * @param faq The instance of the Faq to update
     * @param plugin The Plugin using this data access service
     */
    public static void store( Faq faq, Plugin plugin )
    {
        Faq oldFaq = load( faq.getId(  ), plugin );

        if ( oldFaq != null )
        {
            if ( !oldFaq.getRoleKey(  ).equals( faq.getRoleKey(  ) ) )
            {
                for ( Subject subject : faq.getSubjectsList( plugin ) )
                {
                    IndexationService.addIndexerAction( Integer.toString( subject.getId(  ) ),
                        AppPropertiesService.getProperty( HelpdeskIndexer.PROPERTY_INDEXER_NAME ),
                        IndexerAction.TASK_MODIFY );
                    
                    HelpdeskIndexerUtils.addIndexerAction( Integer.toString( subject.getId(  ) ), IndexerAction.TASK_MODIFY, HelpdeskIndexerUtils.CONSTANT_SUBJECT_TYPE_RESOURCE );
                    
                    for ( Subject subjectChild : subject.getChilds( plugin ) )
                    {
                        reindexSubject( subjectChild, plugin );
                    }
                }
            }
        }

        _dao.store( faq, plugin );
    }

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    public static Collection<Faq> findAll( Plugin plugin )
    {
        return _dao.findAll( plugin );
    }

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A {@link ReferenceList}
     */
    public static ReferenceList findReferenceList( Plugin plugin )
    {
        return _dao.findReferenceList( plugin );
    }

    /**
     * Finds all authorized objects of this type specified by roleKey. Faq with
     * role "none" will be ignored.
     * 
     * @param arrayRoleKey The role key array
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    public static Collection<Faq> findAuthorizedFaqWhitoutNoneRole( String[] arrayRoleKey, Plugin plugin )
    {
        return _dao.findAuthorizedFaq( arrayRoleKey, plugin );
    }

    /**
     * Finds all authorized objects of this type specified by roleKey
     * 
     * @param arrayRoleKey The role key array
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    public static Collection<Faq> findAuthorizedFaq( String[] arrayRoleKey, Plugin plugin )
    {
        String[] arrayRoleKeyLocal = arrayRoleKey;

        if ( ( arrayRoleKeyLocal == null ) || ( arrayRoleKeyLocal.length == 0 ) )
        {
            arrayRoleKeyLocal = new String[] { Faq.ROLE_NONE };
        }
        else
        {
        	//add the role "none"
        	arrayRoleKeyLocal = new String[ arrayRoleKey.length + 1 ];
        	for( int i = 0; i < arrayRoleKey.length; i++ )
        	{
        		arrayRoleKeyLocal[i] = arrayRoleKey[i];
        	}
        	arrayRoleKeyLocal[arrayRoleKey.length] = Faq.ROLE_NONE;
        }

        return _dao.findAuthorizedFaq( arrayRoleKeyLocal, plugin );
    }

    /**
     * Finds all objects of this type
     * @param strWorkgroupKey The workgroup key
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    public static Collection<Faq> findByWorkgroup( String[] strWorkgroupKey, Plugin plugin )
    {
        String[] strWorkgroupKeyLocal = strWorkgroupKey;

        if ( ( strWorkgroupKeyLocal == null ) || ( strWorkgroupKeyLocal.length == 0 ) )
        {
            strWorkgroupKeyLocal = new String[] { AdminWorkgroupService.ALL_GROUPS };
        }

        return _dao.findByWorkgroup( strWorkgroupKeyLocal, plugin );
    }

    /**
     * Find a faq containing the subject
     * @param nSubjectId subject id
     * @param plugin the plugin
     * @return the faq
     */
    public static Faq findBySubjectId( int nSubjectId, Plugin plugin )
    {
        return _dao.findBySubjectId( nSubjectId, plugin );
    }

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    public static ReferenceList findListFaq( Plugin plugin )
    {
        return _dao.findListFaq( plugin );
    }
}
