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
 * $Id: TaskManagementServicesFacade.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */

package org.intalio.tempo.workflow.task;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.OMNamespaceImpl;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentMap;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.xmlbeans.XmlException;
import javax.xml.stream.XMLStreamException;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.attachments.Attachment;
import org.intalio.tempo.workflow.task.traits.IChainableTask;
import org.intalio.tempo.workflow.task.traits.ICompleteReportingTask;
import org.intalio.tempo.workflow.task.traits.IInstanceBoundTask;
import org.intalio.tempo.workflow.task.traits.IProcessBoundTask;
import org.intalio.tempo.workflow.task.traits.ITaskWithAttachments;
import org.intalio.tempo.workflow.task.traits.ITaskWithDeadline;
import org.intalio.tempo.workflow.task.traits.ITaskWithInput;
import org.intalio.tempo.workflow.task.traits.ITaskWithOutput;
import org.intalio.tempo.workflow.task.traits.ITaskWithPriority;
import org.intalio.tempo.workflow.task.traits.ITaskWithState;
import org.intalio.tempo.workflow.task.xml.XmlTooling;
import org.intalio.tempo.workflow.util.RequiredArgumentException;
import org.w3c.dom.Document;

import com.intalio.gi.forms.tAmanagement.ActivityType;
import com.intalio.gi.forms.tAmanagement.ArrivalDepartureType;
import com.intalio.gi.forms.tAmanagement.DCType;
import com.intalio.gi.forms.tAmanagement.FormModel;
import com.intalio.gi.forms.tAmanagement.InspectionType;
import com.intalio.gi.forms.tAmanagement.impl.FormModelImpl;

/**
 * Activity task
 */
