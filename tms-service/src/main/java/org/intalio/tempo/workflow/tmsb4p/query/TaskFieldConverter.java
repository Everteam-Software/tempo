package org.intalio.tempo.workflow.tmsb4p.query;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.intalio.tempo.workflow.taskb4p.TaskType;
import org.intalio.tempo.workflow.tmsb4p.query.ParameterValues.SinglePara;
import org.intalio.tempo.workflow.tmsb4p.server.dao.TaskQueryType;

public class TaskFieldConverter {
	
	private static Map<String, String> FIELDS_MAP = new HashMap<String, String>();
	private static Map<String, String> ROLES_MAP = new HashMap<String, String>();
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

		FIELDS_MAP.put(TaskView.ATTACHMENT_NAME, "attachments.attachmentInfo.name");
		FIELDS_MAP.put(TaskView.ATTACHMENT_TYPE, "attachments.attachmentInfo.contentType");
		
		// field will be converted like "potentialOwners is null"
		FIELDS_MAP.put(TaskView.HAS_POTENTIAL_OWNERS, "potentialOwners");
		FIELDS_MAP.put(TaskView.STARTBYEXISTS, "startBy");
		FIELDS_MAP.put(TaskView.COMPLETE_BY_EXISTS, "completeBy");
		FIELDS_MAP.put(TaskView.RENDER_METH_EXISTS, "renderingMethName");
		
		// search the specific role which includes the specific userid or group.
		FIELDS_MAP.put(TaskView.USERID, "");
		FIELDS_MAP.put(TaskView.GROUP, "");
		FIELDS_MAP.put(TaskView.GENERIC_HUMAN_ROLE, "");
	}
	
	static {
		ROLES_MAP.put(GenericRoleType.task_initiator.name(), "taskInitiator");
		ROLES_MAP.put(GenericRoleType.task_stakeholders.name(), "taskStakeholders");
		ROLES_MAP.put(GenericRoleType.potential_owners.name(), "potentialOwners");
		ROLES_MAP.put(GenericRoleType.actual_owner.name(), "actualOwner");
		ROLES_MAP.put(GenericRoleType.excluded_owners.name(), "excluded_owners");
		ROLES_MAP.put(GenericRoleType.business_administrators.name(), "businessAdministrators");
		ROLES_MAP.put(GenericRoleType.notification_recipients.name(), "notificationRecipients");
	}

	private static String ATTRIBUTE_PREFIX = "task.";

