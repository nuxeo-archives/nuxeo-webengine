/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 */
package org.nuxeo.ecm.webengine.jaxrs.context;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * An HTTP request context
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@SuppressWarnings("serial")
public class RequestContext extends HashMap<String, Object> {

    private static final ThreadLocal<RequestContext> CTX = new ThreadLocal<RequestContext>();

    public static RequestContext getActiveContext() {
        return CTX.get();
    }

    public static RequestContext getActiveContext(ServletRequest request) {
        return (RequestContext)request.getAttribute(RequestContext.class.getName());
    }

    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected List<RequestCleanupHandler> cleanupHandlers;


    public RequestContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.cleanupHandlers = new ArrayList<RequestCleanupHandler>();
        CTX.set(this);
        request.setAttribute(RequestContext.class.getName(), this);
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object o = get(key);
        return (T)o;
    }

    public void addRequestCleanupHandler(RequestCleanupHandler handler) {
        cleanupHandlers.add(handler);
    }

    public boolean removeCleanupHandler(RequestCleanupHandler handler) {
        return cleanupHandlers.remove(handler);
    }

    public void dispose() {
        request.removeAttribute(RequestContext.class.getName());
        CTX.remove();
        for (RequestCleanupHandler handler : cleanupHandlers) {
            try {
                handler.cleanup(request);
            } catch (Throwable t) {
                // do nothing to allow other cleanup handlers to do their work
            }
        }
        cleanupHandlers = null;
        request = null;
        response = null;
    }
}