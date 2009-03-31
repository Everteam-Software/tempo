package org.intalio.tempo.workflow.tmsb4p.query;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.intalio.tempo.workflow.taskb4p.TaskStatus;
import org.intalio.tempo.workflow.taskb4p.TaskType;
import org.intalio.tempo.workflow.tmsb4p.query.ParameterValues.SinglePara;
import org.intalio.tempo.workflow.tmsb4p.server.dao.GenericRoleType;
import org.intalio.tempo.workflow.tmsb4p.server.dao.TaskQueryType;

public class TaskFieldConverter {
	
	private static Map<String, String> FIELDS_MAP = new HashMap<String, String>();
	private static Map<String, String> ROLES_MAP = new HashMap<String, String>();
	private static Map<String, TaskStatus> STATUS_MAP = new HashMap<String, TaskStatus>();
	
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

		FIELDS_MAP.put(TaskView.ATTACHMENT_NAME, "attachmentInfo.name");
		FIELDS_MAP.put(TaskView.ATTACHMENT_TYPE, "attachmentInfo.contentType");
		
		// field will be converted like "potentialOwners is null"
		FIELDS_MAP.put(TaskView.HAS_POTENTIAL_OWNERS, "potentialOwners");
		FIELDS_MAP.put(TaskView.STARTBYEXISTS, "startBy");
		FIELDS_MAP.put(TaskView.COMPLETE_BY_EXISTS, "completeBy");
		FIELDS_MAP.put(TaskView.RENDER_METH_EXISTS, "renderingMethName");
		
		// search the specific role which includes the specific userid or group.
		FIELDS_MAP.put(TaskView.USERID, "");
		FIELDS_MAP.put(TaskView.GROUP, "");
		FIELDS_MAP.put(TaskView.GENERIC_HUMAN_ROLE, "");
		
		// extends field
		FIELDS_MAP.put(TaskView.ATTACHMENTS, "attachments");
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
	
	static {
		STATUS_MAP.put(TaskStatus.CREATED.name(), TaskStatus.CREATED);
		STATUS_MAP.put(TaskStatus.READY.name(), TaskStatus.READY);
		STATUS_MAP.put(TaskStatus.RESERVED.name(), TaskStatus.RESERVED);
		STATUS_MAP.put(TaskStatus.IN_PROGRESS.name(), TaskStatus.IN_PROGRESS);
		STATUS_MAP.put(TaskStatus.SUSPENDED.name(), TaskStatus.SUSPENDED);
		STATUS_MAP.put(TaskStatus.COMPLETED.name(), TaskStatus.COMPLETED);
		STATUS_MAP.put(TaskStatus.FAILED.name(), TaskStatus.FAILED);
		STATUS_MAP.put(TaskStatus.ERROR.name(), TaskStatus.ERROR);
		STATUS_MAP.put(TaskStatus.EXITED.name(), TaskStatus.EXITED);
		STATUS_MAP.put(TaskStatus.OBSOLETE.name(), TaskStatus.OBSOLETE);
	}

	private static String ATTRIBUTE_PREFIX = "task.";

	public static String getFieldForSelectClause(String viewField)
			throws InvalidFieldException {
		if ((TaskView.USERID.equals(viewField))
				|| (TaskView.GROUP.equals(viewField))
				|| (TaskView.GENERIC_HUMAN_ROLE.equals(viewField))){
			return null;			
		} 
		
		String result = FIELDS_MAP.get(viewField);
		if (result == null) {
			throw new InvalidFieldException(
					"Invalid field has been found in select clause: " + viewField);
		}

		return result;
	}
	
	public static ParameterValues convertWhereClause(String viewField,
			String funName, Object value) throws InvalidFieldException {	
		String mappingField = FIELDS_MAP.get(viewField);
		if (TaskView.TASK_TYPE.equals(viewField)) {
			
			return convertTaskType(mappingField, funName, value);
			
		} else if (TaskView.STATUS.equals(viewField)){
			
			return convertTaskStatus(mappingField, funName, value);
			
		} else if (TaskView.CREATED_ON.equals(viewField)
				|| TaskView.ACTIVATION_TIME.equals(viewField)
				|| TaskView.EXPIRATION_TIME.equals(viewField)
				|| TaskView.STARTBY.equals(viewField)
				|| TaskView.COMPLETE_BY.equals(viewField)) {
			
			return convertDateField(mappingField, funName, value);
			
		} else if ((TaskView.STARTBYEXISTS.equals(viewField))
				|| (TaskView.COMPLETE_BY_EXISTS.equals(viewField))
				|| (TaskView.RENDER_METH_EXISTS.equals(viewField))
				|| (TaskView.HAS_POTENTIAL_OWNERS.equals(viewField))) {
			
			return convertExistField(viewField, funName, value);
			
		} else {
			return convertNormalField(mappingField, funName, value);
		} 
	}
	
