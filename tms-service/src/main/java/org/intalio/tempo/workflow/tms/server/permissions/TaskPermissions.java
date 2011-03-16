package org.intalio.tempo.workflow.tms.server.permissions;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskPermissions {

    public static final Logger LOG = LoggerFactory.getLogger(TaskPermissions.class);
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_READ = "read";

    private Map<String, Set<String>> _permissions;

    public TaskPermissions(Map<String, Set<String>> permissions) {
        _permissions = permissions;
    }

    /**
     * check if the action is possible on the task with the given credentials
     */
    public boolean isAuthorized(String action, Task t, UserRoles credentials) {

        if (!_permissions.containsKey(action))
            return false;
        Set<String> roles = _permissions.get(action);
        LOG.info("Checking for action:"+action+" on credentials:"+credentials.toString()+" where allowed roles are:"+roles);
        if (roles.contains(credentials.getUserID()))
            return true;
        if (CollectionUtils.containsAny(roles, credentials.getAssignedRoles()))
            return true;
        return false;
    }
    
    public boolean isAuthroized(String action,UserRoles credentials){
        if (!_permissions.containsKey(action))
            return false;
        Set<String> roles = _permissions.get(action);
        LOG.info("Checking for action:"+action+" on credentials:"+credentials.toString()+" where allowed roles are:"+roles);
        if (roles.contains(credentials.getUserID()))
            return true;
        if (CollectionUtils.containsAny(roles, credentials.getAssignedRoles()))
            return true;
        return false;
        
        
    }
}
