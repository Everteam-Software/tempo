package org.intalio.tempo.web.controller;

import javax.servlet.http.Cookie;
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
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.ModelAndView;


public class LoginControllerTest extends TestCase {
    private HttpServletRequest request = new MockHttpServletRequest();
    private HttpServletResponse response = new MockHttpServletResponse();
    private XmlWebApplicationContext context;
    private MockServletContext msc;
    private LoginController loginController;

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
    public void testAlreadyLogin() throws Exception {
        FakeUIFWApplicationState state = new FakeUIFWApplicationState();
        User currentUser = new User("test1", new String[] { "test/test1" }, "token1");
        state.setCurrentUser(currentUser);
        request.getSession().setAttribute("APPLICATION_STATE", state);
        LoginCommand loginCom = new LoginCommand();
        loginController = (LoginController) context.getBean("loginController");
        ModelAndView mav = loginController.logIn(request, response, loginCom, new BindException(currentUser, "test"));
        Assert.assertTrue(mav.getView().toString().contains("tasks.htm"));
    }

    @Test
    public void testLoginInvalidUser() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginCom.setUsername("test2");
        loginCom.setPassword("no");
        loginController = (LoginController) context.getBean("loginController");
        ModelAndView mav = loginController.logIn(request, response, loginCom, new BindException(loginCom, "test"));
        Assert.assertEquals(mav.getViewName(), Constants.LOGIN_VIEW);
    }

    @Test
    public void testLoginValidUser() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginCom.setUsername("test1");
        loginCom.setPassword("yes");
        loginController = (LoginController) context.getBean("loginController");
        ModelAndView mav = loginController.logIn(request, response, loginCom, new BindException(loginCom, "test"));
        Assert.assertTrue(mav.getView().toString().contains("tasks.htm"));
    }

    @Test
    public void testLogout() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginCom.setUsername("test1");
        loginCom.setPassword("yes");
        loginController = (LoginController) context.getBean("loginController");
        loginController.logIn(request, response, loginCom, new BindException(loginCom, "test"));
        ModelAndView mav = loginController.logOut(request, response, loginCom, new BindException(loginCom, "test"));
        Assert.assertEquals(mav.getViewName(), Constants.LOGIN_VIEW);
    }

    @Test
    public void testLoginValidUserAutoLogin() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginCom.setUsername("test1");
        loginCom.setPassword("yes");
        loginCom.setAutoLogin(true);
        loginController = (LoginController) context.getBean("loginController");
        ModelAndView mav = loginController.logIn(request, response, loginCom, new BindException(loginCom, "test"));
        Assert.assertTrue(mav.getView().toString().contains("tasks.htm"));
    }

    @Test
    public void testLoginValidatorPass() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginCom.setUsername("test1");
        loginCom.setPassword("yes");
        loginController = (LoginController) context.getBean("loginController");
        Validator validator = loginController.getValidator();
        BindException errors = new BindException(loginCom, "test");
        validator.validate(loginCom, errors);
        assertEquals(errors.getErrorCount(), 0);
    }

    @Test
    public void testLoginValidatorBlankName() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginCom.setPassword("yes");
        loginController = (LoginController) context.getBean("loginController");
        Validator validator = loginController.getValidator();
        BindException errors = new BindException(loginCom, "test");
        validator.validate(loginCom, errors);
        assertEquals(errors.getErrorCount(), 1);
    }

    @Test
    public void testLoginValidatorLongName() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginCom.setUsername("test1test1test1test1test1test1test1test1test1test1test1test1test1"
                        + "test1test1test1test1test1test1test1test1test1test1test1test1test1test1test1");
        loginCom.setPassword("yes");
        loginController = (LoginController) context.getBean("loginController");
        Validator validator = loginController.getValidator();
        BindException errors = new BindException(loginCom, "test");
        validator.validate(loginCom, errors);
        assertEquals(errors.getErrorCount(), 1);
    }

    @Test
    public void testLoginValidatorBlankPassword() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginCom.setUsername("test1");
        loginController = (LoginController) context.getBean("loginController");
        Validator validator = loginController.getValidator();
        BindException errors = new BindException(loginCom, "test");
        validator.validate(loginCom, errors);
        assertEquals(errors.getErrorCount(), 1);
    }

    @Test
    public void testLoginValidatorLongPassword() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginCom.setUsername("test1");
        loginCom.setPassword("passwordpasswordpasswordpasswordpasswordpasswordpasswordpasswordpassword");
        loginController = (LoginController) context.getBean("loginController");
        Validator validator = loginController.getValidator();
        BindException errors = new BindException(loginCom, "test");
        validator.validate(loginCom, errors);
        assertEquals(errors.getErrorCount(), 1);
    }

    @Test
    public void testShowFormValidUser() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginCom.setUsername("test1");
        loginCom.setPassword("yes");
        loginController = (LoginController) context.getBean("loginController");
        loginController.logIn(request, response, loginCom, new BindException(loginCom, "test"));
        ModelAndView mav = loginController.showForm(request, response, new BindException(loginCom, "test"));
        Assert.assertTrue(mav.getView().toString().contains("tasks.htm"));
    }

    @Test
    public void testShowFormInvalidUser() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginCom.setUsername("test2");
        loginCom.setPassword("yes");
        loginController = (LoginController) context.getBean("loginController");
        loginController.logIn(request, response, loginCom, new BindException(loginCom, "test"));
        ModelAndView mav = loginController.showForm(request, response, new BindException(loginCom, "test"));
        Assert.assertEquals(mav.getViewName(), Constants.LOGIN_VIEW);
    }

    @Test
    public void testShowFormWithoutUser() throws Exception {
        LoginCommand loginCom = new LoginCommand();
        loginController = (LoginController) context.getBean("loginController");
        ModelAndView mav = loginController.showForm(request, response, new BindException(loginCom, "test"));
        Assert.assertEquals(mav.getViewName(), Constants.LOGIN_VIEW);
    }

    @Test
    public void testLoginPageURL() throws Exception {
        loginController = (LoginController) context.getBean("loginController");
        loginController.setLoginPageURL("/testlogin.htm");
        assertEquals(loginController.getLoginPageURL(), "/testlogin.htm");
    }

    @Test
    public void testSecureRadom() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        loginController = (LoginController) context.getBean("loginController");
        LoginController.generateSecureRandom(req, res);
        Cookie[] cookies = res.getCookies();
        boolean contains = false;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase("SECURE_RANDOM") && cookie.getValue().length() > 0) {
                contains = true;
                LoginController.setSecureRandomSession(req, cookie.getValue());
            }
        }
        assertTrue(contains);

        String random = LoginController.getSecureRandomCookie(request);
        assertNull(random);

        assertTrue(LoginController.getSecureRandomSession(req).length() > 0);
    }
    
    @Test
    public void testGetCurrentUser() throws Exception {
        //TODO need to know how to set cookie to mockhttpservletrequest
    }
    
    @Test
    public void testSetRedirectAfterLogin() throws Exception {
        MockHttpServletResponse res = new MockHttpServletResponse();
        loginController = (LoginController) context.getBean("loginController");
        loginController.setRedirectAfterLoginCookie(res);
        Cookie[] cookies = res.getCookies();
        boolean contains = false;
        String url = null;
        for (Cookie cookie : cookies){
            if (cookie.getName().equalsIgnoreCase("redirectAfterLogin")){
                contains = true;
                url = cookie.getValue();
            }
        }
        assertTrue(contains);
        assertEquals(url, "tasks.htm");
    }
}
