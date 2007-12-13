/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.tms.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.intalio.tempo.workflow.auth.SimpleAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.tms.server.dao.ITaskDAOConnectionFactory;
import org.intalio.tempo.workflow.tms.server.dao.SimpleTaskDAOConnectionFactory;
import org.w3c.dom.Document;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class Utils {

    public static OMElement loadElementFromResource(String resource)
            throws Exception {
        InputStream requestInputStream = Utils.class.getResourceAsStream(resource);

        XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(requestInputStream);
        StAXOMBuilder builder = new StAXOMBuilder(parser);

        return builder.getDocumentElement();
    }

    public static String toPrettyXML(OMElement element)
            throws Exception {
        String uglyString = element.toString();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(uglyString.getBytes()));

        OutputFormat format = new OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        XMLSerializer serializer = new XMLSerializer(outputStream, format);
        serializer.serialize(doc);

        return new String(outputStream.toByteArray(), "UTF-8");
    }

    public static Document createXMLDocument()
            throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.newDocument();
        doc.appendChild(doc.createElement("testDocument"));

        return doc;
    }

    public static ITMSServer createTMSServer()
            throws Exception {
        UserRoles user1 = new UserRoles("test/user1", new String[] { "test/role1", "test/role2" });
        UserRoles user2 = new UserRoles("test/user2", new String[] { "test/role2", "test/role3" });
        UserRoles user3 = new UserRoles("test/user3", new String[] { "test/role4", "test/role5" });
        UserRoles systemUser = new UserRoles("test/system-user", new String[] {});
        systemUser.assignSystemRole();

        SimpleAuthProvider authProvider = new SimpleAuthProvider();
        authProvider.addUserToken("token1", user1);
        authProvider.addUserToken("token2", user2);
        authProvider.addUserToken("token3", user3);
        authProvider.addUserToken("system-user-token", systemUser);

        ITaskDAOConnectionFactory daoFactory = new SimpleTaskDAOConnectionFactory();

        return new TMSServer(authProvider, daoFactory);
    }
}
