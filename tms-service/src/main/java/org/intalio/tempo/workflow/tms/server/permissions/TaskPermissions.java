package org.intalio.tempo.workflow.tms.server.permissions;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Task;

public class TaskPermissions {

    public static final String ACTION_DELETE = "delete";

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
        if (roles.contains(credentials.getUserID()))
            return true;
        if (CollectionUtils.containsAny(roles, credentials.getAssignedRoles()))
            return true;
        return false;
    }
}
