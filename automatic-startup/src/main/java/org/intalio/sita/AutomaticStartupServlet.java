package org.intalio.sita;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.swing.filechooser.FileFilter;
import javax.xml.stream.XMLStreamException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.util.XMLUtils;

public class AutomaticStartupServlet extends HttpServlet {

	@Override
	public void init() throws ServletException {
		System.out.println("Servlet started");

		File root = new File("").getAbsoluteFile();
		System.out.println(root.getParentFile());
		File ODEDirectory = new File(root.getParentFile().getAbsolutePath() + "\\var\\deploy");
		System.out.println("file" + ODEDirectory.getAbsolutePath());
		FilenameFilter filter = new AutomaticStartupProcessFilter();
		for (File currentDir : visitAllDirs(ODEDirectory)) {
			File[] files = currentDir.listFiles(filter);
			if (files == null || files.length == 0) {
				// System.out.println("No Processes to start");
			} else {
				// System.out.println(files.length + "file found");
				for (File file : files) {
					// System.out.println("file " + file.getAbsolutePath());

new StartProcess(file).start();
				
				}
			}
		}

	}

	

	public static Set<File> visitAllDirs(File dir) {
		Set<File> result = new HashSet<File>();

		if (dir.isDirectory()) {

			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				result.addAll(visitAllDirs(new File(dir, children[i])));

			}
			result.add(dir);
		}

		return result;

	}

}