@Entity
@Table(name = "tempo_pa")
@NamedQueries( { @NamedQuery(name = PATask.FIND_BY_STATES, query = "select m from PATask m where m._state=?1", hints = { @QueryHint(name = "openjpa.hint.OptimizeResultCount", value = "1") }) })
public class PATask extends Task implements ITaskWithState, IProcessBoundTask,
		ITaskWithInput, ITaskWithOutput, ICompleteReportingTask,
		ITaskWithAttachments, IChainableTask, ITaskWithPriority,
		ITaskWithDeadline, IInstanceBoundTask {

	public static final String FIND_BY_STATES = "find_by_ps_states";
	public static final String FIND_BY_PA_USER_ROLE = "find_by_pa_user_role";
	public static final String FIND_BY_PA_USER_ROLE_GENERIC = "find_by_pa_user_role_generic";

	@Persistent
	@Column(name = "state")
	private TaskState _state = TaskState.READY;

	@Persistent(fetch = FetchType.LAZY)
	@Column(name = "failure_code")
	private String _failureCode = "";

	@Persistent(fetch = FetchType.LAZY)
	@Column(name = "failure_reason")
	private String _failureReason = "";

	@Persistent
	@Column(name = "complete_soap_action")
	private String _completeSOAPAction;

	@Persistent(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Lob
	@Column(name = "input_xml")
	private String _input;

	@Persistent(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Column(name = "output_xml")
	@Lob
	private String _output;

	@PersistentMap(keyCascade = CascadeType.ALL, elementCascade = CascadeType.ALL, keyType = String.class, elementType = Attachment.class, fetch = FetchType.LAZY)
	@MapKey(name = "payloadURLAsString")
	@ContainerTable(name = "tempo_attachment_map")
	private Map<String, Attachment> _attachments = new HashMap<String, Attachment>();

	@Persistent(fetch = FetchType.EAGER)
	@Column(name = "is_chained_before")
	private Boolean _isChainedBefore = false;

	@Persistent(fetch = FetchType.EAGER)
	@Column(name = "previous_task_id")
	private String _previousTaskID = null;

	@Persistent
	@Column(name = "deadline")
	private Date _deadline;

	@Persistent
	@Column(name = "priority")
	private Integer _priority;

	@Persistent
	@Column(name = "process_id")
	private String _processID;

	@Persistent
	@Column(name = "instance_id")
	private String _instanceId;

	/****************************/
	/** Begin Extra metadata for SITA **/
	/** Activity data */
	@Persistent
	@Column(name = "AircraftID")
	private String _AircraftID;
	
	@Persistent
	@Column(name = "startTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date _startTime;

	@Persistent
	@Column(name = "finishTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date _finishTime;

	@Persistent
	@Column(name = "releaseTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date _releaseTime;

	@Persistent
	@Column(name = "late")
	private Boolean _late;

	@Persistent
	@Column(name = "updateField")
	private Boolean _update;
	/********************************************/
	/** ArrivalDeparture data */
	@Persistent
	@Column(name = "ScheduledArrival")
	@Temporal(TemporalType.TIMESTAMP)
	private Date _ScheduledArrival;

	@Persistent
	@Column(name = "ScheduledDeparture")
	@Temporal(TemporalType.TIMESTAMP)
	private Date _ScheduledDeparture;

	@Persistent
	@Column(name = "EstimatedArrival")
	private Date _EstimatedArrival;

	@Persistent
	@Column(name = "EstimatedDeparture")
	private Date _EstimatedDeparture;

	@Persistent
	@Column(name = "ActualArrival")
	private Date _ActualArrival;

	@Persistent
	@Column(name = "ActualDeparture")
	private Date _ActualDeparture;

	@Persistent
	@Column(name = "ArrivalFlightNumber")
	private String _ArrivalFlightNumber;

	@Persistent
	@Column(name = "DepartureFlightNumber")
	private String _DepartureFlightNumber;

	/********************************************/
	/*** Inspection metadata */

	@Persistent
	@Column(name = "Stand")
	private String _Stand;

	@Persistent
	@Column(name = "InspectionType")
	private String _InspectionType;

	@Persistent
	@Column(name = "InspectionStatus")
	private String _InspectionStatus;

	@Persistent
	@Column(name = "resources")
	private String _resources;

	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	Collection<org.intalio.tempo.workflow.task.AssignedCoords> _assignedCoord;

	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	Collection<org.intalio.tempo.workflow.task.RTR> _RTR;

	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	Collection<org.intalio.tempo.workflow.task.AssignedMechanics> _assignedMechanics;

	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	Collection<org.intalio.tempo.workflow.task.AssignedAvionics> _assignedAvionics;
	/********************************************/
	/******** DC metadata **********/
	@Persistent
	@Column(name = "comments")
	private String _comments;

	/** End Extra metadata for SITA **/
	/****************************/

	public PATask() {
		super();
	}

	public PATask(String id, URI formURL) {
		super(id, formURL);
	}

	public PATask(String id, URI formURL, String processID,
			String completeSOAPAction, Document input) {
		super(id, formURL);
		this.setProcessID(processID);
		this.setCompleteSOAPAction(completeSOAPAction);
		if (input != null)
			this.setInput(input);
	}

	public String getProcessID() {
		return _processID;
	}

	public void setProcessID(String processID) {
		if (processID == null) {
			throw new RequiredArgumentException("processID");
		}
		_processID = processID;
	}

	public TaskState getState() {
		return _state;
	}

	public void setState(TaskState state) {
		if (state == null) {
			throw new RequiredArgumentException("state");
		}
		_state = state;
	}

	public String getFailureCode() {
		if (_state.equals(TaskState.FAILED)) {
			return _failureCode;
		} else {
			throw new IllegalStateException("Task ID '" + getID() + "': "
					+ "Attempt to get the failure code at task state " + _state);
		}
	}

	public void setFailureCode(String failureCode) {
		if (failureCode == null) {
			throw new RequiredArgumentException("failureCode");
		}

		if (_state.equals(TaskState.FAILED)) {
			_failureCode = failureCode;
		} else {
			throw new IllegalStateException("Task ID '" + getID() + "': "
					+ "Attempt to set the failure code at task state " + _state);
		}
	}

	public String getFailureReason() {
		if (_state.equals(TaskState.FAILED)) {
			return _failureReason;
		} else {
			throw new IllegalStateException("Task ID '" + getID() + "': "
					+ "Attempt to get the failure reason at task state "
					+ _state);
		}
	}

	public void setFailureReason(String failureReason) {
		if (failureReason == null) {
			throw new RequiredArgumentException("failureReason");
		}

		if (_state.equals(TaskState.FAILED)) {
			_failureReason = failureReason;
		} else {
			throw new IllegalStateException("Task ID '" + getID() + "': "
					+ "Attempt to set the failure reason at task state "
					+ _state);
		}
	}

	public String getCompleteSOAPAction() {
		return _completeSOAPAction;
	}

	public void setCompleteSOAPAction(String soapAction) {
		if (soapAction == null) {
			throw new RequiredArgumentException("soapAction");
		}
		_completeSOAPAction = soapAction;
	}

	public boolean isInputAvailable() {
		return _input != null;
	}

	public Document getInput() {
		if (!this.isInputAvailable()) {
			throw new IllegalStateException(
					"Task input not available (e.g. was not retrieved).");
		}
		return XmlTooling.deserializeDocument(_input);
	}

	public void setInput(Document inputDocument) {
		if (inputDocument == null) {
			throw new RequiredArgumentException("inputDocument");
		}
		_input = XmlTooling.serializeDocument(inputDocument);
	}

	public Document getOutput() {
		return XmlTooling.deserializeDocument(_output);
	}

	public String getInputAsXmlString() {
		return _input;
	}

	public String getOutputAsXmlString() {
		return _output;
	}

	public void setOutput(Document outputDocument) {
		if (outputDocument == null) {
			throw new RequiredArgumentException("outputDocument");
		}
		setOutput(XmlTooling.serializeDocument(outputDocument));
	}

	public Attachment addAttachment(Attachment attachment) {
		return _attachments.put(attachment.getPayloadURL().toExternalForm(),
				attachment);
	}

	public Attachment removeAttachment(URL attachmentURL) {
		return _attachments.remove(attachmentURL.toExternalForm());
	}

	public Collection<Attachment> getAttachments() {
		return Collections.unmodifiableCollection(_attachments.values());
	}

	public boolean isChainedBefore() {
		return _isChainedBefore;
	}

	public void setChainedBefore(boolean isChainedBefore) {
		if (!isChainedBefore) {
			_previousTaskID = null;
		} else {
			if (_previousTaskID == null) {
				throw new IllegalStateException(
						"Set previousTaskID before setting isChainedBefore to true");
			}
		}
		_isChainedBefore = isChainedBefore;
	}

	public String getPreviousTaskID() {
		return _previousTaskID;
	}

	public void setPreviousTaskID(String previousTaskID) {
		if (previousTaskID == null) {
			throw new RequiredArgumentException("previousTaskID");
		}
		_previousTaskID = previousTaskID;
	}

	public void setInput(String input) {
		_input = input;
	}

	public void setOutput(String output) {
		_output = output;
		// System.out.println(output);
		try {
			OMElement om = AXIOMUtil.stringToOM(output);
			om.setLocalName("xml-fragment");
			om.setNamespace(new OMNamespaceImpl("", "k"));
			FormModelImpl outputXML = (FormModelImpl) FormModel.Factory
					.parse(om.getXMLStreamReaderWithoutCaching());
			setMetadata(outputXML);
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// <xs:element type="xs:string" name="AircraftID" minOccurs="0"/>
	// <xs:element type="xs:time" name="startTime" minOccurs="0"/>
	// <xs:element type="xs:time" name="finishTime" minOccurs="0"/>
	// <xs:element type="xs:time" name="releaseTime" minOccurs="0"/>
	// <xs:element type="xs:boolean" name="late" minOccurs="0"/>
	// <xs:element type="xs:boolean" name="update" minOccurs="0"/>
	private void setMetadata(FormModelImpl outputXML) {
		/** calendar is an object for time+date calculation **/
		Calendar calendar = Calendar.getInstance();
		// System.out.println("setMEtadata1");
		/**************** Activity Data Departure DATA ****************/
		if (outputXML.getActivity() != null) {
			ActivityType activity = outputXML.getActivity();
			if (activity.xgetAircraftID() != null
					&& activity.xgetAircraftID().validate()
					&& activity.getAircraftID() != null) {
				set_AircraftID(activity.getAircraftID());
			}

			if (activity.xgetStartTime() != null
					&& activity.xgetStartTime().validate()
					&& activity.getStartTime() != null) {
				set_startTime(activity.getStartTime().getTime());
			}
			if (activity.xgetFinishTime() != null
					&& activity.xgetFinishTime().validate()
					&& activity.getFinishTime() != null) {
				set_finishTime(activity.getFinishTime().getTime());
			}
			if (activity.xgetReleaseTime() != null
					&& activity.xgetReleaseTime().validate()
					&& activity.getReleaseTime() != null) {
				set_releaseTime(activity.getReleaseTime().getTime());
			}
			if (activity.xgetLate() != null && activity.xgetLate().validate()) {
				set_late(activity.getLate());
			}
			if (activity.xgetUpdate() != null
					&& activity.xgetUpdate().validate()) {
				set_update(activity.getUpdate());
			}

		}
		/**************** Arrival Departure DATA ****************/
		if (outputXML.getArrivalDeparture() != null) {
			// System.out.println("setMEtadata12");
			ArrivalDepartureType arrival = outputXML.getArrivalDeparture();

			/**************** Arrival Departure DATA ****************/
			/** Scheduled departure */
			if (arrival.xgetScheduledDepartureDate() != null
					&& arrival.xgetScheduledDepartureDate().validate()
					&& arrival.getScheduledDepartureDate() != null
					&& arrival.xgetSTD() != null
					&& arrival.xgetSTD().validate() && arrival.getSTD() != null) {
				calendar = arrival.getScheduledDepartureDate();
				addTimeToDate(arrival.getSTD().getTime(), calendar);
				set_ScheduledDeparture(calendar.getTime());
			}

			/** scheduled arrival */

			if (arrival.xgetScheduledArrivalDate() != null
					&& arrival.xgetScheduledArrivalDate().validate()
					&& arrival.getScheduledArrivalDate() != null
					&& arrival.xgetSTA() != null
					&& arrival.xgetSTA().validate() && arrival.getSTA() != null) {
				calendar = arrival.getScheduledArrivalDate();
				addTimeToDate(arrival.getSTA().getTime(), calendar);
				set_ScheduledArrival(calendar.getTime());
			}
			/** Estimated departure */
			if (arrival.xgetEstimatedDepartureDate() != null
					&& arrival.xgetEstimatedDepartureDate().validate()
					&& arrival.getEstimatedDepartureDate() != null
					&& arrival.xgetETD() != null
					&& arrival.xgetETD().validate() && arrival.getETD() != null)

			{
				calendar = arrival.getEstimatedDepartureDate();
				addTimeToDate(arrival.getETD().getTime(), calendar);
				set_EstimatedDeparture(calendar.getTime());
			}
			/** Estimated Arrival */
			if (arrival.xgetEstimatedArrivalDate() != null
					&& arrival.xgetEstimatedArrivalDate().validate()
					&& arrival.getEstimatedArrivalDate() != null
					&& arrival.xgetETA() != null
					&& arrival.xgetETA().validate() && arrival.getETA() != null)

			{
				calendar = arrival.getEstimatedArrivalDate();
				addTimeToDate(arrival.getETA().getTime(), calendar);
				set_EstimatedArrival(calendar.getTime());
			}
			/** Actual departure */
			if (arrival.xgetActualDepartureDate() != null
					&& arrival.xgetActualDepartureDate().validate()
					&& arrival.getActualDepartureDate() != null
					&& arrival.xgetATD() != null
					&& arrival.xgetATD().validate() && arrival.getATD() != null)

			{
				calendar = arrival.getActualDepartureDate();
				addTimeToDate(arrival.getATD().getTime(), calendar);
				set_ActualDeparture(calendar.getTime());
			}
			/** Actual Arrival */
			if (arrival.xgetActualArrivalDate() != null
					&& arrival.xgetActualArrivalDate().validate()
					&& arrival.getActualArrivalDate() != null
					&& arrival.xgetATA() != null
					&& arrival.xgetATA().validate() && arrival.getATA() != null)

			{
				calendar = arrival.getActualArrivalDate();
				addTimeToDate(arrival.getATA().getTime(), calendar);
				set_ActualArrival(calendar.getTime());
			}

			/** Flight numbers */
			if (arrival.xgetArrivalFlightNumber() != null
					&& arrival.xgetArrivalFlightNumber().validate()
					&& arrival.getArrivalFlightNumber() != null) {
				set_ArrivalFlightNumber(arrival.getArrivalFlightNumber());
			}
			if (arrival.xgetDepartureFlightNumber() != null
					&& arrival.xgetDepartureFlightNumber().validate()
					&& arrival.getDepartureFlightNumber() != null) {
				set_DepartureFlightNumber(arrival.getArrivalFlightNumber());
			}

		}

		/**************** Inspection DATA ****************/
		if (outputXML.getInspection() != null) {
			InspectionType inspection = outputXML.getInspection();
			if (inspection.xgetStand() != null
					&& inspection.xgetStand().validate()
					&& inspection.getStand() != null) {
				set_Stand(inspection.getStand());
			}
			if (inspection.xgetInspectionType() != null
					&& inspection.xgetInspectionType().validate()
					&& inspection.getInspectionType() != null) {
				set_InspectionType(inspection.getInspectionType());
			}
			if (inspection.xgetInspectionStatus() != null
					&& inspection.xgetInspectionStatus().validate()
					&& inspection.getInspectionStatus() != null) {
				set_InspectionStatus(inspection.getInspectionStatus()
						.toString());
			}
			if (inspection.xgetResources() != null
					&& inspection.xgetResources().validate()
					&& inspection.getResources() != null) {
				set_resources(inspection.getResources());
			}
			/** Coords */
			com.intalio.gi.forms.tAmanagement.InspectionType.AssignedCoord[] coords = inspection
					.getAssignedCoordArray();
			set_assignedCoord(new ArrayList<org.intalio.tempo.workflow.task.AssignedCoords>());
			for (com.intalio.gi.forms.tAmanagement.InspectionType.AssignedCoord coord : coords) {
				org.intalio.tempo.workflow.task.AssignedCoords newCoord = new org.intalio.tempo.workflow.task.AssignedCoords();
				newCoord.setName(coord.getAssignedCoordName());
				get_assignedCoord().add(newCoord);
			}
			/** RTR */
			com.intalio.gi.forms.tAmanagement.InspectionType.RTR[] RTRs = inspection
					.getRTRArray();
			set_RTR(new ArrayList<org.intalio.tempo.workflow.task.RTR>());
			for (com.intalio.gi.forms.tAmanagement.InspectionType.RTR RTR : RTRs) {
				org.intalio.tempo.workflow.task.RTR newRTR = new org.intalio.tempo.workflow.task.RTR();
				newRTR.setRTRID(RTR.getRTRid());
				get_RTR().add(newRTR);
			}

			/** mechanics */
			com.intalio.gi.forms.tAmanagement.InspectionType.AssignedMechanics[] mechanics = inspection
					.getAssignedMechanicsArray();
			set_assignedMechanics(new ArrayList<org.intalio.tempo.workflow.task.AssignedMechanics>());
			for (com.intalio.gi.forms.tAmanagement.InspectionType.AssignedMechanics mechanic : mechanics) {
				org.intalio.tempo.workflow.task.AssignedMechanics newMechanic = new org.intalio.tempo.workflow.task.AssignedMechanics();
				// newMechanic.setMechanicID(mechanic.getAssignedMechanicID());
				newMechanic.set_entitledToRelease(mechanic
						.getEntitledToRelease());
				newMechanic.setName(mechanic.getAssignedMechanicName());
				get_assignedMechanics().add(newMechanic);
			}
			/** avionics */
			com.intalio.gi.forms.tAmanagement.InspectionType.AssignedAvionics[] avionics = inspection
					.getAssignedAvionicsArray();
			set_assignedAvionics(new ArrayList<org.intalio.tempo.workflow.task.AssignedAvionics>());
			for (com.intalio.gi.forms.tAmanagement.InspectionType.AssignedAvionics avionic : avionics) {
				org.intalio.tempo.workflow.task.AssignedAvionics newAvioninc = new org.intalio.tempo.workflow.task.AssignedAvionics();
				// newMechanic.setMechanicID(mechanic.getAssignedMechanicID());
				newAvioninc.setName(avionic.getAssignedAvionicName());
				get_assignedAvionics().add(newAvioninc);
			}
		}
		/**************** DC DATA ****************/
		if (outputXML.getDC() != null) {
			DCType dc = outputXML.getDC();
			if (dc.xgetComments() != null && dc.xgetComments().validate()
					&& dc.getComments() != null) {
				set_comments(dc.getComments());
			}
		}
	}

	public Integer getPriority() {
		return _priority;
	}

	public void setPriority(Integer priority) {
		_priority = priority;
	}

	public Date getDeadline() {
		return _deadline;
	}

	public void setDeadline(Date deadline) {
		_deadline = deadline;
	}

	public boolean isAvailableTo(UserRoles credentials) {
		if (_state.equals(TaskState.CLAIMED)) {
			for (String userOwner : this.getUserOwners())
				if (credentials.getUserID().equals(userOwner))
					return true;
			return false;
		} else {
			return super.isAvailableTo(credentials);
		}
	}

	public String getInstanceId() {
		return _instanceId;
	}

	public void setInstanceId(String instanceId) {
		_instanceId = instanceId;
	}

	public Collection<org.intalio.tempo.workflow.task.AssignedMechanics> get_assignedMechanics() {
		return _assignedMechanics;
	}

	public void set_assignedMechanics(
			Collection<org.intalio.tempo.workflow.task.AssignedMechanics> mechanics) {
		_assignedMechanics = mechanics;
	}

	public TaskState get_state() {
		return _state;
	}

	public void set_state(TaskState _state) {
		this._state = _state;
	}

	public String get_ArrivalFlightNumber() {
		return _ArrivalFlightNumber;
	}

	public void set_ArrivalFlightNumber(String arrivalFlightNumber) {
		_ArrivalFlightNumber = arrivalFlightNumber;
	}

	public String get_Stand() {
		return _Stand;
	}

	public void set_Stand(String stand) {
		_Stand = stand;
	}

	public Collection<org.intalio.tempo.workflow.task.RTR> get_RTR() {
		return _RTR;
	}

	public void set_RTR(Collection<org.intalio.tempo.workflow.task.RTR> _rtr) {
		_RTR = _rtr;
	}

	public String get_AircraftID() {
		return _AircraftID;
	}

	public void set_AircraftID(String aircraftID) {
		_AircraftID = aircraftID;
	}

	public Date get_startTime() {
		return _startTime;
	}

	public void set_startTime(Date time) {
		_startTime = time;
	}

	public Date get_finishTime() {
		return _finishTime;
	}

	public void set_finishTime(Date time) {
		_finishTime = time;
	}

	public Boolean get_late() {
		return _late;
	}

	public void set_late(Boolean _late) {
		this._late = _late;
	}

	public Boolean get_update() {
		return _update;
	}

	public void set_update(Boolean _update) {
		this._update = _update;
	}

	public Date get_releaseTime() {
		return _releaseTime;
	}

	public void set_releaseTime(Date time) {
		_releaseTime = time;
	}

	public String get_DepartureFlightNumber() {
		return _DepartureFlightNumber;
	}

	public void set_DepartureFlightNumber(String departureFlightNumber) {
		_DepartureFlightNumber = departureFlightNumber;
	}

	public Collection<org.intalio.tempo.workflow.task.AssignedAvionics> get_assignedAvionics() {
		return _assignedAvionics;
	}

	public void set_assignedAvionics(
			Collection<org.intalio.tempo.workflow.task.AssignedAvionics> avionics) {
		_assignedAvionics = avionics;
	}

	public String get_InspectionType() {
		return _InspectionType;
	}

	public void set_InspectionType(String inspectionType) {
		_InspectionType = inspectionType;
	}

	public String get_InspectionStatus() {
		return _InspectionStatus;
	}

	public void set_InspectionStatus(String inspectionStatus) {
		_InspectionStatus = inspectionStatus;
	}

	public String get_resources() {
		return _resources;
	}

	public void set_resources(String _resources) {
		this._resources = _resources;
	}

	public String get_comments() {
		return _comments;
	}

	public void set_comments(String _comments) {
		this._comments = _comments;
	}

	public Date get_ScheduledArrival() {
		return _ScheduledArrival;
	}

	public void set_ScheduledArrival(Date scheduledArrival) {
		_ScheduledArrival = scheduledArrival;
	}

	public Date get_ScheduledDeparture() {
		return _ScheduledDeparture;
	}

	public void set_ScheduledDeparture(Date scheduledDeparture) {
		_ScheduledDeparture = scheduledDeparture;
	}

	public Date get_EstimatedArrival() {
		return _EstimatedArrival;
	}

	public void set_EstimatedArrival(Date estimatedArrival) {
		_EstimatedArrival = estimatedArrival;
	}

	public Date get_EstimatedDeparture() {
		return _EstimatedDeparture;
	}

	public void set_EstimatedDeparture(Date estimatedDeparture) {
		_EstimatedDeparture = estimatedDeparture;
	}

	public Date get_ActualArrival() {
		return _ActualArrival;
	}

	public void set_ActualArrival(Date actualArrival) {
		_ActualArrival = actualArrival;
	}

	public Date get_ActualDeparture() {
		return _ActualDeparture;
	}

	public void set_ActualDeparture(Date actualDeparture) {
		_ActualDeparture = actualDeparture;
	}

	public Collection<org.intalio.tempo.workflow.task.AssignedCoords> get_assignedCoord() {
		return _assignedCoord;
	}

	public void set_assignedCoord(
			Collection<org.intalio.tempo.workflow.task.AssignedCoords> coord) {
		_assignedCoord = coord;
	}

	private void addTimeToDate(Date time, Calendar date) {
		date.add(Calendar.HOUR, time.getHours());
		date.add(Calendar.MINUTE, time.getMinutes());
		date.add(Calendar.SECOND, time.getSeconds());
	}

}
