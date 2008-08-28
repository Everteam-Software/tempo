package org.intalio.tempo.portlet;

import java.rmi.RemoteException;

import org.apache.pluto.wrappers.PortletRequestWrapper;
import org.intalio.tempo.uiframework.Configuration;
import org.intalio.tempo.uiframework.URIUtils;
import org.intalio.tempo.portlet.ApplicationState;
import org.intalio.tempo.portlet.Action;
import org.intalio.tempo.workflow.tms.ITaskManagementService;
import org.intalio.tempo.workflow.tms.client.RemoteTMSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.portlet.ModelAndView;

public class DeleteAllTasksAction extends Action {

    private static final Logger _log = LoggerFactory.getLogger(TasksAction.class);

    final Configuration conf = Configuration.getInstance();

    public void deleteAllTasks() {
        try {
            String pToken = getParticipantToken();
            ITaskManagementService taskManager = getTMS(pToken);
            taskManager.deleteAll("false", null, "Task");
        } catch (Exception e) {

        }
    }

    protected ITaskManagementService getTMS(String participantToken) throws RemoteException {
        String endpoint = URIUtils.resolveURI(new PortletRequestWrapper(_request), conf.getServiceEndpoint());
        return new RemoteTMSFactory(endpoint, participantToken).getService();
    }

    protected String getParticipantToken() {
        ApplicationState state = ApplicationState.getCurrentInstance(_request);
        return state.getCurrentUser().getToken();
    }

    @Override
    public ModelAndView execute() {
        deleteAllTasks();
        return new ModelAndView("view");
    }

    @Override
    public ModelAndView getErrorView() {
        return null;
    }

}
