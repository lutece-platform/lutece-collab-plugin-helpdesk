package fr.paris.lutece.plugins.helpdesk.utils;

import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.event.ResourceEventManager;

/**
 * 
 * HelpdeskIndexerUtils
 *
 */
public class HelpdeskIndexerUtils
{
	// Indexed resource type name
	public static final String CONSTANT_SUBJECT_TYPE_RESOURCE = "HELPDESK_SUBJECT";
	public static final String CONSTANT_QUESTION_ANSWER_TYPE_RESOURCE = "HELPDESK_QUESTION_ANSWER";
	
	/**
     * Warn that a action has been done.
     * @param strIdDocument the document id
     * @param nIdTask the key of the action to do
     * @param strResourceType the ttpe of the resource
     */
    public static void addIndexerAction( String strIdDocument, int nIdTask, String strResourceType )
    {
        ResourceEvent event = new ResourceEvent();
        event.setIdResource( strIdDocument );
        event.setTypeResource( strResourceType );
        switch (nIdTask)
        {
        case IndexerAction.TASK_CREATE:
        	ResourceEventManager.fireAddedResource( event );
        	break;
        case IndexerAction.TASK_MODIFY:
        	ResourceEventManager.fireUpdatedResource( event );
        	break;
        case IndexerAction.TASK_DELETE:
        	ResourceEventManager.fireDeletedResource( event );
        	break;
        default:
        	break;
        }
    }
}
