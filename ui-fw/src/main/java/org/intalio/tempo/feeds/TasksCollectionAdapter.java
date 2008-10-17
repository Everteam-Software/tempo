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
package org.intalio.tempo.feeds;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.protocol.server.impl.AbstractCollectionAdapter;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.lang.StringUtils;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.util.PropertyUtils;
import org.intalio.tempo.security.ws.TokenClient;
import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.URIUtils;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.uiframework.forms.GenericFormManager;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TasksCollectionAdapter extends AbstractCollectionAdapter {

    private static Logger LOG = LoggerFactory.getLogger(TasksCollectionAdapter.class);
    private GenericFormManager _manager;

    public TasksCollectionAdapter() {
        super();
        _manager = (GenericFormManager) FormManagerBroker.getInstance().getFormManager();
    }

    @Override
    public String getAuthor(RequestContext requestcontext) throws ResponseContextException {
        return "intalio";
    }

    @Override
    public String getId(RequestContext requestcontext) {
        return "ID";
    }

    /**
     * Not supported
     */
    public ResponseContext deleteEntry(RequestContext requestcontext) {
        return ProviderHelper.unavailable(requestcontext);
    }

    private Entry getAbderaEntry(RequestContext request) {
        try {
            return getFeedDocument(request).getRoot().getEntry(getEntryID(request));
        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String getEntryID(RequestContext request) {
        if (request.getTarget().getType() != TargetType.TYPE_ENTRY)
            return null;
        String[] segments = request.getUri().toString().split("/");
        String ret = UrlEncoding.decode(segments[segments.length - 1]);
        return ret;
    }

    public ResponseContext getEntry(RequestContext requestcontext) {
        Entry entry = (Entry) getAbderaEntry(requestcontext);
        if (entry != null) {
            Feed feed = entry.getParentElement();
            entry = (Entry) entry.clone();
            entry.setSource(feed.getAsSource());
            Document<Entry> entry_doc = entry.getDocument();
            return ProviderHelper.returnBase(entry_doc, 200, entry.getEdited()).setEntityTag(ProviderHelper.calculateEntityTag(entry));
        } else {
            return ProviderHelper.notfound(requestcontext);
        }
    }

    public enum IntalioFeeds {
        PROCESSES, TASKS, ALL
    }

    protected ITaskManagementService getClient(String token){
    	return new RemoteTMSFactory(Configuration.getInstance().getServiceEndpoint(), token).getService();
    }
    
    private Document<Feed> getFeedDocument(RequestContext context) throws ResponseContextException {

        Target target = context.getTarget();
        String collection = target.getParameter("collection");
        LOG.debug("Feed for collection:" + collection);

        // String priority = target.getParameter("priority");
        // String deadline = target.getParameter("deadline");
        // Locale preferredLocale = context.getPreferredLocale();

        String user = target.getParameter("user");
        String password = target.getParameter("password");
        String token = target.getParameter("token");

        Feed feed = createFeedBase(context);

        TokenClient tokenClient = Configuration.getInstance().getTokenClient();
        try {
            if (token != null) {
                Property[] props = tokenClient.getTokenProperties(token);
                user = (String) PropertyUtils.getProperty(props, AuthenticationConstants.PROPERTY_USER).getValue();
            } else if (user != null && password != null) {
                token = tokenClient.authenticateUser(user, password);
            } else
                throw new Exception("No credentials");

            feed.setSubtitle("This is a feed for the following user:" + user);
        } catch (Exception e1) {
            LOG.error("Credential exception", e1);
            throw new ResponseContextException(500, e1);
        }

        feed.setId("IntalioFEEDID");

        feed.setMustPreserveWhitespace(true);
        feed.setUpdated(new Date());
        try {
//            ITaskManagementService client = new RemoteTMSFactory(Configuration.getInstance().getServiceEndpoint(), token).getService();
        	ITaskManagementService client = getClient(token);
            if (collection.equalsIgnoreCase(IntalioFeeds.PROCESSES.name())) {
                feed.setTitle("Intalio Processes");
                addTasksToFeed(context, feed, client.getAvailableTasks(PIPATask.class.getSimpleName(), null), token, user);
            } else if (collection.equalsIgnoreCase(IntalioFeeds.TASKS.name())) {
                feed.setTitle("Intalio Tasks");
                addTasksToFeed(context, feed, client.getAvailableTasks(PATask.class.getSimpleName(), "T._state <> TaskState.COMPLETED"), token, user);
                addTasksToFeed(context, feed, client.getAvailableTasks(Notification.class.getSimpleName(), "T._state <> TaskState.COMPLETED"), token, user);
            } else if (collection.equalsIgnoreCase(IntalioFeeds.ALL.name())) {
                feed.setTitle("Full Intalio Feeds");
                addTasksToFeed(context, feed, client.getTaskList(), token, user);
            } else
                throw new Exception("Invalid collection requestsed");

            feed.addCategory(collection);
            feed.addLink(Configuration.getInstance().getFeedUrl());
            feed.setBaseUri(Configuration.getInstance().getFeedUrl());
            
        } catch (Exception e) {
            LOG.error("Feed exception", e);
            throw new ResponseContextException(500, e);
        }

        if (LOG.isDebugEnabled())
            try {
                feed.writeTo(System.out);
            } catch (IOException e) {
            }
        return feed.getDocument();
    }

    private void addTasksToFeed(RequestContext context, Feed feed, Task[] tasks, String token, String user) throws Exception {
        for (Task t : tasks) {
            Entry entry = feed.addEntry();
            entry.setId(t.getID());
            entry.setTitle(t.getDescription());
            entry.setUpdated(new Date());
            setLinkForTask(t, token, context, entry, user);
        }
    }

    private void setLinkForTask(Task t, String ticket, RequestContext context, Entry e, String user) throws Exception {
        Factory factory = context.getAbdera().getFactory();
        Link link = factory.newLink();
        String formLink = URIUtils.getFormURLForTask(_manager, t, ticket, user);
        // if the URL of the form manager are relative URLs, we want to have a
        // proper link back to the machine.
        // localhost won't work for most RSS readers, so using loopback ip
        // address in this very specific case
        String href = formLink;
        if (!formLink.toLowerCase().startsWith("http")) { // relative URL
            href = Configuration.getInstance().getFeedItemBaseUrl() + href;
        }
        link.setHref(href);
        link.setTitle("Link to " + t.getDescription());
        link.setText("Link to " + t.getDescription());
        e.addLink(link);
    }

    public ResponseContext getFeed(RequestContext requestcontext) {
        Document<Feed> feed;
        try {
            feed = getFeedDocument(requestcontext);
        } catch (ResponseContextException e) {
            return ProviderHelper.servererror(requestcontext, e);
        }
        return ProviderHelper.returnBase(feed, 200, new Date());
    }

    /**
     * Not supported yet
     */
    public ResponseContext postEntry(RequestContext requestcontext) {
        return ProviderHelper.unavailable(requestcontext);
    }

    /**
     * Not supported yet
     */
    public ResponseContext putEntry(RequestContext requestcontext) {
        return ProviderHelper.unavailable(requestcontext);
    }

    public String getTitle(RequestContext requestcontext) {
        return null;
    }

    public ResponseContext getCategories(RequestContext request) {
        return new StreamWriterResponseContext(request.getAbdera()) {
            protected void writeTo(StreamWriter sw) throws IOException {
                sw.startDocument().startCategories(false).writeCategory(PIPATask.class.getSimpleName()).writeCategory(PATask.class.getSimpleName())
                                .writeCategory(Notification.class.getSimpleName()).endCategories().endDocument();
            }
        }.setStatus(200).setContentType(Constants.CAT_MEDIA_TYPE);
    }

}
