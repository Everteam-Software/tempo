package org.intalio.tempo.workflow.tas.live;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.util.ByteArrayDataSource;

import junit.framework.Assert;

import org.intalio.tempo.workflow.tas.live.TasStub.AddRequest;
import org.intalio.tempo.workflow.tas.live.TasStub.AddRequestChoice_type0;
import org.intalio.tempo.workflow.tas.live.TasStub.AttachmentMetadata;
import org.intalio.tempo.workflow.tas.live.TasStub.AuthCredentials;
import org.intalio.tempo.workflow.tas.live.TasStub.AuthorizedRoles_type0;
import org.junit.Test;

/**
 * Class to programatically test attachments in TAS
 * @author niko
 */
public class TaskAttachmentTest {

	// supposing token and roles are final for this test
	final String TOKEN = "VE9LRU4mJnVzZXI9PWludGFsaW9cYWRtaW4mJmlzc3VlZD09MTI2NTE2NTE0NTg2NyYmcm9sZXM9PWV4YW1wbGVzXG1hbmFnZXIsZXhhbXBsZXNcZW1wbG95ZWUsaW50YWxpb1xwcm9jZXNzYWRtaW5pc3RyYXRvcixleGFtcGxlc1xzdWJlbXBsb3llZSxpbnRhbGlvXHByb2Nlc3NtYW5hZ2VyJiZmdWxsTmFtZT09QWRtaW5pbmlzdHJhdG9yJiZlbWFpbD09YWRtaW5AZXhhbXBsZS5jb20mJm5vbmNlPT0tMzUyMDMxNjk1NDU0OTY2MDA0NyYmdGltZXN0YW1wPT0xMjY1MTY1MTQ1ODY3JiZkaWdlc3Q9PWhPVmp2UUYxUEpISUtMZHhpTHdlT1loRk9URT0mJiYmVE9LRU4=";
	final String[] ROLES = new String[]{"examples\\employee"};
	
	@Test
	public void testFileAttachment() throws Exception {
		addFileFromTestResourcesFolder("sample.pdf");
	}
	
	/**
	 * locate the test file in the resource folder
	 */
	private void addFileFromTestResourcesFolder(String fileName) throws Exception {
		addAttachment(getClass().getResource("/" + fileName).getFile());
	}
	
	private void addAttachment(String pathToFile) throws Exception {
		// get file, file metadata and file content
		File f = new File(pathToFile);
		String fileName = f.getName();
		String mimetype = new MimetypesFileTypeMap().getContentType(f);
		byte[] bytes = getBytesFromFile(f);
		
		// start creating the request
		TasStub tas = new TasStub();
		AddRequest req = new AddRequest();
		
		// add the content of the file
		AddRequestChoice_type0 choice = new AddRequestChoice_type0();
		DataHandler h = new DataHandler(new ByteArrayDataSource(bytes, "base64"));
		choice.setPayload(h);
		req.setAddRequestChoice_type0(choice);

		// add the TAS metadata
		AttachmentMetadata meta = new AttachmentMetadata();
		meta.setFilename(fileName);
		meta.setMimeType(mimetype);
		req.setAttachmentMetadata(meta);
		
		// add TAS credentials
		AuthCredentials cred = new AuthCredentials();
		AuthorizedRoles_type0 roles = new AuthorizedRoles_type0();
		roles.setRole(ROLES);
		cred.setAuthorizedRoles(roles);
		cred.setParticipantToken(TOKEN);
		req.setAuthCredentials(cred);

		// test resulting url
		// this will throw an exception if the URL is not valid
		URL url = new URL(tas.add(req).getUrl().toString());
		Assert.assertNotNull(url);
	}
	
	// turn the content of the file into a bytearray
	private static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) throw new RuntimeException("File is too large");
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
}
