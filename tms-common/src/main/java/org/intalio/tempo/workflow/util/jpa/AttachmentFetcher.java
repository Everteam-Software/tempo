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
import javax.persistence.Query;

import org.intalio.tempo.workflow.task.attachments.Attachment;

/**
 * Fix for WF-1479 
 * */

public class AttachmentFetcher {
    
    private Query find_by_attachment_url;
    private EntityManager _entityManager;
    
    public AttachmentFetcher(EntityManager em){
        _entityManager = em;
        find_by_attachment_url = _entityManager.createNamedQuery(Attachment.FIND_BY_URL);
    }
    
    public Attachment fetchAttachmentIfExists(String url){
        Query q = find_by_attachment_url.setParameter(1, url);
        List<Attachment> resultList = q.getResultList();
        
        return resultList.get(0);
    }
}
