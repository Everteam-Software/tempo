package org.intalio.tempo.uiframework.service;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.forms.FormManagerBroker;
import org.intalio.tempo.uiframework.forms.GenericFormManager;
import org.intalio.tempo.workflow.auth.AuthException;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.Task;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class JsonUpdateTest {
    final static ExpectThat expect = new ExpectThatImpl();

    @Subject
    JsonUpdate servlet;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @BeforeSpecification
    public void before() {
        servlet = new JsonUpdate(){
            @Override
            protected Task[] getTasks(HttpServletRequest request, String participantToken, String taskType, String subQuery) throws AuthException {
                Task[] tasks = new Task[3];
                try{
                    Task notification = new Notification("notification", new URI("http://localhost:8080/notification"));
                    tasks[0] = notification;
                    
                    Task paTask = new PATask("patask", new URI("http://localhost:8080/patask"));
                    tasks[1] = paTask;
                    
                    Task pipaTask = new PIPATask("pipatask", new URI("http://localhost:8080/pipatask"));
                    tasks[2] = pipaTask;
                }catch(Exception e){
                    Assert.fail("URI incorrect " + e.getMessage());
                }
                return tasks;
            }
        };
    }

    @Specification
    public void testDoPost() throws Exception {
        CharArrayWriter caw = new CharArrayWriter();
        final PrintWriter pw = new PrintWriter(caw);
        final String token = "token";
        final String user = "user";
        final String taskType = null;
        final String description = null;
        final String requestScheme = "";
        final String serverName = "localhost";
        final int serverPort = 8080;

        expect.that(new Expectations() {
            {
                atLeast(1).of(response).setContentType("application/x-json");

                atLeast(1).of(request).getParameter("token");
                will(returnValue(token));

                atLeast(1).of(request).getParameter("user");
                will(returnValue(user));

                atLeast(1).of(request).getParameter("taskType");
                will(returnValue(taskType));

                atLeast(1).of(request).getParameter("description");
                will(returnValue(description));

                atLeast(1).of(request).getScheme();
                will(returnValue(requestScheme));

                atLeast(1).of(request).getServerName();
                will(returnValue(serverName));

                atLeast(1).of(request).getServerPort();
                will(returnValue(serverPort));

                atLeast(1).of(response).getWriter();
                will(returnValue(pw));
            }
        });

        Configuration.getInstance().setServiceEndpoint("http://localhost:8080/xxx");
        GenericFormManager genericFormManager = new GenericFormManager();
        FormManagerBroker.getInstance().setFormManager(genericFormManager);
        Map<String, Map<String, String>> mappings = new HashMap<String, Map<String, String>>();
        
        java.util.HashMap<String, String> notification = new HashMap<String, String>();
        notification.put(".*xform", "/xFormsManager/notification");
        
        java.util.HashMap<String, String> pipa = new HashMap<String, String>();
        pipa.put(".*xform", "/xFormsManager/init");
        
        java.util.HashMap<String, String> pa = new HashMap<String, String>();
        pa.put(".*xform", "/xFormsManager/pa");

        mappings.put(GenericFormManager.NOTIFICATION, notification);
        mappings.put(GenericFormManager.PIPA, pipa);
        mappings.put(GenericFormManager.PA, pa);
        genericFormManager.setMappings(mappings);
        servlet.doPost(request, response);
        String expected = "{\"tasks\":[{\"taskUrl\":\"http://localhost:8080/notification?id=notification&type=Notification&url";
        Assert.assertTrue(caw.toString().startsWith(expected));

    }

}
