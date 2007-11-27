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

package org.intalio.tempo.workflow.task.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;

import com.intalio.bpms.workflow.taskManagementServices20051109.Task;
import com.intalio.bpms.workflow.taskManagementServices20051109.TaskMetadata;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

class TestUtils {

	private TestUtils() {

	}

	public static OMElement loadElementFromResource(String resource)
			throws Exception {
		InputStream requestInputStream = TestUtils.class
				.getResourceAsStream(resource);
		if (requestInputStream == null)
			throw new IllegalStateException("Missing resource: " + resource);

		XMLStreamReader parser = XMLInputFactory.newInstance()
				.createXMLStreamReader(requestInputStream);
		StAXOMBuilder builder = new StAXOMBuilder(parser);

		return builder.getDocumentElement();
	}

	public static XmlObject loadXmlObjectFromResource(String resource) throws Exception {
		InputStream requestInputStream = TestUtils.class
				.getResourceAsStream(resource);
		if (requestInputStream == null)
			throw new IllegalStateException("Missing resource: " + resource);

		return XmlObject.Factory.parse(requestInputStream);
	}
	
	public static Task loadTaskFromResource(String resource) throws Exception {
		InputStream requestInputStream = TestUtils.class
				.getResourceAsStream(resource);
		if (requestInputStream == null)
			throw new IllegalStateException("Missing resource: " + resource);
		XmlOptions opt = new XmlOptions();
		return Task.Factory.parse(requestInputStream, opt);
	}

	public static TaskMetadata loadTaskMetadataFromResource(String resource) throws Exception {
		InputStream requestInputStream = TestUtils.class
				.getResourceAsStream(resource);
		if (requestInputStream == null)
			throw new IllegalStateException("Missing resource: " + resource);

		return TaskMetadata.Factory.parse(requestInputStream);
	}

	public static String toPrettyXML(OMElement element) throws Exception {
		String uglyString = element.toString();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(uglyString
				.getBytes("UTF-8")));

		OutputFormat format = new OutputFormat(doc);
		format.setEncoding("UTF-8");
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		XMLSerializer serializer = new XMLSerializer(outputStream, format);
		serializer.serialize(doc);

		return new String(outputStream.toByteArray(), "UTF-8");
	}

	public static String toPrettyXML(XmlObject element) throws Exception {
		HashMap suggestedPrefixes = new HashMap();
		suggestedPrefixes
				.put(TaskXMLConstants.TASK_NAMESPACE, TaskXMLConstants.TASK_NAMESPACE_PREFIX);
		XmlOptions opts = new XmlOptions();
		opts.setSaveSuggestedPrefixes(suggestedPrefixes);

		String uglyString = element.xmlText(opts);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(uglyString
				.getBytes("UTF-8")));

		OutputFormat format = new OutputFormat(doc);
		format.setEncoding("UTF-8");
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		XMLSerializer serializer = new XMLSerializer(outputStream, format);
		serializer.serialize(doc);

		return new String(outputStream.toByteArray(), "UTF-8");
	}

	public static Document createXMLDocument() throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document doc = builder.newDocument();
		doc.appendChild(doc.createElement("testDocument"));

		return doc;
	}
}
