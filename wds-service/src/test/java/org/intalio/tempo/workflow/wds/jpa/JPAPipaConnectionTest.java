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

package org.intalio.tempo.workflow.wds.jpa;

import java.util.Properties;
import java.util.ResourceBundle;

import static org.intalio.tempo.workflow.wds.WDSUtil.*;
import org.intalio.tempo.workflow.wds.core.tms.PipaTask;
import org.intalio.tempo.workflow.wds.core.tms.TMSJPAConnection;
import org.intalio.tempo.workflow.wds.core.tms.TMSJPAConnectionFactory;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Specification;

@RunWith(InstinctRunner.class)
public final class JPAPipaConnectionTest {
    private TMSJPAConnection jpac;
    public static final ExpectThat expect = new ExpectThatImpl();

    @BeforeSpecification
    void createConnection() {
    	Properties map = convertBundleToProperties(ResourceBundle.getBundle("jpa"));
    	TMSJPAConnectionFactory factory = new TMSJPAConnectionFactory(map);
    	jpac = factory.openConnection();
    }

    @Specification
    void JPADoesNotThrowAnExceptionWhenDeletingANonExistingItem() {
    	jpac.deletePipaTask("Iamnothere");
    }
    
    @Specification
    void JPACanStoreAPipaTask() {
    	PipaTask task1 = new PipaTask();
        task1.setId("abc");
        task1.setFormNamespace("urn:ns");
        task1.setFormURL("http://localhost/");
        task1.setProcessEndpoint("http://localhost/process");
        task1.setInitSoapAction("initProcess");
        
        jpac.storePipaTask(task1);
    }

}