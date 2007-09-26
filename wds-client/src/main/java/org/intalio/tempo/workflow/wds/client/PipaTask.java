package org.intalio.tempo.workflow.wds.client;

public class PipaTask {

    private String _id;

    private String _description = "";

    private String[] _userOwners = {};

    private String[] _roleOwners = {};

    private String _formURL;

    private String _processEndpoint;

    private String _formNamespace;

    private String _initSoapAction;

    public PipaTask() {

    }

    public boolean isValid() {
        return !(_formURL == null || "".equals(_formURL.trim()) || _processEndpoint == null || "".equals(_processEndpoint.trim()) ||
                _formNamespace == null || "".equals(_formNamespace.trim()) || _initSoapAction == null || "".equals(_initSoapAction.trim()) ||
                _description == null  || "".equals(_description.trim()) || (_userOwners.length == 0 && _roleOwners.length == 0));
    }

    private static void fieldToString(StringBuilder builder, String caption, String value) {
        builder.append(caption);
        builder.append(": ");
        builder.append(value == null || "".equals(value.trim()) ? "EMPTY" : "'" + value + "'");
        builder.append("\n");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        PipaTask.fieldToString(builder, "ID", _id);
        PipaTask.fieldToString(builder, "Description", _description);
        for (String userOwner : _userOwners) {
            PipaTask.fieldToString(builder, "User owner", userOwner);
        }
        for (String roleOwner : _roleOwners) {
            PipaTask.fieldToString(builder, "Role owner", roleOwner);
        }
        PipaTask.fieldToString(builder, "Form URL", _formURL);
        PipaTask.fieldToString(builder, "Process endpoint", _processEndpoint);
        PipaTask.fieldToString(builder, "Form namespace", _formNamespace);
        PipaTask.fieldToString(builder, "Init SOAPAction", _initSoapAction);
        return builder.toString();
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getFormNamespace() {
        return _formNamespace;
    }

    public void setFormNamespace(String formNamespace) {
        _formNamespace = formNamespace;
    }

    public String getFormURL() {
        return _formURL;
    }

    public void setFormURL(String formUrl) {
        _formURL = formUrl;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getInitSoapAction() {
        return _initSoapAction;
    }

    public void setInitSoapAction(String initSoapAction) {
        _initSoapAction = initSoapAction;
    }

    public String getProcessEndpoint() {
        return _processEndpoint;
    }

    public void setProcessEndpoint(String processEndpoint) {
        _processEndpoint = processEndpoint;
    }

    public String[] getRoleOwners() {
        return _roleOwners;
    }

    private static String[] normalizeAuthIdentifiers(String[] sourceIdentifiers) {
        String[] result = new String[sourceIdentifiers.length];
        for (int i = 0; i < sourceIdentifiers.length; ++i) {
            result[i] = sourceIdentifiers[i].replace('/', '\\').replace('.', '\\');
        }
        return result;
    }

    public void setRoleOwners(String[] roleOwners) {
        if (roleOwners != null) {
            _roleOwners = PipaTask.normalizeAuthIdentifiers(roleOwners);
        } else {
            _roleOwners = null;
        }
    }

    public String[] getUserOwners() {
        return _userOwners;
    }

    public void setUserOwners(String[] userOwners) {
        if (userOwners != null) {
            _userOwners = PipaTask.normalizeAuthIdentifiers(userOwners);
        } else {
            _userOwners = null;
        }
    }
}
