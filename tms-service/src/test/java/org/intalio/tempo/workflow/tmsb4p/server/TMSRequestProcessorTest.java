package org.intalio.tempo.workflow.tmsb4p.server;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.xmlbeans.XmlObject;
import org.intalio.tempo.workflow.auth.AuthIdentifierSet;
import org.intalio.tempo.workflow.auth.SimpleAuthProvider;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.tms.server.Utils;
import org.intalio.tempo.workflow.tms.server.permissions.TaskPermissions;
import org.intalio.tempo.workflow.tmsb4p.server.dao.JPATaskDaoConnectionFactory;

import com.intalio.wsHT.api.xsd.ActivateDocument;
import com.intalio.wsHT.api.xsd.ActivateResponseDocument;
import com.intalio.wsHT.api.xsd.ClaimDocument;
import com.intalio.wsHT.api.xsd.ClaimResponseDocument;
import com.intalio.wsHT.api.xsd.CompleteDocument;
import com.intalio.wsHT.api.xsd.CompleteResponseDocument;
import com.intalio.wsHT.api.xsd.CreateResponseDocument;
import com.intalio.wsHT.api.xsd.FailDocument;
import com.intalio.wsHT.api.xsd.FailResponseDocument;
import com.intalio.wsHT.api.xsd.ReleaseDocument;
import com.intalio.wsHT.api.xsd.ReleaseResponseDocument;
import com.intalio.wsHT.api.xsd.StartDocument;
import com.intalio.wsHT.api.xsd.StartResponseDocument;
import com.intalio.wsHT.api.xsd.StopDocument;
import com.intalio.wsHT.api.xsd.StopResponseDocument;
import com.intalio.wsHT.api.xsd.CompleteDocument.Complete;