	public static String getFieldForOrderClause(String field)
			throws InvalidFieldException {
		String viewField = field;
		if (field.startsWith(ATTRIBUTE_PREFIX)) {
			viewField = field.substring(ATTRIBUTE_PREFIX.length());
		}

		// TaskView.HAS_POTENTIAL_OWNERS will be ignored since there has
		// no such field in the task entity
		if (TaskView.HAS_POTENTIAL_OWNERS.equals(viewField)
				|| (TaskView.FAULT_MESSAGE.equals(viewField)) 
				|| (TaskView.INPUT_MESSAGE).equals(viewField) 
				|| (TaskView.OUTPUT_MESSAGE.equals(viewField)) 
				|| (TaskView.ATTACHMENT_NAME.equals(viewField)) 
				|| (TaskView.ATTACHMENT_TYPE.equals(viewField)) 
				|| (TaskView.USERID.equals(viewField))
				|| (TaskView.GROUP.equals(viewField))
				|| (TaskView.GENERIC_HUMAN_ROLE.equals(viewField))) {
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
				result = new ParameterValues();
				
				List<TaskType> newValue = new ArrayList<TaskType>();
				newValue.add(TaskType.NOTIFICATION);
				newValue.add(TaskType.TASK);
				
				result.addPara(new SinglePara(fieldName, QueryOperator.IN, newValue));
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
				result = new ParameterValues();
				
				List<TaskType> newValue = new ArrayList<TaskType>();
				newValue.add(TaskType.NOTIFICATION);
				newValue.add(TaskType.TASK);
				
				
				result.addPara(new SinglePara(fieldName, QueryOperator.NOT_IN, newValue));		
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
			throw new InvalidFieldException(viewField
					+ " can only support operators: =, <>, != ");
		}
		
		return result;
	}
	
	public static ParameterValues convertTaskStatus(String viewField,
			String funName, Object value) throws InvalidFieldException {
		ParameterValues result = new ParameterValues();
		if ((QueryOperator.EQUALS.equals(funName))
				|| (QueryOperator.NOT_EQUALS.equals(funName))
				|| (QueryOperator.NOT_EQUALS2.equals(funName))) {
			
			TaskStatus newValue = STATUS_MAP.get(value.toString());
			if (newValue == null) {
				throw new InvalidFieldException("Value of Task status can't be: " + value);
			}
			result.addPara(new SinglePara(FIELDS_MAP.get(viewField), funName, newValue));
			
		} else if ((QueryOperator.IN.equalsIgnoreCase(funName)) 
				|| (QueryOperator.NOT_IN.equalsIgnoreCase(funName))) {
			
			List<TaskStatus> newValue = convertTaskStatus((List<Object>)value);
			result.addPara(new SinglePara(FIELDS_MAP.get(viewField), funName, newValue));
						
		} else {
			throw new InvalidFieldException("Task status can only support operators: =, <>, !=, in, not in ");
		}
		
		return result;
	}
	
	private static List<TaskStatus> convertTaskStatus(List<Object> data) {
		List<TaskStatus> resultValue = new ArrayList<TaskStatus>();
		
		for (Iterator iterator = data.iterator(); iterator.hasNext();) {
			resultValue.add(STATUS_MAP.get(iterator.next()));
		}
		
		return resultValue;
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
	
	public static Set<String> getInlcudedRole(List<String> excludedRoles) {
        Set<String> result = ROLES_MAP.keySet();
        if ((excludedRoles == null) || (excludedRoles.isEmpty())) {
            return result;
        }

        for (Iterator<String> iterator = excludedRoles.iterator(); iterator.hasNext();) {
            result.remove(iterator.next());
        }

        return result;
    }
	
	public static List convertListValue(Object value) {
        if (value == null) {
            return Collections.EMPTY_LIST;
        }

        if (value instanceof List) {
            return (List) value;
        }

        String newVal = value.toString();
        return QueryUtil.parseString(newVal, ",");
    }

//	private static ParameterValues convertAttachmentField(String fieldName,
//			String funName, Object value) throws InvalidFieldException {
//		
//	}
}
