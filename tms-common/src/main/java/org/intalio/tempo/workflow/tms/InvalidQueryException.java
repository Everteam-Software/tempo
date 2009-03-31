package org.intalio.tempo.workflow.tms;

public class InvalidQueryException extends TMSException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3407651420376925934L;

	public InvalidQueryException() {
		super();
	}

	public InvalidQueryException(String message) {
		super(message);
	}

	public InvalidQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidQueryException(Throwable cause) {
		super(cause);
	}

}
