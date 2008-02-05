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

package org.intalio.tempo.workflow.wds.core.tms;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import static org.intalio.tempo.workflow.wds.WDSUtil.*;
import org.intalio.tempo.workflow.wds.core.Item;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.AfterSpecification;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Specification;


@RunWith(InstinctRunner.class)
public class PipaJpaTest {
    final static Logger log = LoggerFactory.getLogger(PipaJpaTest.class);
    final static ExpectThat expect = new ExpectThatImpl();

    EntityManager em;
    EntityManagerFactory factory;
    EntityTransaction jpa;

    @BeforeSpecification
    public void setUpEntityManager() throws Exception {
        factory = Persistence.createEntityManagerFactory("org.intalio.tempo", getJpaProperties());
        em = factory.createEntityManager();
        jpa = em.getTransaction();
    }

    @AfterSpecification
    public void closeEntityManager() {
        try {em.close();} catch (Exception e) {}
        try {factory.close();} catch (Exception e) {}
    }

    private void persist(Object o) {
    	jpa.begin();
        em.persist(o);
        jpa.commit();
        em.clear();
    }
    
    @Specification
    public void TheSamePIPACanBeRetrievedAfterBeingStored() {
        PipaTask task1 = getSamplePipa();
        persist(task1);
        Query q = em.createNamedQuery(PipaTask.FIND_BY_ID).setParameter(1, task1.getId());
        PipaTask task2 = (PipaTask)(q.getSingleResult());
        
        expect.that(task1).isEqualTo(task2);
    }
    
    @Specification
    public void TheSameItemCanBeRetrievedAfterBeingStored() throws Exception {
        Item i1 = getSampleItem();
        persist(i1);
        Query q = em.createNamedQuery(Item.FIND_BY_URI).setParameter(1, i1.getURI());
        Item i2 = (Item)q.getSingleResult();
        
        expect.that(i1).isEqualTo(i2);
    }
    
    @Specification 
    public void ExpectOnlyOneItem() throws Exception {
        Query q = em.createNamedQuery(Item.COUNT_FOR_URI).setParameter(1, "http://www.hellonico.net");
        
        expect.that(q.getSingleResult()).isEqualTo(1l);
    }
}
