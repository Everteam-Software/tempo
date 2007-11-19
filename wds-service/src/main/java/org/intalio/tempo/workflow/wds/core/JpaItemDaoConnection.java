package org.intalio.tempo.workflow.wds.core;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class JpaItemDaoConnection implements ItemDaoConnection {
    private EntityManager entityManager;
    
    public JpaItemDaoConnection(EntityManager createEntityManager) {
        this.entityManager = createEntityManager;
    }

    public void close() {
        entityManager.close();
    }

    public void commit() {
        entityManager.getTransaction().commit();
    }

    public void deleteItem(String uri) throws UnavailableItemException {
        Query q = entityManager.createNamedQuery(Item.FIND_BY_URI).setParameter(1, uri);
        try {
            Item i = (Item)q.getResultList().get(0);
            entityManager.remove(i);
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
        entityManager.persist(item);
    }

}