//	private String entityAlias = null;
//
//	private Map<Integer, Object> paraValuesMap = new HashMap<Integer, Object>();
//	private int parameterIdx = 0;
//	
//	
//	public TaskClauseConverter(String entityAlias) {
//		this.entityAlias = entityAlias;
//	}
//
//	public Map<Integer, Object> getParaValues() {
//		return paraValuesMap;
//	}
//	
//	public String convertAttribute(String attribute)
//			throws InvalidAttributeException {
//		if (attribute == null) {
//			return null;
//		}
//
//		String temp = attribute;
//		if (temp.toLowerCase().startsWith(ATTRIBUTE_PREFIX.toLowerCase())) {
//			temp = attribute.substring(ATTRIBUTE_PREFIX.length());
//		}
//
//		String mappedField = FIELDS_MAP.get(temp.toLowerCase());
//		if (mappedField == null) {
//			throw new InvalidAttributeException("Invalid task attribute: "
//					+ attribute);
//		}
//
//		return entityAlias + "." + mappedField;
//	}

	public static String getFieldForSelectClause(String field)
			throws InvalidFieldException {
		String newField = field;
		if (field.startsWith(ATTRIBUTE_PREFIX)) {
			newField = field.substring(ATTRIBUTE_PREFIX.length());
		}

		// TaskView.HAS_POTENTIAL_OWNERS will be ignored since there has
		// no such field in the task entity
		if (TaskView.HAS_POTENTIAL_OWNERS.equals(newField)) {
			return null;
		}

		String result = FIELDS_MAP.get(field);
		if (result == null) {
			throw new InvalidFieldException(
					"Invalid field has been found in select clause: " + field);
		}

		return result;
	}
	
	public static ParameterValues convertWhereClause(String viewField,
			String funName, Object value) throws InvalidFieldException {	
		String mappingField = FIELDS_MAP.get(viewField);
		if (TaskView.TASK_TYPE.equals(viewField)) {
			
			return convertTaskType(mappingField, funName, value);
			
		} else if (TaskView.CREATED_ON.equals(viewField)
				|| TaskView.ACTIVATION_TIME.equals(viewField)
				|| TaskView.EXPIRATION_TIME.equals(viewField)
				|| TaskView.STARTBY.equals(viewField)
				|| TaskView.COMPLETE_BY.equals(viewField)) {
			
			return convertDateField(mappingField, funName, value);
			
		} else if ((TaskView.STARTBYEXISTS.equals(viewField))
				|| (TaskView.COMPLETE_BY_EXISTS.equals(viewField))
				|| (TaskView.RENDER_METH_EXISTS.equals(viewField))) {
			
			return convertExistField(viewField, funName, value);
			
		} else if ((TaskView.ATTACHMENT_NAME.equals(viewField))
				|| (TaskView.ATTACHMENT_TYPE.equals(viewField))){
			
		} else {
			return convertNormalField(mappingField, funName, value);
		} 
		
		return null; 
		
	}
	
	public static String getFieldForOrderClause(String field)
			throws InvalidFieldException {
		String newField = field;
		if (field.startsWith(ATTRIBUTE_PREFIX)) {
			newField = field.substring(ATTRIBUTE_PREFIX.length());
		}

		// TaskView.HAS_POTENTIAL_OWNERS will be ignored since there has
		// no such field in the task entity
		if (TaskView.HAS_POTENTIAL_OWNERS.equals(newField)) {
			return null;
		}

		String result = FIELDS_MAP.get(field);
		if (result == null) {
			throw new InvalidFieldException(
					"Invalid field has been found in select clause: " + field);
		}

		return result;
	}
	
	private static ParameterValues convertTaskType(String fieldName, String funName,
			Object value) throws InvalidFieldException {
		// check the funName, should be only two function support here
		// = or != or <>
		String strValue = (String)value;
		
		ParameterValues result = null;
		if (funName.equals(QueryOperator.EQUALS)) {
			if (TaskQueryType.ALL.toString().equalsIgnoreCase(strValue)) {
				result = new ParameterValues("or");
				result.addPara(new SinglePara(fieldName, funName, TaskType.TASK));
				result.addPara(new SinglePara(fieldName, funName, TaskType.NOTIFICATION));
			} else if (TaskQueryType.NOTIFICATIONS.toString().equalsIgnoreCase(strValue)) {
				result = new ParameterValues();
				result.addPara(new SinglePara(fieldName, funName, TaskType.NOTIFICATION));
			} else if (TaskQueryType.TASKS.toString().equalsIgnoreCase(strValue)) {
				result = new ParameterValues();
				result.addPara(new SinglePara(fieldName, funName, TaskType.TASK));				
			} else {
				throw new InvalidFieldException("TaskType type can only be ALL, NOTIFICATIONS or TASKS.");
			}
		} else if ((funName.equals(QueryOperator.NOT_EQUALS))
				|| (funName.equals(QueryOperator.NOT_EQUALS2))) {
			if (TaskQueryType.ALL.toString().equalsIgnoreCase(strValue)) {
				result = new ParameterValues("and");
				result.addPara(new SinglePara(fieldName, funName, TaskType.TASK));
				result.addPara(new SinglePara(fieldName, funName, TaskType.NOTIFICATION));				
			} else if (TaskQueryType.NOTIFICATIONS.toString().equalsIgnoreCase(strValue)) {
				result = new ParameterValues();
				result.addPara(new SinglePara(fieldName, funName, TaskType.NOTIFICATION));
			} else if (TaskQueryType.TASKS.toString().equalsIgnoreCase(strValue)) {
				result = new ParameterValues();
				result.addPara(new SinglePara(fieldName, funName, TaskType.TASK));				
			} else {
				throw new InvalidFieldException("TaskType type can only be ALL, NOTIFICATIONS or TASKS.");
			}
		} else {
			throw new InvalidFieldException("Task type can only support operators: =, <>, != ");
		}

		return result;		
	}
	
	private static ParameterValues convertDateField(String fieldName, String funName,
			Object value) throws InvalidFieldException {
		// Date type
		Date date = null;
		try {
			date = QueryUtil.formatDate((String) value);
		} catch (ParseException e) {
			throw new InvalidFieldException(
					"The date should be as the format like: '2009-02-27 16:22:11 CST' for field: " + fieldName);
		}
		
		ParameterValues paraValues = new ParameterValues();
		paraValues.addPara(new SinglePara(fieldName, funName, date));
		
		return paraValues;
	}
	
	private static ParameterValues convertExistField(String viewField,
			String funName, Object value) throws InvalidFieldException {
		// check the parameter value
		if (value == null) {			
			throw new InvalidFieldException("Value of " + viewField
					+ " hasn't been sepcified.");
		}
		
		// the value can only be true or false.
		String strValue = value.toString();
		if (!"true".equalsIgnoreCase(strValue)
				&& !"false".equalsIgnoreCase(strValue)) {
			throw new InvalidFieldException("Value of " + viewField
					+ " can only be 'true' or 'false'.");
		}
		
		// the function name can only be "=", "!=" or "<>"
		Boolean obj = Boolean.parseBoolean(strValue);
		String mappingField = FIELDS_MAP.get(viewField);
		ParameterValues result = new ParameterValues("and");
		if (funName.equals(QueryOperator.EQUALS)) {
			if (obj) {
				// the clause likes: haspotentialowners = true
				result.addPara(new SinglePara(mappingField, "is not null", null));
			} else {
				// the clause likes: haspotentialowners = false
				result.addPara(new SinglePara(mappingField, "is null", null));
			}
		} else if ((funName.equals(QueryOperator.NOT_EQUALS))
				|| (funName.equals(QueryOperator.NOT_EQUALS2))) {
			if (obj) {
				// the clause likes: haspotentialowners != true
				result.addPara(new SinglePara(mappingField, "is null", null));
			} else {
				// the clause likes: haspotentialowners != false
				result.addPara(new SinglePara(mappingField, "is not null", null));
			}
		} else {
			throw new InvalidFieldException("Status type can only support operators: =, <>, != ");
		}
		
		return result;
	}
	
	private static ParameterValues convertNormalField(String field, String funName, Object value) {
		ParameterValues result = new ParameterValues();
		result.addPara(new SinglePara(field, funName, value));
		
		return result;
	}
	
	public static String getTaskViewField(String field)
			throws InvalidFieldException {
		String newField = field;
		if (field.startsWith(ATTRIBUTE_PREFIX)) {
			newField = field.substring(ATTRIBUTE_PREFIX.length());
		}

		// To check the field is valid.
		if (FIELDS_MAP.get(newField) == null) {
			throw new InvalidFieldException(
					"Task query can't support the field: " + field);
		}

		return newField;
	}
	
	public static String getRoleMappingField(String roleViewfield) {
		String newField = roleViewfield;
		if (roleViewfield.startsWith(ATTRIBUTE_PREFIX)) {
			newField = roleViewfield.substring(ATTRIBUTE_PREFIX.length());
		}
		
		return (ROLES_MAP.get(newField));
	}
	
	public static ParameterValues convertAttachmentField(String viewField,
			String funName, Object value) {
		String mappingField = FIELDS_MAP.get(viewField);

		ParameterValues result = new ParameterValues();
		result.addPara(new SinglePara(mappingField, funName, value));

		return result;
	}

//	private static ParameterValues convertAttachmentField(String fieldName,
//			String funName, Object value) throws InvalidFieldException {
//		
//	}
}
