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

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return MimetypesFileTypeMap.getDefaultFileTypeMap()
				.getContentType(file);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return fis;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return file.getName();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return fos;
	}

}