public class TMSRequestProcessorTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
    
    public static TaskPermissions getMeADefaultPermissionHandler() {
        Map<String, Set<String>> permissions = new HashMap<String, Set<String>>();
        AuthIdentifierSet deletePermissions = new AuthIdentifierSet();
        deletePermissions.add("test/system-user");
        permissions.put(TaskPermissions.ACTION_DELETE, deletePermissions);
        return new TaskPermissions(permissions);
    }

    public static SimpleAuthProvider getMeASimpleAuthProvider() {
        UserRoles user1 = new UserRoles("test/user1", new String[] { "test/role1", "test/role2" });
        UserRoles user2 = new UserRoles("test/user2", new String[] { "test/role2", "test/role3" });
        UserRoles user3 = new UserRoles("test/user3", new String[] { "test/role4", "test/role5" });
        UserRoles systemUser = new UserRoles("test/system-user", new String[] { "test/role1", "test/role2","test/role3", "examples/employee", "*/*"});
        UserRoles systemUser2 = new UserRoles("intalio/manager", new String[] { "test/role1", "test/role2","test/role3", "examples/employee", "*/*"});
        UserRoles systemUser3 = new UserRoles("intalio/admin", new String[] { "test/role1", "test/role2","test/role3", "examples/employee", "*/*"});

        SimpleAuthProvider authProvider = new SimpleAuthProvider();
        authProvider.addUserToken("token1", user1);
        authProvider.addUserToken("token2", user2);
        authProvider.addUserToken("token3", user3);
        authProvider.addUserToken("system-user-token", systemUser);
        authProvider.addUserToken("intalio/manager", systemUser2);
        authProvider.addUserToken("intalio/admin", systemUser3);
        return authProvider;
    }
    
    private TMSRequestProcessor createRequestProcessorJPA() throws Exception {
        ITMSServer server =  new TMSServer(getMeASimpleAuthProvider(), new JPATaskDaoConnectionFactory(), getMeADefaultPermissionHandler());   
        TMSRequestProcessor proc = new TMSRequestProcessor(){
            protected String getParticipantToken() throws AxisFault{
               // return "VE9LRU4mJnVzZXI9PWFkbWluJiZpc3N1ZWQ9PTExODA0NzY2NjUzOTMmJnJvbGVzPT1pbnRhbGlvXHByb2Nlc3NhZG1pbmlzdHJhdG9yLGV4YW1wbGVzXGVtcGxveWVlLGludGFsaW9ccHJvY2Vzc21hbmFnZXIsZXhhbXBsZXNcbWFuYWdlciYmZnVsbE5hbWU9PUFkbWluaW5pc3RyYXRvciYmZW1haWw9PWFkbWluQGV4YW1wbGUuY29tJiZub25jZT09NDMxNjAwNTE5NDM5MTk1MDMzMyYmdGltZXN0YW1wPT0xMTgwNDc2NjY1Mzk1JiZkaWdlc3Q9PTVmM1dQdDBXOEp2UlpRM2gyblJ6UkRrenRwTT0mJiYmVE9LRU4";
                return "intalio/manager";
            }
        };
        proc.setServer(server);
        return proc;
    }
    public static OMElement loadElementFromResource(String resource) throws Exception {
        InputStream requestInputStream = Utils.class.getResourceAsStream(resource);

        XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(requestInputStream);
        StAXOMBuilder builder = new StAXOMBuilder(parser);

        return builder.getDocumentElement();
    }
    
    private String getTaskId(OMElement res) throws Exception{
        CreateResponseDocument doc = CreateResponseDocument.Factory.parse(res.getXMLStreamReader());
        String out = doc.getCreateResponse().getOut();
        String taskId = out.substring(1, out.length()-1);
        System.out.println("task id: "+ taskId);
        return taskId;
    }
    
    private String createTask(String reqFile) throws Exception{
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();
        OMElement requestElement = loadElementFromResource(reqFile);
        OMElement  res = tmsRP.create(requestElement);
        return getTaskId(res);
    }
    
    private OMElement genOMElement(XmlObject xmlObject){
        OMElement dm = null;
        InputStream is = xmlObject.newInputStream();
        StAXOMBuilder builder;
        try {
            builder = new StAXOMBuilder(is);
            dm = builder.getDocumentElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dm;
    }
    
    /************************************************************
     * Test case for participant Operation
     *************************************************************/
    public void testCreate() throws Exception{
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();
        OMElement requestElement = loadElementFromResource("/B4PRequest/create.xml");
        OMElement  res = tmsRP.create(requestElement);
        System.out.println("taskid:"+getTaskId(res));
        
    }
    
    public void testClaim() throws Exception{
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();
        
        // create task
        String taskId = createTask("/B4PRequest/create.xml");
        
        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        tmsRP.activate(genOMElement(activateReq));

        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        OMElement  res = tmsRP.claim(requestElement);
        ClaimResponseDocument resDoc = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
       
        System.out.println("response:"+ resDoc.xmlText());
        
    }
    
    
    public void testStart() throws Exception{
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();
        
        // create task
        String taskId = createTask("/B4PRequest/create.xml");
        
        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement  res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ activateRes.xmlText());
        
        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ claimRes.xmlText());
        
        // start
        StartDocument startReq = StartDocument.Factory.newInstance();
        startReq.addNewStart().setIdentifier(taskId);
         res = tmsRP.start(genOMElement(startReq));
         StartResponseDocument startRes = StartResponseDocument.Factory.parse(res.getXMLStreamReader());
        
       
        System.out.println("response:"+ startRes.xmlText());
        
    }
    
    
    public void testStop() throws Exception{
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();
        
        // create task
        String taskId = createTask("/B4PRequest/create.xml");
        
        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement  res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ activateRes.xmlText());
        
        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ claimRes.xmlText());
        
        // start
        StartDocument startReq = StartDocument.Factory.newInstance();
        startReq.addNewStart().setIdentifier(taskId);
         res = tmsRP.start(genOMElement(startReq));
         StartResponseDocument startRes = StartResponseDocument.Factory.parse(res.getXMLStreamReader());
       
        System.out.println("response:"+ startRes.xmlText());
        
        // stop
        StopDocument stopReq = StopDocument.Factory.newInstance();
        stopReq.addNewStop().setIdentifier(taskId);
         res = tmsRP.stop(genOMElement(stopReq));
         StopResponseDocument stopRes = StopResponseDocument.Factory.parse(res.getXMLStreamReader());

        System.out.println("response:"+ stopRes.xmlText());     
    }
    
    public void testComplete() throws Exception{
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();
        
        // create task
        String taskId = createTask("/B4PRequest/create.xml");
        
        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement  res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ activateRes.xmlText());
        
        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ claimRes.xmlText());
        
        // start
        StartDocument startReq = StartDocument.Factory.newInstance();
        startReq.addNewStart().setIdentifier(taskId);
         res = tmsRP.start(genOMElement(startReq));
         StartResponseDocument startRes = StartResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ startRes.xmlText());
       
        System.out.println("response:"+ startRes.xmlText());      
        // Complete
        CompleteDocument completeReq = CompleteDocument.Factory.newInstance();
        Complete complete = completeReq.addNewComplete();
        complete.setIdentifier(taskId);
        XmlObject taskData = XmlObject.Factory.parse("<out>sample output</out>");
        complete.setTaskData(taskData);
         res = tmsRP.complete(genOMElement(completeReq));
         CompleteResponseDocument completeRes = CompleteResponseDocument.Factory.parse(res.getXMLStreamReader());

        System.out.println("response:"+ completeRes.xmlText());     
    }
    
    public void testFail() throws Exception{
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();
        
        // create task
        String taskId = createTask("/B4PRequest/create.xml");
        
        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement  res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ activateRes.xmlText());
        
        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ claimRes.xmlText());
        
        // start
        StartDocument startReq = StartDocument.Factory.newInstance();
        startReq.addNewStart().setIdentifier(taskId);
         res = tmsRP.start(genOMElement(startReq));
         StartResponseDocument startRes = StartResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ startRes.xmlText());
       
        
        // fail
        FailDocument failReq = FailDocument.Factory.newInstance();
        failReq.addNewFail().setIdentifier(taskId);
        res = tmsRP.fail(genOMElement(failReq));
        FailResponseDocument failRes = FailResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ failRes.xmlText());     
    }
    
    public void testRelease() throws Exception{
        TMSRequestProcessor tmsRP = createRequestProcessorJPA();
        
        // create task
        String taskId = createTask("/B4PRequest/create.xml");
        
        // activate task
        ActivateDocument activateReq = ActivateDocument.Factory.newInstance();
        activateReq.addNewActivate().setIdentifier(taskId);
        OMElement  res = tmsRP.activate(genOMElement(activateReq));
        ActivateResponseDocument activateRes = ActivateResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ activateRes.xmlText());
        
        // claim
        ClaimDocument doc = ClaimDocument.Factory.newInstance();
        doc.addNewClaim().setIdentifier(taskId);
        OMElement requestElement = genOMElement(doc);
        res = tmsRP.claim(requestElement);
        ClaimResponseDocument claimRes = ClaimResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ claimRes.xmlText());
     
        // release
        ReleaseDocument releaseReq = ReleaseDocument.Factory.newInstance();
        releaseReq.addNewRelease().setIdentifier(taskId);
        res = tmsRP.release(genOMElement(releaseReq));
        ReleaseResponseDocument releaseRes = ReleaseResponseDocument.Factory.parse(res.getXMLStreamReader());
        System.out.println("response:"+ releaseRes.xmlText());     
    }
    
    /************************************************************
     * Test case for Task Manipulation Operation
     *************************************************************/
    
    
    /************************************************************
     * Test case for admin/query Operation
     *************************************************************/

}
