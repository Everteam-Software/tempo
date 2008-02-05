/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */

package org.intalio.tempo.workflow.wds.core;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.intalio.tempo.workflow.dao.AbstractJPAConnection;

/**
 * JPA-based item persistence
 */
public class JPAItemDaoConnection extends AbstractJPAConnection implements ItemDaoConnection {

    public JPAItemDaoConnection(EntityManager createEntityManager) {
    	super(createEntityManager);
    }

    public void deleteItem(String uri) throws UnavailableItemException {
        Query q = entityManager.createNamedQuery(Item.FIND_BY_URI).setParameter(1, uri);
        try {
            Item item = (Item)(q.getResultList().get(0));
            checkTransactionIsActive();
            entityManager.remove(item);	
            commit();
        } catch (Exception e) {
            throw new UnavailableItemException(e);
        }
    }

    public boolean itemExists(String uri) {
        Query q = entityManager.createNamedQuery(Item.COUNT_FOR_URI).setParameter(1, uri);
        return ((Long)q.getSingleResult()) > 0;
    }

    public Item retrieveItem(String uri) throws UnavailableItemException {
        Query q = entityManager.createNamedQuery(Item.FIND_BY_URI).setParameter(1, uri);
        try {
            return  (Item)q.getResultList().get(0);    
        } catch (Exception e) {
            throw new UnavailableItemException(e);
        }
    }

    public void storeItem(Item item) throws UnavailableItemException {
    	checkTransactionIsActive();
        entityManager.persist(item);
        commit();
    }

}
