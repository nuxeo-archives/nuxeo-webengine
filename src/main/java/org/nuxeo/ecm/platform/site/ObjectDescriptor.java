/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     bstefanescu
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.site;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.platform.site.actions.ActionDescriptor;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@XObject("object")
public class ObjectDescriptor {

    @XNode("@id") // internal id needed to uniquely identify the object
    protected String id;

    @XNode("@type")
    protected String type;

    @XNode("@extends")
    protected String base;

    @XNode("requestHandler")
    protected Class<RequestHandler> requestHandlerClass;

    @XNodeMap(value="actions/action", key="@id", type=HashMap.class, componentType=ActionDescriptor.class)
    protected Map<String, ActionDescriptor> actions;

    protected RequestHandler requestHandler;


    public ObjectDescriptor() {}

    public ObjectDescriptor(String id, String type, String base, ActionDescriptor ... actions) {
        this.id = id;
        this.type = type;
        this.base = base;
        this.actions = new HashMap<String, ActionDescriptor>();
        if (actions != null) {
            for (int i=0; i<actions.length; i++) {
                this.actions.put(actions[i].getId(), actions[i]);
            }
        }
    }

    public ObjectDescriptor(String id, String type, String base, Collection<ActionDescriptor> actions) {
        this.id = id;
        this.type = type;
        this.base = base;
        this.actions = new HashMap<String, ActionDescriptor>();
        if (actions != null) {
            for (ActionDescriptor action : actions) {
                this.actions.put(action.getId(), action);
            }
        }
    }

    /**
     * @return the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @return the base.
     */
    public String getBase() {
        return base;
    }

    /**
     * @return the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @return the requestHandlerClass.
     */
    public Class<RequestHandler> getRequestHandlerClass() {
        return requestHandlerClass;
    }

    /**
     * @return the requestHandler.
     */
    public RequestHandler getRequestHandler() throws SiteException {
        if (requestHandler == null) {
            if (requestHandlerClass == null) {
                requestHandler = RequestHandler.DEFAULT;
            } else {
                try {
                    requestHandler = requestHandlerClass.newInstance();
                } catch (Exception e) {
                    throw new SiteException("Failed to instantiate request handler for object type: "+type, e);
                }
            }
        }
        return requestHandler;
    }

    /**
     * @param requestHandler the requestHandler to set.
     */
    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    /**
     * @return the actions.
     */
    public Map<String, ActionDescriptor> getActions() {
        return actions;
    }

    public ActionDescriptor getAction(String name) {
        return actions.get(name);
    }

    public void merge(ObjectDescriptor baseObj) {
        if (requestHandlerClass == null) {
            requestHandlerClass = baseObj.requestHandlerClass;
        }
        if (baseObj.actions == null) return;
        // merge actions
        for (ActionDescriptor desc : baseObj.actions.values()) {
            ActionDescriptor action = actions.get(desc.getId());
            if (action == null) { // import base action
                actions.put(desc.getId(), desc);
            } else { // merge the 2 actions
                action.merge(desc);
            }
        }
    }

}