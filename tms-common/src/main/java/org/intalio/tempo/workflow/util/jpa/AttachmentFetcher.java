/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.intalio.tempo.workflow.util.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import org.hibernate.Query;
import org.hibernate.Session;

import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.tms.UnavailableAttachmentException;

/**
 * Fix for WF-1479 
 * */

public class AttachmentFetcher {
    
    private Query find_by_attachment_url;
    private EntityManager _entityManager;
    
    public AttachmentFetcher(EntityManager em){
        _entityManager = em;
        find_by_attachment_url = em.unwrap(Session.class).getNamedQuery(Attachment.FIND_BY_URL);
    }
    
    public Attachment fetchAttachmentIfExists(String url) throws UnavailableAttachmentException{
        Query q = find_by_attachment_url.setParameter(0, url);
        List<Attachment> resultList = q.list();
        if (resultList.size() < 1)
            throw new UnavailableAttachmentException("Attachment does not exist"
                    + url);
        return resultList.get(0);
    }
}
