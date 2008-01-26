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
 */
package org.intalio.tempo.workflow.wds.cli;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import org.intalio.tempo.workflow.wds.client.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.validation.Schema;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

/**
 * Implements the command-line interface (CLI) for REST WDS client.
 *
 * @author Iwan Memruk
 * @version $Revision: 595 $
 */
public class WDSCommandLineClient {

    private static Logger _log = (Logger) Logger.getLogger(WDSCommandLineClient.class);

    private static final String DEPLOYMENT_DESCRIPTOR_SCHEMA =  "deployment.xsd";

    private static final String OXF_PREFIX =  "oxf://";

    /**
     * Defines the set of required methods for CLI command handling.
     */
    public interface CommandHandler {
        void handleCommand(WDSClient client, String uri, String fileName,
                           String contentType, String descriptorFileName, boolean force)
                throws Exception;
    }

    /**
     * Contains known CLI command handlers.
     */
    private static Map<String, CommandHandler> _commandHandlers;

    static {
        _commandHandlers = new HashMap<String, CommandHandler>();
        _commandHandlers.put("store", new StoreCommandHandler());
        _commandHandlers.put("store-activity", new StoreActivityCommandHandler());
        _commandHandlers.put("store-pipa", new StorePipaCommandHandler());
        _commandHandlers.put("create-pipa", new CreatePipaCommandHandler());
        _commandHandlers.put("retrieve", new RetrieveCommandHandler());
        _commandHandlers.put("delete", new DeleteCommandHandler());
        _commandHandlers.put("delete-pipa", new DeletePIPACommandHandler());
        _commandHandlers.put("delete-activity", new DeleteXFormCommandHandler());
        _commandHandlers = Collections.unmodifiableMap(_commandHandlers);
    }

    /**
     * Handles the "store" command.
     */
    public static class StoreCommandHandler implements CommandHandler {

        public void handleCommand(WDSClient client, String uri, String fileName,
                                  String contentType, String descriptorFileName, boolean force)
                throws Exception {

            if (fileName == null) {
                throw new Exception("A file name is required");
            }
            _log.debug("Storing the file " + fileName);
            client.storeFile(uri, new File(fileName), contentType);
            _log.debug("success");
        }

    }

    public static class StorePipaCommandHandler implements CommandHandler {

        public void handleCommand(WDSClient client, String uri, String fileName,
                                  String contentType, String descriptorFileName, boolean force)
                throws Exception {

            if (fileName == null || descriptorFileName == null) {
                throw new IllegalArgumentException("Please specify a form file and a descriptor");
            }

            if (!force) {
                try {
                    client.retrieveItem(uri).close();
                    throw new Exception("URI '" + uri + "' is already in use. " +
                            "Please specify --force option if you still want to overwrite it.");
                } catch (UnavailableItemException e) {
                    // OK
                }
            }

            WDSCommandLineClient.createAndStorePipaTask(client, descriptorFileName);

            File formFile = new File(fileName);
            _log.debug("Storing the form " + fileName);
            client.storeXForm(uri, formFile);
            _log.debug("success");
            WDSCommandLineClient.storeSchema(client, uri, formFile);
        }

    }

    public static class CreatePipaCommandHandler implements CommandHandler {

        public void handleCommand(WDSClient client, String uri, String fileName,
                                  String contentType, String descriptorFileName, boolean force)
                throws Exception {

            WDSCommandLineClient.createAndStorePipaTask(client, descriptorFileName);
        }

    }

    public static class StoreActivityCommandHandler implements CommandHandler { // FIXME: this is copy-pasted!

        public void handleCommand(WDSClient client,
                                  String uri,
                                  String fileName,
                                  String contentType,
                                  String descriptorFileName, boolean force)
                throws Exception {
            if (fileName == null) {
                throw new Exception("Please specify a form file");
            }

            if (!force) {
                try {
                    client.retrieveItem(uri).close();
                    throw new Exception("This URI is already taken. Please use the --force option to overwrite it.");
                } catch (UnavailableItemException e) {
                    // OK
                }
            }

            File formFile = new File(fileName);

            _log.debug("Storing the form " + fileName);
            client.storeXForm(uri, formFile);
            _log.debug("success");
            WDSCommandLineClient.storeSchema(client, uri, formFile);
        }

    }

