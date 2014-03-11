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
package fr.paris.lutece.plugins.helpdesk.service.search;

import fr.paris.lutece.plugins.helpdesk.business.Faq;
import fr.paris.lutece.plugins.helpdesk.business.FaqHome;
import fr.paris.lutece.plugins.helpdesk.business.QuestionAnswer;
import fr.paris.lutece.plugins.helpdesk.business.Subject;
import fr.paris.lutece.plugins.helpdesk.business.SubjectHome;
import fr.paris.lutece.plugins.helpdesk.service.HelpdeskPlugin;
import fr.paris.lutece.plugins.helpdesk.service.helpdesksearch.HelpdeskSearchItem;
import fr.paris.lutece.plugins.helpdesk.web.HelpdeskApp;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/**
 * Helpdesk Indexer
 * 
 */
public class HelpdeskIndexer implements SearchIndexer
{
    public static final String PROPERTY_INDEXER_NAME = "helpdesk.indexer.name";
    public static final String SHORT_NAME_SUBJECT = "hds";
    public static final String SHORT_NAME_QUESTION_ANSWER = "hdq";
    private static final String ENABLE_VALUE_TRUE = "1";
    private static final String PROPERTY_PAGE_PATH_LABEL = "helpdesk.pagePathLabel";
    private static final String PROPERTY_INDEXER_DESCRIPTION = "helpdesk.indexer.description";
    private static final String PROPERTY_INDEXER_VERSION = "helpdesk.indexer.version";
    private static final String PROPERTY_INDEXER_ENABLE = "helpdesk.indexer.enable";
    private static final String CONSTANT_SPACE = " ";
    private static final String JSP_PAGE_SEARCH = "jsp/site/Portal.jsp?page=helpdesk";

