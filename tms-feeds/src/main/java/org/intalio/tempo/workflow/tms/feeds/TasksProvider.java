/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.workflow.tms.feeds;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.RequestContextWrapper;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceProvider;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.impl.TemplateTargetBuilder;

public class TasksProvider extends AbstractWorkspaceProvider {
//    private static Logger LOG = LoggerFactory.getLogger(TMSTasksProvider.class);

    private final TasksCollectionAdapter adapter;

    public TasksProvider() {
        // Create the adapter that will handle all of the requests processed by
        // this provider
        this.adapter = new TasksCollectionAdapter();
        this.adapter.setHref("tasks");

        // The target resolver provides the URL path mappings
        super.setTargetResolver(new RegexTargetResolver().setPattern("/atom(\\?[^#]*)?", TargetType.TYPE_SERVICE).setPattern("/atom/([^/#?]+);categories",
                        TargetType.TYPE_CATEGORIES, "collection").setPattern("/atom/([^/#?;]+)(\\?[^#]*)?", TargetType.TYPE_COLLECTION, "collection")
                        .setPattern("/atom/([^/#?]+)/([^/#?]+)(\\?[^#]*)?", TargetType.TYPE_ENTRY, "collection", "entry"));

        // The target builder is used to construct url's for the various targets
        setTargetBuilder(new TemplateTargetBuilder().setTemplate(TargetType.TYPE_SERVICE, "{target_base}/atom").setTemplate(TargetType.TYPE_COLLECTION,
                        "{target_base}/atom/{collection}{-opt|?|q,c,s,p,l,i,o}{-join|&|q,c,s,p,l,i,o}").setTemplate(TargetType.TYPE_CATEGORIES,
                        "{target_base}/atom/{collection};categories").setTemplate(TargetType.TYPE_ENTRY, "{target_base}/atom/{collection}/{entry}"));

        // Add a Workspace descriptor so the provider can generate an atompub
        // service document
        SimpleWorkspaceInfo workspace = new SimpleWorkspaceInfo();
        workspace.setTitle("Intalio Tasks");
        workspace.addCollection(adapter);
        
        addWorkspace(workspace);

        // Add one of more Filters to be invoked prior to invoking the Provider
        addFilter(new SimpleFilter());
    }

    public CollectionAdapter getCollectionAdapter(RequestContext request) {
        return adapter;
    }

    public class SimpleFilter implements Filter {
        public ResponseContext filter(RequestContext request, FilterChain chain) {
            RequestContextWrapper rcw = new RequestContextWrapper(request);
            rcw.setAttribute("offset", 10);
            rcw.setAttribute("count", 10);
            return chain.next(rcw);
        }
    }

}
