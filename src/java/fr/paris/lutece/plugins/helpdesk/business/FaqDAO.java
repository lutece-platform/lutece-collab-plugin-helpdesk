/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for Faq objects
 */
public final class FaqDAO implements IFaqDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_faq ) FROM helpdesk_faq";
    private static final String SQL_QUERY_SELECT = " SELECT id_faq, name, description, role_key, workgroup_key FROM helpdesk_faq WHERE id_faq = ?";
    private static final String SQL_QUERY_SELECT_BY_SUBJECT = " SELECT hlfs.id_faq, hf.role_key FROM helpdesk_ln_faq_subject hlfs, helpdesk_faq hf WHERE hlfs.id_subject = ? AND hlfs.id_faq=hf.id_faq";
    private static final String SQL_QUERY_INSERT = " INSERT INTO helpdesk_faq ( id_faq, name, description, role_key, workgroup_key ) VALUES ( ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM helpdesk_faq WHERE id_faq = ?";
    private static final String SQL_QUERY_UPDATE = " UPDATE helpdesk_faq SET name = ?, description = ?, role_key = ? , workgroup_key = ? WHERE id_faq = ?";
    private static final String SQL_QUERY_SELECTALL = " SELECT id_faq, name, description, role_key, workgroup_key  FROM helpdesk_faq ORDER BY name ";
    private static final String SQL_QUERY_SELECT_AUTHORIZED_SELECT = " SELECT id_faq, name, description, role_key, workgroup_key  FROM helpdesk_faq WHERE ";
    private static final String SQL_QUERY_SELECT_AUTHORIZED_ROLE_KEY = " role_key = ? ";
    private static final String SQL_QUERY_SELECT_AUTHORIZED_WORKGROUP_KEY = " workgroup_key = ? ";
    private static final String SQL_QUERY_SELECT_AUTHORIZED_OR = " OR ";
    private static final String SQL_QUERY_SELECT_AUTHORIZED_ORDER_BY = " ORDER BY name ";

    ///////////////////////////////////////////////////////////////////////////////////////
    //Access methods to data

    /**
     * Calculate a new primary key to add a new Faq
     *
     * @param plugin The Plugin using this data access service
     * @return The new key.
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;

        daoUtil.free(  );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     *
     * @param faq The Instance of the object Faq
     * @param plugin The Plugin using this data access service
     */
    public synchronized void insert( Faq faq, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        faq.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, faq.getId(  ) );
        daoUtil.setString( 2, faq.getName(  ) );
        daoUtil.setString( 3, faq.getDescription(  ) );
        daoUtil.setString( 4, faq.getRoleKey(  ) );
        daoUtil.setString( 5, faq.getWorkgroup(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete a record from the table
     *
     * @param nIdFaq The indentifier of the object Faq
     * @param plugin The Plugin using this data access service
     */
    public void delete( int nIdFaq, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdFaq );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * load the data of Faq from the table
     *
     * @param nIdFaq The indentifier of the object Faq
     * @param plugin The Plugin using this data access service
     * @return The Instance of the object Faq
     */
    public Faq load( int nIdFaq, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdFaq );
        daoUtil.executeQuery(  );

        Faq faq = null;

        if ( daoUtil.next(  ) )
        {
            faq = new Faq(  );
            faq.setId( daoUtil.getInt( 1 ) );
            faq.setName( daoUtil.getString( 2 ) );
            faq.setDescription( daoUtil.getString( 3 ) );
            faq.setRoleKey( daoUtil.getString( 4 ) );
            faq.setWorkgroup( daoUtil.getString( 5 ) );
        }

        daoUtil.free(  );

        return faq;
    }

    /**
     * Update the record in the table
     *
     * @param faq The instance of the Faq to update
     * @param plugin The Plugin using this data access service
     */
    public void store( Faq faq, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( 1, faq.getName(  ) );
        daoUtil.setString( 2, faq.getDescription(  ) );
        daoUtil.setString( 3, faq.getRoleKey(  ) );
        daoUtil.setString( 4, faq.getWorkgroup(  ) );
        daoUtil.setInt( 5, faq.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    public Collection<Faq> findAll( Plugin plugin )
    {
        Collection<Faq> list = new ArrayList<Faq>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Faq faq = new Faq(  );
            faq.setId( daoUtil.getInt( 1 ) );
            faq.setName( daoUtil.getString( 2 ) );
            faq.setDescription( daoUtil.getString( 3 ) );
            faq.setRoleKey( daoUtil.getString( 4 ) );
            faq.setWorkgroup( daoUtil.getString( 5 ) );
            list.add( faq );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A {@link ReferenceList}
     */
    public ReferenceList findReferenceList( Plugin plugin )
    {
        ReferenceList list = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Finds all authorized objects of this type specified by roleKey (front office)
     * @param plugin The Plugin using this data access service
     * @param arrayRoleKey The role key array
     * @return A collection of objects
     */
    public Collection<Faq> findAuthorizedFaq( String[] arrayRoleKey, Plugin plugin )
    {
        return findByKey( arrayRoleKey, false, plugin );
    }

    /**
     * Finds all objects of this type
     * @param strWorkgroupKey The workgroup key
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    public Collection<Faq> findByWorkgroup( String[] strWorkgroupKey, Plugin plugin )
    {
        return findByKey( strWorkgroupKey, true, plugin );
    }

    /**
     * Finds all objects for a specified key
     * @param arrayKeys The key to filter
     * @param bAdminWorkgroup true = filter by workgroup, false = filter by role key
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    private Collection<Faq> findByKey( String[] arrayKeys, boolean bAdminWorkgroup, Plugin plugin )
    {
        Collection<Faq> list = new ArrayList<Faq>(  );
        String strSql = SQL_QUERY_SELECT_AUTHORIZED_SELECT;

        if ( arrayKeys.length == 0 )
        {
            return list;
        }

        int i = 1;

        for ( String strRoleKey : arrayKeys )
        {
            if ( i++ > 1 )
            {
                strSql += SQL_QUERY_SELECT_AUTHORIZED_OR;
            }

            if ( bAdminWorkgroup )
            {
                strSql += SQL_QUERY_SELECT_AUTHORIZED_WORKGROUP_KEY;
            }
            else
            {
                strSql += SQL_QUERY_SELECT_AUTHORIZED_ROLE_KEY;
            }
        }

        strSql += SQL_QUERY_SELECT_AUTHORIZED_ORDER_BY;

        DAOUtil daoUtil = new DAOUtil( strSql, plugin );
        i = 1;

        for ( String strKey : arrayKeys )
        {
            daoUtil.setString( i++, strKey );
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Faq faq = new Faq(  );
            faq.setId( daoUtil.getInt( 1 ) );
            faq.setName( daoUtil.getString( 2 ) );
            faq.setDescription( daoUtil.getString( 3 ) );
            faq.setRoleKey( daoUtil.getString( 4 ) );
            faq.setWorkgroup( daoUtil.getString( 5 ) );
            list.add( faq );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Find a faq containing the subject
     * @param nSubjectId subject id
     * @param plugin the plugin
     * @return the faq
     */
    public Faq findBySubjectId( int nSubjectId, Plugin plugin )
    {
        Faq faq = new Faq(  );
        String strSql = SQL_QUERY_SELECT_BY_SUBJECT;
        DAOUtil daoUtil = new DAOUtil( strSql, plugin );
        daoUtil.setInt( 1, nSubjectId );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            faq.setId( daoUtil.getInt( 1 ) );
            faq.setRoleKey( daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );
        
        return faq;
    }

    /**
     * Finds all objects of this type
     * @param plugin The Plugin using this data access service
     * @return A collection of objects
     */
    public ReferenceList findListFaq( Plugin plugin )
    {
        ReferenceList list = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return list;
    }
}
