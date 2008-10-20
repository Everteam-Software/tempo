package org.intalio.tempo.workflow.tms;

import junit.framework.TestCase;

public class TMSExceptionTest extends TestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		junit.textui.TestRunner.run(TMSExceptionTest.class);
	}
	
	public void testTMSException() throws Exception{
		Throwable cause = new Exception();
		String message = "message";
		AccessDeniedException ade = new AccessDeniedException();
		AccessDeniedException ade1 = new AccessDeniedException(message);
		
		InvalidTaskStateException itse = new InvalidTaskStateException();
		InvalidTaskStateException itse1 = new InvalidTaskStateException(message);
		InvalidTaskStateException itse2 = new InvalidTaskStateException(message, cause);
		InvalidTaskStateException itse3 = new InvalidTaskStateException(cause);
		
		TaskIDConflictException tice = new TaskIDConflictException();
		TaskIDConflictException tice1 = new TaskIDConflictException(message);
		TaskIDConflictException tice2 = new TaskIDConflictException(message, cause);
		TaskIDConflictException tice3 = new TaskIDConflictException(message, cause);
		
		UnavailableAttachmentException uae = new UnavailableAttachmentException();
		UnavailableAttachmentException uae1 = new UnavailableAttachmentException(message);
		UnavailableAttachmentException uae2 = new UnavailableAttachmentException(cause);
		UnavailableAttachmentException uae3 = new UnavailableAttachmentException(message, cause);
		
		UnavailableTaskException ute = new UnavailableTaskException();
		UnavailableTaskException ute1 = new UnavailableTaskException(message);
		UnavailableTaskException ute2 = new UnavailableTaskException(message, cause);
		UnavailableTaskException ute3 = new UnavailableTaskException(cause);
	}

}
