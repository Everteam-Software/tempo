package org.intalio.sita;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.util.XMLUtils;

public class StartProcess extends Thread {

	private File file;

	public StartProcess(File _file) {
		file = _file;
	}

	@Override
	public void run() {
		startProcess(file);
	}

	private void startProcess(File file) {
		boolean retry = true;

//		while (retry) {
//			retry = false;
			Properties properties = new Properties();
			try {
				properties.load(new FileReader(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String action = (String) properties
					.get("userProcessInitSOAPAction");
			String endpoint = (String) properties.get("processEndpoint");
			EndpointReference processEndpoint = new EndpointReference(endpoint);
			System.out.println("Process to autostart located=> Calling action "
					+ action + "on endpoint" + processEndpoint);

			Options options = new Options();
			options.setTo(processEndpoint);
			options.setAction(action);
			try {
				ServiceClient serviceClient = new ServiceClient();
				serviceClient.setOptions(options);
				File requestFile = new File(file.getAbsoluteFile()
						+ ".auto-startup");
				OMElement request;

				request = (OMElement) XMLUtils
						.toOM(new FileReader(requestFile));
				// System.out.println(request);
				OMElement response = serviceClient.sendReceive(request);
				// System.out.println(response);
				System.out.println("Process with start endpoint  " + endpoint
						+ " has start automatically at startup");
			} catch (Exception e) {

				e.printStackTrace();
				System.out
						.println("Warning : process with start endpoint  "
								+ endpoint
								+ " could not start automatically...retrying after 60 seconds");
//				retry = true;
//				try {
//					Thread.sleep(60000);
//				} catch (InterruptedException error) {
//					// TODO Auto-generated catch block
//					error.printStackTrace();
//				}
			}
		}

	//}
}
