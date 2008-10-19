package org.intalio.tempo.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.intalio.tempo.web.Constants;
import org.intalio.tempo.web.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.BindException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

public class SecuredControllerTest extends TestCase {
    private HttpServletRequest request = new MockHttpServletRequest();
    private HttpServletResponse response = new MockHttpServletResponse();
    private XmlWebApplicationContext context;
    private MockServletContext msc;
    private SecuredController securedController;

    protected void setUp() throws Exception {
        String[] contexts = new String[] { "tempo-ui-fw-servlet.xml", "tempo-ui-fw.xml" };
        context = new XmlWebApplicationContext();
        context.setConfigLocations(contexts);
        msc = new MockServletContext();
        context.setServletContext(msc);
        context.refresh();
        msc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
    }

    @Test
    public void testShowFormWithoutUser() throws Exception {
        securedController = (SecuredController) context.getBean("tasksController");
        ModelAndView mav = securedController.showForm(request, response, null);
        Assert.assertTrue(mav.getView().toString().contains(Constants.LOGIN_URL));
    }

    @Test
    public void testShowFormWithUser() throws Exception {
        FakeUIFWApplicationState state = new FakeUIFWApplicationState();
        User currentUser = new User("test1", new String[] { "test/test1" }, "token1");
        state.setCurrentUser(currentUser);
        request.getSession().setAttribute("APPLICATION_STATE", state);
        securedController = (SecuredController) context.getBean("tasksController");
        ModelAndView mav = securedController.showForm(request, response, new BindException(currentUser, "test"));
        Assert.assertEquals(mav.getViewName(), "tasks");
    }

    @Test
    public void testProcessFormSubmissionWithoutUser() throws Exception {
        securedController = (SecuredController) context.getBean("tasksController");
        ModelAndView mav = securedController.processFormSubmission(request, response, null, null);
        Assert.assertTrue(mav.getView().toString().contains(Constants.LOGIN_URL));
    }

    @Test
    public void testProcessFormSubmissionWithUser() throws Exception {
        FakeUIFWApplicationState state = new FakeUIFWApplicationState();
        User currentUser = new User("test1", new String[] { "test/test1" }, "token1");
        state.setCurrentUser(currentUser);
        request.getSession().setAttribute("APPLICATION_STATE", state);
        securedController = (SecuredController) context.getBean("tasksController");
        ModelAndView mav = securedController.processFormSubmission(request, response, null, new BindException(currentUser, "test"));
        Assert.assertEquals(mav.getViewName(), "tasks");
    }

    @Test
    public void testProcessFormSubmissionInvalidActionWithUser() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("actionName", "getTaskList");
        request = mockRequest;
        FakeUIFWApplicationState state = new FakeUIFWApplicationState();
        User currentUser = new User("test1", new String[] { "test/test1" }, "token1");
        state.setCurrentUser(currentUser);
        request.getSession().setAttribute("APPLICATION_STATE", state);
        securedController = (SecuredController) context.getBean("tasksController");
        try {
            ModelAndView mav = securedController.processFormSubmission(request, response, null, new BindException(currentUser, "test"));
            Assert.fail("Invalid Action exception expected");
        } catch (Exception e) {

        }

    }

    @Test
    public void testProcessFormSubmissionValidActionWithUser() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter("actionName", "default");
        request = mockRequest;
        FakeUIFWApplicationState state = new FakeUIFWApplicationState();
        User currentUser = new User("test1", new String[] { "test/test1" }, "token1");
        state.setCurrentUser(currentUser);
        request.getSession().setAttribute("APPLICATION_STATE", state);
        securedController = (SecuredController) context.getBean("tasksController");

        ModelAndView mav = securedController.processFormSubmission(request, response, null, new BindException(currentUser, "test"));
        Assert.assertEquals(mav.getViewName(), "tasks");
    }
    
    @Test
    public void testGetCurrentUser() throws Exception {
        FakeUIFWApplicationState state = new FakeUIFWApplicationState();
        User currentUser = new User("test1", new String[] { "test/test1" }, "token1");
        state.setCurrentUser(currentUser);
        request.getSession().setAttribute("APPLICATION_STATE", state);
        securedController = (SecuredController) context.getBean("tasksController");
        String userName = securedController.getCurrentUserName(request);
        Assert.assertEquals("test1", userName);
    }
}