    /**
     * Handles the "retrieve" command.
     */
    public static class RetrieveCommandHandler implements CommandHandler {

        public void handleCommand(WDSClient client,
                                  String uri,
                                  String fileName,
                                  String contentType,
                                  String descriptorFileName, boolean force)
                throws Exception {
            if (fileName != null) {
                File file = new File(fileName);
                file.createNewFile();
                client.retrieveItemToFile(uri, file);
            } else {
                InputStream dataStream = client.retrieveItem(uri);
                IOUtils.copy(dataStream, System.out);
                dataStream.close();
            }
        }

    }

    /**
     * Handles the "delete" command.
     */
    public static class DeleteCommandHandler implements CommandHandler {

        public void handleCommand(WDSClient client, String uri, String fileName,
                                  String contentType, String descriptorFileName, boolean force)
                throws Exception {

            _log.debug("Deleting resource " + client.getWdsUrl() + uri);
            client.deleteItem(uri);
            _log.debug("success");
        }

    }

    public static class DeleteXFormCommandHandler implements CommandHandler {

        public void handleCommand(WDSClient client, String uri, String fileName,
                                  String contentType, String descriptorFileName, boolean force)
                throws Exception {

            InputStream formInputStream = null;
            try {
                formInputStream = client.retrieveItem(uri);
                InputSource descriptorSource = new InputSource(formInputStream);
                DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
                builder.setNamespaceAware(true);
                DocumentBuilder parser = builder.newDocumentBuilder();
                Document xform = parser.parse(descriptorSource);

                XPath xpath = XPathFactory.newInstance().newXPath();
                xpath.setNamespaceContext(new NC(new Object[] {"xforms"}, new String[] {"http://www.w3.org/2002/xforms"}));

                String schemaUrl = (String) xpath.evaluate("//xforms:model/@schema", xform, XPathConstants.STRING);

                client.deleteItem(uri);

                if (schemaUrl != null) {
                    if (schemaUrl.startsWith(OXF_PREFIX)) {
                        schemaUrl = schemaUrl.substring(OXF_PREFIX.length());
                    }

                    _log.debug("Deleting schema: '" + schemaUrl + "'");
                    client.deleteItem(schemaUrl);
                    _log.debug("success");
                }
            } finally {
                if (formInputStream != null) {
                    formInputStream.close();
                }
            }
        }

    }

    public static class DeletePIPACommandHandler implements CommandHandler {

        public void handleCommand(WDSClient client, String uri, String fileName,
                                  String contentType, String descriptorFileName, boolean force) throws Exception {

            PipaTask pipaTask = WDSCommandLineClient.parsePIPA(descriptorFileName);
            client.deletePIPA(pipaTask);
        }

    }

    /**
     * Auxiliary class, serves here for XPath handling
     *
     * @author Oleg Zenzin
     */
    private static class NC implements NamespaceContext {

        private Object[] _prefixes;

        private String[] _namespaceURIs;

        /*
         * prefixes <-> names as many <-> one
         */
        NC(Object[] prefixes, String[] namespaceURIs) {
            assert prefixes != null && namespaceURIs != null;
            _prefixes = prefixes;
            _namespaceURIs = namespaceURIs; 
        }

        public String getNamespaceURI(String prefix) {
            assert prefix != null;
            int n = _prefixes.length;

        SEEK_THE_PREFIX:
            while (n-- > 0) {
                if (_prefixes[n] instanceof String[]) {
                    for(String pfx : (String[]) _prefixes[n]) {
                        if (prefix.equals(pfx)) {
                            break SEEK_THE_PREFIX;
                        }
                    }
                } else if (prefix.equals(_prefixes[n])) {
                    break;
                }
            }

            return (n < 0) ? null : _namespaceURIs[n];
        }

