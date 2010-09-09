package org.intalio.tempo.workflow.tas.live;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.activation.MimetypesFileTypeMap;

public class FileDataSource implements DataSource {

	public FileDataSource(String path) throws Exception {
		file = new File(path);
		fis = new FileInputStream(file);
		fos = new FileOutputStream(file);
	}

	private File file;
	private FileInputStream fis;
	private FileOutputStream fos;

	
	public String getContentType() {
		return MimetypesFileTypeMap.getDefaultFileTypeMap()
				.getContentType(file);
	}

	
	public InputStream getInputStream() throws IOException {
		return fis;
	}

	
	public String getName() {
		return file.getName();
	}

	
	public OutputStream getOutputStream() throws IOException {
		return fos;
	}

}