    /**
     * Returns the indexer service description
     * @return The indexer service description
     */
    public String getDescription( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_DESCRIPTION );
    }

    /**
     * Index all documents
     * 
     * @throws IOException the exception
     * @throws InterruptedException the exception
     * @throws SiteMessageException the exception
     */
    public void indexDocuments( ) throws IOException, InterruptedException, SiteMessageException
    {
        Plugin plugin = PluginService.getPlugin( HelpdeskPlugin.PLUGIN_NAME );

        for ( Faq faq : FaqHome.findAll( plugin ) )
        {
            for ( Subject subject : (Collection<Subject>) SubjectHome.getInstance( ).findByIdFaq( faq.getId( ), plugin ) )
            {
                indexSubject( faq, subject );
            }
        }
    }

    /**
     * Recursive method for indexing a subject and his children
     * 
     * @param faq the faq linked to the subject
     * @param subject the subject
     * @throws IOException I/O Exception
     * @throws InterruptedException interruptedException
     */
    public void indexSubject( Faq faq, Subject subject ) throws IOException, InterruptedException
    {
        String strPortalUrl = AppPathService.getPortalUrl( );
        Plugin plugin = PluginService.getPlugin( HelpdeskPlugin.PLUGIN_NAME );

        UrlItem urlSubject = new UrlItem( strPortalUrl );
        urlSubject.addParameter( XPageAppService.PARAM_XPAGE_APP,
                AppPropertiesService.getProperty( PROPERTY_PAGE_PATH_LABEL ) ); //FIXME
        urlSubject.addParameter( HelpdeskApp.PARAMETER_FAQ_ID, faq.getId( ) );
        urlSubject.setAnchor( HelpdeskApp.ANCHOR_SUBJECT + subject.getId( ) );

        org.apache.lucene.document.Document docSubject = null;
        try
        {
            docSubject = getDocument( subject, faq.getRoleKey( ), urlSubject.getUrl( ), plugin );
        }
        catch ( Exception e )
        {
            String strMessage = "FAQ ID : " + faq.getId( ) + " - Subject ID : " + subject.getId( );
            IndexationService.error( this, e, strMessage );
        }
        if ( docSubject != null )
        {
            IndexationService.write( docSubject );
        }

        for ( QuestionAnswer questionAnswer : (List<QuestionAnswer>) subject.getQuestions( ) )
        {
            if ( questionAnswer.isEnabled( ) )
            {
                UrlItem urlQuestionAnswer = new UrlItem( strPortalUrl );
                urlQuestionAnswer.addParameter( XPageAppService.PARAM_XPAGE_APP,
                        AppPropertiesService.getProperty( PROPERTY_PAGE_PATH_LABEL ) ); //FIXME
                urlQuestionAnswer.addParameter( HelpdeskApp.PARAMETER_FAQ_ID, faq.getId( ) );
                urlQuestionAnswer
                        .setAnchor( HelpdeskApp.ANCHOR_QUESTION_ANSWER + questionAnswer.getIdQuestionAnswer( ) );

                org.apache.lucene.document.Document docQuestionAnswer = null;
                try
                {
                    docQuestionAnswer = getDocument( faq.getId( ), questionAnswer, urlQuestionAnswer.getUrl( ),
                            faq.getRoleKey( ), plugin );
                }
                catch ( Exception e )
                {
                    String strMessage = "FAQ ID : " + faq.getId( ) + " - Subject ID : " + subject.getId( )
                            + " - QuestionAnswer ID :" + questionAnswer.getIdQuestionAnswer( );
                    IndexationService.error( this, e, strMessage );
                }
                if ( docQuestionAnswer != null )
                {
                    IndexationService.write( docQuestionAnswer );
                }
            }
        }

        Collection<Subject> children = subject.getChilds( plugin );

        for ( Subject childSubject : children )
        {
            indexSubject( faq, childSubject );
        }
    }

    /**
     * Get the subject document
     * @param strDocument id of the subject to index
     * @return The list of lucene documents
     * @throws IOException the exception
     * @throws InterruptedException the exception
     * @throws SiteMessageException the exception
     */
    public List<Document> getDocuments( String strDocument ) throws IOException, InterruptedException,
            SiteMessageException
    {
        List<org.apache.lucene.document.Document> listDocs = new ArrayList<org.apache.lucene.document.Document>( );
        String strPortalUrl = AppPathService.getPortalUrl( );
        Plugin plugin = PluginService.getPlugin( HelpdeskPlugin.PLUGIN_NAME );

        Subject subject = (Subject) SubjectHome.getInstance( ).findByPrimaryKey( Integer.parseInt( strDocument ),
                plugin );

        if ( subject != null )
        {
            UrlItem urlSubject = new UrlItem( strPortalUrl );
            urlSubject.addParameter( XPageAppService.PARAM_XPAGE_APP,
                    AppPropertiesService.getProperty( PROPERTY_PAGE_PATH_LABEL ) ); //FIXME

            //if it's a sub-subject, we need to get the first parent to have the faq
            int nIdParent = subject.getIdParent( );
            Subject parentSubject = subject;

            while ( nIdParent != SubjectHome.FIRST_ORDER )
            {
                parentSubject = (Subject) SubjectHome.getInstance( ).findByPrimaryKey( nIdParent, plugin );
                nIdParent = parentSubject.getIdParent( );
            }

            Faq faq = FaqHome.findBySubjectId( parentSubject.getId( ), plugin );

            if ( faq != null )
            {
                urlSubject.addParameter( HelpdeskApp.PARAMETER_FAQ_ID, faq.getId( ) );
                urlSubject.setAnchor( HelpdeskApp.ANCHOR_SUBJECT + subject.getId( ) );

                org.apache.lucene.document.Document docSubject = getDocument( subject, faq.getRoleKey( ),
                        urlSubject.getUrl( ), plugin );
                listDocs.add( docSubject );

                for ( QuestionAnswer questionAnswer : (List<QuestionAnswer>) subject.getQuestions( ) )
                {
                    if ( questionAnswer.isEnabled( ) )
                    {
                        UrlItem urlQuestionAnswer = new UrlItem( strPortalUrl );
                        urlQuestionAnswer.addParameter( XPageAppService.PARAM_XPAGE_APP,
                                AppPropertiesService.getProperty( PROPERTY_PAGE_PATH_LABEL ) ); //FIXME
                        urlQuestionAnswer.addParameter( HelpdeskApp.PARAMETER_FAQ_ID, faq.getId( ) );
                        urlQuestionAnswer.setAnchor( HelpdeskApp.ANCHOR_QUESTION_ANSWER
                                + questionAnswer.getIdQuestionAnswer( ) );

                        org.apache.lucene.document.Document docQuestionAnswer = getDocument( faq.getId( ),
                                questionAnswer, urlQuestionAnswer.getUrl( ), faq.getRoleKey( ), plugin );
                        listDocs.add( docQuestionAnswer );
                    }
                }
            }
        }

        return listDocs;
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of the
     * question/answer list
     * 
     * @param nIdFaq The {@link Faq} Id
     * @param questionAnswer the {@link QuestionAnswer} to index
     * @param strUrl the url of the subject
     * @param strRoleKey The role key
     * @param plugin The {@link Plugin}
     * @return A Lucene {@link Document} containing QuestionAnswer Data
     * @throws IOException The IO Exception
     * @throws InterruptedException The InterruptedException
     */
    public static org.apache.lucene.document.Document getDocument( int nIdFaq, QuestionAnswer questionAnswer,
            String strUrl, String strRoleKey, Plugin plugin ) throws IOException, InterruptedException
    {
        // make a new, empty document
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document( );

        FieldType ft = new FieldType( StringField.TYPE_STORED );
        ft.setOmitNorms( false );

        doc.add( new Field( HelpdeskSearchItem.FIELD_FAQ_ID, String.valueOf( nIdFaq ), ft ) );

        doc.add( new Field( HelpdeskSearchItem.FIELD_ROLE, strRoleKey, ft ) );

        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the question/answer, but is not searchable.
        doc.add( new Field( HelpdeskSearchItem.FIELD_URL, strUrl, ft ) );

        doc.add( new Field( HelpdeskSearchItem.FIELD_SUBJECT, String.valueOf( questionAnswer.getIdSubject( ) ), ft ) );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with question/answer, it is indexed, but it is not
        // tokenized prior to indexing.
        String strIdQuestionAnswer = String.valueOf( questionAnswer.getIdQuestionAnswer( ) );
        doc.add( new Field( HelpdeskSearchItem.FIELD_UID, strIdQuestionAnswer + "_" + SHORT_NAME_QUESTION_ANSWER, ft ) );

        // Add the last modified date of the file a field named "modified".
        // Use a field that is indexed (i.e. searchable), but don't tokenize
        // the field into words.
        String strDate = DateTools.dateToString( questionAnswer.getCreationDate( ), DateTools.Resolution.DAY );
        doc.add( new Field( HelpdeskSearchItem.FIELD_DATE, strDate, ft ) );

        String strContentToIndex = getContentToIndex( questionAnswer, plugin );
        ContentHandler handler = new BodyContentHandler( );
        Metadata metadata = new Metadata( );
        try
        {
            new HtmlParser( ).parse( new ByteArrayInputStream( strContentToIndex.getBytes( ) ), handler, metadata,
                    new ParseContext( ) );
        }
        catch ( SAXException e )
        {
            throw new AppException( "Error during page parsing." );
        }
        catch ( TikaException e )
        {
            throw new AppException( "Error during page parsing." );
        }

        //the content of the article is recovered in the parser because this one
        //had replaced the encoded caracters (as &eacute;) by the corresponding special caracter (as ?)
        StringBuilder sb = new StringBuilder( handler.toString( ) );

        // Add the tag-stripped contents as a Reader-valued Text field so it will
        // get tokenized and indexed.
        doc.add( new Field( HelpdeskSearchItem.FIELD_CONTENTS, sb.toString( ), TextField.TYPE_NOT_STORED ) );

        // Add the subject name as a separate Text field, so that it can be searched
        // separately.
        doc.add( new Field( HelpdeskSearchItem.FIELD_TITLE, questionAnswer.getQuestion( ), TextField.TYPE_STORED ) );

        doc.add( new Field( HelpdeskSearchItem.FIELD_TYPE, HelpdeskPlugin.PLUGIN_NAME, TextField.TYPE_STORED ) );

        // return the document
        return doc;
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of the
     * subject list
     * 
     * @param subject the {@link Subject} to index
     * @param strUrl the url of the subject
     * @param strRoleKey The role key
     * @param plugin The {@link Plugin}
     * @return The Lucene {@link Document} containing Subject data
     * @throws IOException The IO Exception
     * @throws InterruptedException The InterruptedException
     */
    public static org.apache.lucene.document.Document getDocument( Subject subject, String strRoleKey, String strUrl,
            Plugin plugin ) throws IOException, InterruptedException
    {
        // make a new, empty document
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document( );

        FieldType ft = new FieldType( StringField.TYPE_STORED );
        ft.setOmitNorms( false );

        FieldType ftNotStored = new FieldType( StringField.TYPE_NOT_STORED );
        ftNotStored.setOmitNorms( false );

        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the question/answer, but is not searchable.
        doc.add( new Field( SearchItem.FIELD_URL, strUrl, ft ) );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with question/answer, it is indexed, but it is not
        // tokenized prior to indexing.
        String strIdSubject = String.valueOf( subject.getId( ) );
        doc.add( new Field( SearchItem.FIELD_UID, strIdSubject + "_" + SHORT_NAME_SUBJECT, ftNotStored ) );

        doc.add( new Field( SearchItem.FIELD_CONTENTS, subject.getText( ), ftNotStored ) );

        // Add the subject name as a separate Text field, so that it can be searched
        // separately.
        doc.add( new Field( SearchItem.FIELD_TITLE, subject.getText( ), ft ) );

        doc.add( new Field( SearchItem.FIELD_TYPE, HelpdeskPlugin.PLUGIN_NAME, ft ) );

        doc.add( new Field( SearchItem.FIELD_ROLE, strRoleKey, ft ) );

        // return the document
        return doc;
    }

    /**
     * Set the Content to index (Question and Answer)
     * @param questionAnswer The {@link QuestionAnswer} to index
     * @param plugin The {@link Plugin}
     * @return The content to index
     */
    private static String getContentToIndex( QuestionAnswer questionAnswer, Plugin plugin )
    {
        StringBuffer sbContentToIndex = new StringBuffer( );
        //Do not index question here
        sbContentToIndex.append( questionAnswer.getQuestion( ) );
        sbContentToIndex.append( CONSTANT_SPACE );
        sbContentToIndex.append( questionAnswer.getAnswer( ) );

        return sbContentToIndex.toString( );
    }

    /**
     * Get the name of the indexer.
     * @return The name
     */
    public String getName( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_NAME );
    }

    /**
     * Get the version of the indexer
     * @return The version number
     */
    public String getVersion( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_VERSION );
    }

    /**
     * Get the state of indexer
     * @return Return true if the indexer is enabled, false else.
     */
    public boolean isEnable( )
    {
        boolean bReturn = false;
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE );

        if ( ( strEnable != null )
                && ( strEnable.equalsIgnoreCase( Boolean.TRUE.toString( ) ) || strEnable.equals( ENABLE_VALUE_TRUE ) )
                && PluginService.isPluginEnable( HelpdeskPlugin.PLUGIN_NAME ) )
        {
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getListType( )
    {
        List<String> listType = new ArrayList<String>( );
        listType.add( HelpdeskPlugin.PLUGIN_NAME );
        return listType;
    }

    /**
     * {@inheritDoc}
     */
    public String getSpecificSearchAppUrl( )
    {
        return JSP_PAGE_SEARCH;
    }
}
