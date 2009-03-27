/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.webengine.sites;

import static org.nuxeo.webengine.utils.SiteUtilsConstants.DELETED_LIFE_CYCLE;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.ui.tree.document.DocumentContentProvider;

public class SiteContentProvider extends DocumentContentProvider {

    private static final Log log = LogFactory.getLog(SiteContentProvider.class);

    private static final long serialVersionUID = 1L;

    public SiteContentProvider(CoreSession session) {
        super(session);
    }

    @Override
    public Object[] getChildren(Object obj) {
        Object[] objects = super.getChildren(obj);
        List<Object> children = new Vector<Object>();
        for (Object object : objects) {
            DocumentModel document = (DocumentModel) object;
            // filter pages
            try {
                if (SiteHelper.getBoolean(document, "webp:publish", false)
                        && SiteHelper.getBoolean(document, "webp:pushtomenu",
                                false)
                        && !DELETED_LIFE_CYCLE.equals(document.getCurrentLifeCycleState())) {
                    children.add(document);
                }
            } catch (ClientException e) {
                log.debug(
                        "Problems retrieving the current state of a document ... ",
                        e);
            }
        }
        return children.toArray();
    }
}
