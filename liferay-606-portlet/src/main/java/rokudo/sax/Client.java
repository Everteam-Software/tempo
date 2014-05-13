package rokudo.sax;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class Client {
	public static void main(String[] args) {
		
		try {
			// Create the connection
			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
			SOAPConnection conn = scf.createConnection();

			// Create message
			MessageFactory mf = MessageFactory.newInstance();
			SOAPMessage msg = mf.createMessage();

			MimeHeaders hd = msg.getMimeHeaders();
			hd.addHeader("SOAPAction", "Task");

			// Object for message parts
			SOAPPart sp = msg.getSOAPPart();

			SOAPEnvelope env = sp.getEnvelope();
			//			env.addNamespaceDeclaration("xsd","http://www.w3.org/2001/XMLSchema");
			//			env.addNamespaceDeclaration("xsi","http://www.w3.org/2001/XMLSchema-instance");
			//			env.addNamespaceDeclaration("enc","http://schemas.xmlsoap.org/soap/encoding/");
			//			env.addNamespaceDeclaration("env","http://schemas.xmlsoap.org/soap/envelop/");
			//			env.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

			env.addNamespaceDeclaration("ws", "http://example.com/Diagram/MyBPM/MyExec");
			SOAPBody bd = env.getBody();

			bd.addChildElement("TaskRequest", "ws").addTextNode("Paolo");

			// Save message
			msg.saveChanges();

			// View input
			System.out.println("\n Soap request:\n");
			msg.writeTo(System.out);
			System.out.println();

			// Send
			String urlval = "http://localhost:8080/intalio/ode/processes/TestWS/Diagram/MyBPM/MyExec/MyPool";

			SOAPMessage rp = conn.call(msg, urlval);

			// View the output
			System.out.println("\nXML response\n");

			// Create transformer
			TransformerFactory tff = TransformerFactory.newInstance();
			Transformer tf = tff.newTransformer();

			// Get reply content
			Source sc = rp.getSOAPPart().getContent();

			// Set output transformation
			StreamResult result = new StreamResult(System.out);
			tf.transform(sc, result);
			System.out.println();

			// Close connection
			conn.close();

		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
}