        public String getPrefix(String namespaceURI) {
            assert namespaceURI != null;

            int n = _namespaceURIs.length;
            while (n-- > 0) {
                if (namespaceURI.equals(_namespaceURIs[n])) {
                    break;
                }
            }

            return (n < 0) ? null :// not found
                    (_prefixes[n] instanceof String[]) ?//is there multiple prefixes for this namespace? 
                            ((String[]) _prefixes[n])[0] : (String) _prefixes[n];
        }

        public Iterator getPrefixes(String namespaceURI) {
            assert namespaceURI != null;

            int n = _namespaceURIs.length;
            while (n-- > 0) {
                if (namespaceURI.equals(_namespaceURIs[n])) {
                    break;
                }
            }

            return (n < 0) ? null : Arrays.asList(_prefixes[n]).iterator();
        }
    }

    /**
     * Outputs the usage syntax for CLI.
     */
    public static void printUsage() {
        _log.info("Usage: wds-cli [-t,--token participant_token] [-d,--descr deployment_descriptor] "
                + "[-w,--wds wds_base_url] [-c,--contentType contentType]" + " command [uri] [filename]\n" +
                "Available commands are: " + _commandHandlers.keySet() + "\n" +
                "Filename is only required for store and optional for retrieve (stdout implied).");
    }

