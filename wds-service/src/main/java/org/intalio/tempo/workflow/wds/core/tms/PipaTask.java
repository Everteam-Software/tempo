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
 */
package org.intalio.tempo.workflow.wds.core.tms;

import java.util.Arrays;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;

/**
 * Encapsulates all (relevant) properties of a PIPA task.
 * 
 * @author Iwan Memruk
 * @version $Revision: 40 $
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name=PipaTask.FIND_BY_ID, 
            query="select m from PipaTask m where m._id=?1", 
            hints={ @QueryHint  (name="openjpa.hint.OptimizeResultCount", value="1")})
    })
public class PipaTask {

    static final String FIND_BY_ID = "find_by_id";
    
    /**
     * Task identifier.
     */
    @Basic
    @Column(name="tid")
    private String _id;

    /**
     * A human-readable task description (caption).
     */
    @Basic
    @Column(name="description")
    private String _description = "";

    /**
     * Task user owner identifiers, such as "group&#0092;user".
     */
    @Lob
    @Column(name="user_owners")
    private String[] _userOwners = {};

    /**
     * Task role owner identifiers, such as "group&#0092;role".
     */
    @Lob
    @Column(name="role_owners")
    private String[] _roleOwners = {};

    /**
     * Task form URL.
     */
    @Basic
    @Column(name="form_url")
    private String _formURL;

    /**
     * The endpoint of the business process to start when the task form is submitted.
     */
    @Basic
    @Column(name="process_endpoint")
    private String _processEndpoint;

    /**
     * Form namespace.
     */
    @Basic
    @Column(name="form_namespace")
    private String _formNamespace;

    /**
     * The SOAPAction to start the business process with.
     */
    @Basic
    @Column(name="init_soap_action")
    private String _initSoapAction;

    /**
     * Instance constructor. <br />
     * Note: after construction, the instance is not valid (the {@link #isValid()} method will return <code>false</code>).
     * <br />
     * You have to initialize at least the following properties:
     * <ul>
     * <li>id</li>
     * <li>formURL</li>
     * <li>processEndpoint</li>
     * <li>formNamespace</li>
     * <li>initSoapAction</li>
     * </ul>
     * Other properties are set to valid defaults.
     */
    public PipaTask() {

    }

    /**
     * Returns <code>true</code> if this instance has all necessary properties specified.<br />
     * Note: any instance is (purposely) invalid after initialization. See {@link #PipaTask()} for details.
     * 
     * @return <code>true</code> if this instance has all necessary properties specified.
     */
    public boolean isValid() {
        return (_id != null) && (_formURL != null) && (_processEndpoint != null) && (_formNamespace != null)
                && (_initSoapAction != null) && (_description != null) && (_userOwners != null)
                && (_roleOwners != null);
    }

    /**
     * Used in {@link #toString()} to represent various properties in a generic way.
     * 
     * @param builder
     *            The <code>StringBuilder</code> to output to.
     * @param caption
     *            Name of the property to output.
     * @param value
     *            Value of the property to output.
     */
    private static void fieldToString(StringBuilder builder, String caption, String value) {
        builder.append(caption);
        builder.append(": '");
        builder.append(value);
        builder.append("'\n");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        PipaTask.fieldToString(builder, "Valid?", String.valueOf(this.isValid()));
        PipaTask.fieldToString(builder, "ID", _id);
        PipaTask.fieldToString(builder, "Description", _description);
        PipaTask.fieldToString(builder, "Total user owners", String.valueOf(_userOwners.length));
        for (String userOwner : _userOwners) {
            PipaTask.fieldToString(builder, "User owner", userOwner);
        }
        PipaTask.fieldToString(builder, "Total role owners", String.valueOf(_roleOwners.length));
        for (String roleOwner : _roleOwners) {
            PipaTask.fieldToString(builder, "Role owner", roleOwner);
        }
        PipaTask.fieldToString(builder, "Form URL", _formURL);
        PipaTask.fieldToString(builder, "Process endpoint", _processEndpoint);
        PipaTask.fieldToString(builder, "Form namespace", _formNamespace);
        PipaTask.fieldToString(builder, "Init SOAPAction", _initSoapAction);
        return builder.toString();
    }

    /**
     * Returns the human-readable task description (caption).
     * 
     * @return The human-readable task description (caption).
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Sets the human-readable task description (caption).
     * 
     * @param description
     *            The human-readable task description (caption).
     */
    public void setDescription(String description) {
        _description = description;
    }

    /**
     * Returns the task form namespace.
     * 
     * @return The task form namespace.
     */
    public String getFormNamespace() {
        return _formNamespace;
    }

    /**
     * Sets the task form namespace.
     * 
     * @param formNamespace
     *            The task form namespace.
     */
    public void setFormNamespace(String formNamespace) {
        _formNamespace = formNamespace;
    }

    /**
     * Returns the task form URL.
     * 
     * @return The task form URL.
     */
    public String getFormURL() {
        return _formURL;
    }

    /**
     * Sets the task form URL.
     * 
     * @param formUrl
     *            The task form URL.
     */
    public void setFormURL(String formUrl) {
        _formURL = formUrl;
    }

    /**
     * Returns the task identifier.
     * 
     * @return The task identifier.
     */
    public String getId() {
        return _id;
    }

    /**
     * Sets the task identifier.
     * 
     * @param id
     *            The task identifier.
     */
    public void setId(String id) {
        _id = id;
    }

    /**
     * Returns the SOAPAction used to trigger the business process to start.
     * 
     * @return The SOAPAction used to trigger the business process to start.
     */
    public String getInitSoapAction() {
        return _initSoapAction;
    }

    /**
     * Sets the SOAPAction used to trigger the business process to start.
     * 
     * @param initSoapAction
     *            The SOAPAction used to trigger the business process to start.
     */
    public void setInitSoapAction(String initSoapAction) {
        _initSoapAction = initSoapAction;
    }

    /**
     * Returns the endpoint used to trigger the business process to start.
     * 
     * @return The endpoint used to trigger the business process to start.
     */
    public String getProcessEndpoint() {
        return _processEndpoint;
    }

    /**
     * Sets the endpoint used to trigger the business process to start.
     * 
     * @param processEndpoint
     *            The endpoint used to trigger the business process to start.
     */
    public void setProcessEndpoint(String processEndpoint) {
        _processEndpoint = processEndpoint;
    }

    /**
     * Returns the array of task role owner identifiers, such as "group&#0092;role". <br />
     * Note: the delimiter character in identifiers returned by this method is always the backslash (&#0092;).
     * 
     * @return The array of task role owner identifiers, such as "group&#0092;role".
     */
    public String[] getRoleOwners() {
        return _roleOwners;
    }

    /**
     * Normalizes an array of auth (user or role) identifiers, by replacing all allowed delimeter characters (the
     * forward slash, the backslash and the period) with a single delimeter character (the backslash).
     * 
     * @param sourceIdentifiers
     *            An array of auth (user or role) identifiers, such as "group&#0092;user", "group/user", "group.user".
     * @return The array of the same length which contains the same identifiers, normalized, such as "group&#0092;user".
     */
    private static String[] normalizeAuthIdentifiers(String[] sourceIdentifiers) {
        String[] result = new String[sourceIdentifiers.length];
        for (int i = 0; i < sourceIdentifiers.length; ++i) {
            result[i] = sourceIdentifiers[i].replace('/', '\\').replace('.', '\\');
        }
        return result;
    }

    /**
     * Sets the role owners of the task. <br />
     * Note: all allowed delimeter characters (the forward slash, the backslash and the period) are replaced with a
     * unified delimeter -- the backslash.
     * 
     * @param roleOwners
     *            An array of role identifiers, such as "group&#0092;role", "group/role" or "group.role".
     */
    public void setRoleOwners(String[] roleOwners) {
        if (roleOwners != null) {
            _roleOwners = PipaTask.normalizeAuthIdentifiers(roleOwners);
        } else {
            _roleOwners = null;
        }
    }

    /**
     * Returns the array of task user owner identifiers, such as "group&#0092;user". <br />
     * Note: the delimiter character in identifiers returned by this method is always the backslash (&#0092;).
     * 
     * @return The array of task user owner identifiers, such as "group&#0092;user".
     */    
    public String[] getUserOwners() {
        return _userOwners;
    }

    /**
     * Sets the ruser owners of the task. <br />
     * Note: all allowed delimeter characters (the forward slash, the backslash and the period) are replaced with a
     * unified delimeter -- the backslash.
     * 
     * @param userOwners
     *            An array of user identifiers, such as "group&#0092;user", "group/user" or "group.user".
     */
    public void setUserOwners(String[] userOwners) {
        if (userOwners != null) {
            _userOwners = PipaTask.normalizeAuthIdentifiers(userOwners);
        } else {
            _userOwners = null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PipaTask)) 
            return false;
        PipaTask task2 = (PipaTask)obj;
        return 
         task2._id.equals(_id)  
        && task2._description.equals(_description) 
        && task2._formNamespace.equals(_formNamespace) 
        && task2._formURL.equals(_formURL)
        && task2._initSoapAction.equals(_initSoapAction) 
        && task2._processEndpoint.equals(_processEndpoint) 
        && Arrays.equals(task2._roleOwners,_roleOwners) 
        && Arrays.equals(task2._userOwners,_userOwners);
    }
}
