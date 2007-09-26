package org.intalio.tempo.web;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public class Constants {
	public static final String LOGIN_VIEW = "login";
    public static final String ERROR_VIEW = "genError";
	
    public static final String LOGIN_URL = "login.htm";
    public static final String ERROR_URL = "genError.htm";
    
    public static final String BPMS_VERSION_PROP = "bpms-version";
    public static final String BPMS_BUILD_NUMBER_PROP = "bpms-build-number";
    
    public static final ModelAndView REDIRECTION_TO_LOGIN = new ModelAndView(new RedirectView(LOGIN_URL));
    
}
