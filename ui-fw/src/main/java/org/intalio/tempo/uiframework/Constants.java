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
 * $Id: XFormsManager.java 2764 2006-03-16 18:34:41Z ozenzin $
 * $Log:$
 */
package org.intalio.tempo.uiframework;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public class Constants {
    public static final String ONE_TASK_VIEW = "atask";
    public static final String TASKS_VIEW = "tasks";
    public static final String TASKS_UPDATE_VIEW = "updates";

    public static final String TASKS_URL = "tasks.htm";

    public static final ModelAndView REDIRECTION_TO_TASKS = new ModelAndView(new RedirectView(TASKS_URL));

}
