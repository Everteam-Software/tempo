package org.intalio.tempo.workflow.tmsb4p.query;

public interface TaskView {
	public static String ID = "id";
	public static String TASK_TYPE = "tasktype";
	public static String NAME = "name";
	public static String STATUS = "status";
	public static String PRIORITY = "priority";
	public static String CREATED_ON = "createdon";
	public static String ACTIVATION_TIME = "activationtime";
	public static String EXPIRATION_TIME = "expirationtime";
	public static String HAS_POTENTIAL_OWNERS = "haspotentialowners";
	public static String STARTBYEXISTS = "startbyexists";
	public static String COMPLETE_BY_EXISTS = "completebyexists";
	public static String RENDER_METH_EXISTS = "rendermethexists";
	
	public static String USERID = "userid";
	public static String GROUP = "group";
	public static String GENERIC_HUMAN_ROLE = "generichumanrole";
	public static String SKIPABLE = "skipable";
	public static String STARTBY = "startby";
	public static String COMPLETE_BY = "completeby";
	public static String PRES_NAME = "presname";
	public static String PRES_SUBJECT = "pressubject";
	public static String RENDERING_METH_NAME = "renderingmethname";
	public static String FAULT_MESSAGE = "faultmessage";
	public static String INPUT_MESSAGE = "inputmessage";
	public static String OUTPUT_MESSAGE = "outputmessage";
	public static String ATTACHMENT_NAME = "attachmentname";
	public static String ATTACHMENT_TYPE = "attachmenttype";
	public static String ESCALATED = "escalated";
	public static String PRIMARY_SEARCH_BY = "primarysearchby";	
	
	// extends field
	public static String ATTACHMENTS = "attachments";
}