    /**
     * Main entry point for CLI.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        try {
            CmdLineParser cmdLineParser = new CmdLineParser();
            Option participantTokenOption = cmdLineParser.addStringOption('t', "token");
            Option wdsUrlOption = cmdLineParser.addStringOption('w', "wds");
            Option contentTypeOption = cmdLineParser.addStringOption('c', "contentType");
            Option descriptorOption = cmdLineParser.addStringOption('d', "descr");
            Option forceOption = cmdLineParser.addBooleanOption("force");
            cmdLineParser.parse(args);

            String[] remainingArgs = cmdLineParser.getRemainingArgs();
            if (remainingArgs.length < 1) {
                _log.info("command & uri are required");
                printUsage();
            } else {
                String participantToken = (String) cmdLineParser.getOptionValue(participantTokenOption, "");
                String wdsUrl = (String) cmdLineParser.getOptionValue(wdsUrlOption, "http://localhost:8080/wds/");
                String contentType = (String) cmdLineParser.getOptionValue(contentTypeOption,
                        "application/octet-stream");
                String descriptorFileName = (String) cmdLineParser.getOptionValue(descriptorOption);
                boolean force = (Boolean) cmdLineParser.getOptionValue(forceOption, Boolean.FALSE);

                String command = remainingArgs[0];
                String uri = null;
                if (remainingArgs.length >= 2) {
                    uri = remainingArgs[1];
                }
                String fileName = null;
                if (remainingArgs.length >= 3) {
                    fileName = remainingArgs[2];
                }

                CommandHandler handler = _commandHandlers.get(command);
                if (handler == null) {
                    throw new Exception("Command '" + command + "' is unknown");
                }

                WDSClient client = new WDSClient(wdsUrl, participantToken);
                handler.handleCommand(client, uri, fileName, contentType, descriptorFileName, force);
            }
        } catch (CmdLineParser.OptionException e) {
            _log.info(e.getMessage());
            printUsage();
        } catch (Exception e) {
            _log.info("Exception happened", e);
        }
    }

    private static void validateDescriptor(Document document) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(ClassLoader.getSystemResourceAsStream(DEPLOYMENT_DESCRIPTOR_SCHEMA));
        Schema schema = factory.newSchema(schemaFile);

        Validator validator = schema.newValidator();

        validator.validate(new DOMSource(document));
    }

    private static void storeSchema(WDSClient client, String uri, File xformsFile) throws Exception {
        InputSource formSource = new InputSource(new FileInputStream(xformsFile));
        DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
        builder.setNamespaceAware(true);
        DocumentBuilder parser = builder.newDocumentBuilder();
        Document xform = parser.parse(formSource);

        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NC(new Object[] {"xforms"}, new String[] {"http://www.w3.org/2002/xforms"}));

        String schemaRef = (String) xpath.evaluate("//xforms:model/@schema", xform, XPathConstants.STRING);

        if (schemaRef != null) {
            _log.debug("Storing the schema: '" + schemaRef + "'");

            String wdsSchemaUri = schemaRef;
            int slashIndex = uri.lastIndexOf('/');
            if (slashIndex > 0) {
                wdsSchemaUri = uri.substring(0, slashIndex + 1) + wdsSchemaUri;
            }
            URI schemaUri = xformsFile.getAbsoluteFile().toURI().resolve(schemaRef);
            long schemaLength = new File(schemaUri).length();

            _log.debug("Storing to: '" + wdsSchemaUri + "'");
            client.storeItem(wdsSchemaUri, schemaUri.toURL().openConnection().getInputStream(), schemaLength);
            _log.debug("success");
        }
    }

    public static PipaTask parsePIPA(String descriptorFileName) throws Exception {
        if (descriptorFileName == null) {
            throw new Exception("Please specify a form file and a descriptor");
        }

        PipaTask pipaTask = new PipaTask();

        String taskId = new UIDGenerator().generateUID();

        pipaTask.setId(taskId);

        InputSource descriptorSource = new InputSource(new FileInputStream(descriptorFileName));
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = parser.parse(descriptorSource);

        validateDescriptor(document);

        XPath xpath = XPathFactory.newInstance().newXPath();


        pipaTask.setDescription((String) xpath.evaluate("/deployment/task-description/text()", document, XPathConstants.STRING));

        NodeList owners = (NodeList) xpath.evaluate("/deployment/task-user-owners/user", document, XPathConstants.NODESET);
        int n = owners.getLength();
        List<String> userOwners = new LinkedList<String>();
        for (int i = 0; i < n; i++ ) {
            Node user = owners.item(i);
            Node text = user.getFirstChild();
            if (text != null && !"".equals(text.getNodeValue().trim())) {
                userOwners.add(text.getNodeValue().trim());
            }
        }
        pipaTask.setUserOwners(userOwners.toArray(new String[] {}));

        owners = (NodeList) xpath.evaluate("/deployment/task-role-owners/role", document, XPathConstants.NODESET);
        n = owners.getLength();
        List<String> roleOwners = new LinkedList<String>();
        for (int i = 0; i < n; i++ ) {
            Node user = owners.item(i);
            Node text = user.getFirstChild();
            if (text != null && !"".equals(text.getNodeValue().trim())) {
                roleOwners.add(text.getNodeValue().trim());
            }
        }
        pipaTask.setRoleOwners(roleOwners.toArray(new String[] {}));

        pipaTask.setFormNamespace((String) xpath.evaluate("/deployment/formNamespace/text()", document, XPathConstants.STRING));

        String formURI = (String) xpath.evaluate("/deployment/formURI/text()", document, XPathConstants.STRING);
        if (! new URI(formURI).isAbsolute()) {
            while (formURI.startsWith("/")) formURI = formURI.substring(1);
            formURI = OXF_PREFIX + formURI;
        }
        pipaTask.setFormURL(formURI);

        pipaTask.setProcessEndpoint((String) xpath.evaluate("/deployment/processEndpoint/text()", document, XPathConstants.STRING));
        pipaTask.setInitSoapAction((String) xpath.evaluate("/deployment/userProcessInitSOAPAction/text()", document, XPathConstants.STRING));

        return pipaTask;
    }

    private static PipaTask createAndStorePipaTask(WDSClient client, String descriptorFileName) throws Exception {
        PipaTask pipaTask = parsePIPA(descriptorFileName);

        if (!pipaTask.isValid()) {
            throw new ValidationException("Invalid PIPA task:\n" + pipaTask);
        }

        _log.debug("Storing PIPA Task: " + pipaTask);
        client.storePipaTask(null, pipaTask);
        _log.debug("success");

        return pipaTask;
    }

    /**
     * Prevents instantiation.
     */
    private WDSCommandLineClient() {

    }
}
