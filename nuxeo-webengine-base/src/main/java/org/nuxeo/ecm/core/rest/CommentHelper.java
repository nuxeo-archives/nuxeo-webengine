package org.nuxeo.ecm.core.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;


public class CommentHelper {

    /**
     * Get all the moderators for the corresponding workspace
     * */
    public static ArrayList<String> getModerators(CoreSession session,
            DocumentModel doc) throws Exception {
        List<DocumentModel> parents = session.getParentDocuments(doc.getRef());
        for (DocumentModel documentModel : parents) {
            if (documentModel.getType().equals("Workspace")) {
                // TO DO: test for groups eg. administrators
                String[] moderators = documentModel.getACP().listUsernamesForPermission(
                        "Moderate");
                return new ArrayList<String>(Arrays.asList(moderators));
            }

        }
        return new ArrayList<String>();
    }

    /**
     * @return true if the corresponding workspace is moderated
     * @throws Exception
     */

    public static boolean isCurrentModerated(CoreSession session,
            DocumentModel doc) throws Exception {
        return getModerators(session, doc).size() >= 1 ? true : false;
    }

    /**
     * @return true if the current user is between moderators
     * @throws Exception
     */

    public static boolean isModeratedByCurrentUser(CoreSession session,
            DocumentModel doc) throws Exception {
        ArrayList<String> moderators = getModerators(session, doc);
        if (moderators.contains(session.getPrincipal().getName())) {
            return true;
        }

        return false;

    }

}
