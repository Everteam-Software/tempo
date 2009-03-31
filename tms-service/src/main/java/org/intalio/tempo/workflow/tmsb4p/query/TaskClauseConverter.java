package org.intalio.tempo.workflow.tmsb4p.query;

import java.util.HashMap;
import java.util.Map;

public class TaskClauseConverter {
	private static Map<String, String> FIELDS_MAP = new HashMap<String, String>();
	static {
		// attributes can be mapped directly
		FIELDS_MAP.put(TaskView.ID, "id");
		FIELDS_MAP.put(TaskView.TASK_TYPE, "taskType");
		FIELDS_MAP.put(TaskView.NAME, "name");
		FIELDS_MAP.put(TaskView.STATUS, "status");
		FIELDS_MAP.put(TaskView.PRIORITY, "priority");
		FIELDS_MAP.put(TaskView.CREATED_ON, "createdOn");
		FIELDS_MAP.put(TaskView.ACTIVATION_TIME, "activationTime");
		FIELDS_MAP.put(TaskView.EXPIRATION_TIME, "expirationTime");
		FIELDS_MAP.put(TaskView.STARTBYEXISTS, "startByExists");
		FIELDS_MAP.put(TaskView.COMPLETE_BY_EXISTS, "completeByExists");
		FIELDS_MAP.put(TaskView.RENDER_METH_EXISTS, "renderingMethodExists");
		FIELDS_MAP.put(TaskView.SKIPABLE, "isSkipable");
		FIELDS_MAP.put(TaskView.PRES_NAME, "presentationName");
		FIELDS_MAP.put(TaskView.PRES_SUBJECT, "presentationSubject");
		FIELDS_MAP.put(TaskView.ESCALATED, "escalated");
		FIELDS_MAP.put(TaskView.PRIMARY_SEARCH_BY, "primarySearchBy");

		FIELDS_MAP.put(TaskView.STARTBY, "startBy");
		FIELDS_MAP.put(TaskView.COMPLETE_BY, "completeBy");

		FIELDS_MAP.put(TaskView.RENDERING_METH_NAME, "renderingMethName");

		FIELDS_MAP.put(TaskView.FAULT_MESSAGE, "faultMessage");
		FIELDS_MAP.put(TaskView.INPUT_MESSAGE, "inputMessage");
		FIELDS_MAP.put(TaskView.OUTPUT_MESSAGE, "outputMessage");

		FIELDS_MAP.put(TaskView.ATTACHMENT_NAME, "");
		FIELDS_MAP.put(TaskView.ATTACHMENT_TYPE, "");
		
		// clause will be to check whether the potentialOwners is null or not
		FIELDS_MAP.put(TaskView.HAS_POTENTIAL_OWNERS, TaskView.HAS_POTENTIAL_OWNERS);
		
		// search the specific role which includes the specific userid or group.
		FIELDS_MAP.put(TaskView.USERID, "");
		FIELDS_MAP.put(TaskView.GROUP, "");
		FIELDS_MAP.put(TaskView.GENERIC_HUMAN_ROLE, "");
	}

	private String ATTRIBUTE_PREFIX = "Task.";

	private String entityAlias = null;

	private Map<Integer, Object> paraValuesMap = new HashMap<Integer, Object>();
	private int parameterIdx = 0;
	
	
	public TaskClauseConverter(String entityAlias) {
		this.entityAlias = entityAlias;
	}

	public Map<Integer, Object> getParaValues() {
		return paraValuesMap;
	}
	
	public String convertAttribute(String attribute)
			throws InvalidAttributeException {
		if (attribute == null) {
			return null;
		}

		String temp = attribute;
		if (temp.toLowerCase().startsWith(ATTRIBUTE_PREFIX.toLowerCase())) {
			temp = attribute.substring(ATTRIBUTE_PREFIX.length());
		}

		String mappedField = FIELDS_MAP.get(temp.toLowerCase());
		if (mappedField == null) {
			throw new InvalidAttributeException("Invalid task attribute: "
					+ attribute);
		}

		return entityAlias + "." + mappedField;
	}
}
