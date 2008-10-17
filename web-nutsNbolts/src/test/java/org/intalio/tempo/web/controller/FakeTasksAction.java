package org.intalio.tempo.web.controller;

import org.springframework.web.servlet.ModelAndView;

public class FakeTasksAction extends Action {

    @Override
    public ModelAndView execute() {
        if (Boolean.valueOf(_request.getParameter("update"))) {
            return new ModelAndView("updates", createModel());
        } else {
            return new ModelAndView("tasks", createModel());
        }
    }

    @Override
    public ModelAndView getErrorView() {
        return new ModelAndView("tasks", createModel());
    }

}
