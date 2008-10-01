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

import java.net.URI;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.wds.core.Item;

public class WDSUtil {
    static final Random rand = new Random();

    public static Properties convertBundleToProperties(ResourceBundle rb) {
        Properties props = new Properties();
        for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
            String key = (String) keys.nextElement();
            props.put(key, rb.getString(key));
        }
        return props;
    }

    public static Map<?, ?> getJpaProperties() {
        return convertBundleToProperties(ResourceBundle.getBundle("jpa"));
    }

    public static PIPATask getSamplePipa() {
        PIPATask task1 = new PIPATask("abc", "http://localhost/" + rand.nextInt());
        task1.setInitMessageNamespaceURI(URI.create("urn:ns"));
        task1.setProcessEndpointFromString("http://localhost/process" + rand.nextInt());
        task1.setInitOperationSOAPAction("initProcess" + rand.nextInt());
        return task1;
    }

    public static Item getSampleItem() {
        return new Item("AbscentRequest", "meta" + rand.nextInt(), new byte[] { 1, 2, 3 });
    }
    
    public static Item getXformItem() {
        return new Item("http://www.task.xform", "meta" + rand.nextInt(), new byte[] { 1, 2, 3 });
    }
}