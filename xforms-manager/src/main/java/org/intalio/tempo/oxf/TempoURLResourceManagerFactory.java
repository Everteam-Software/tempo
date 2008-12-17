package org.intalio.tempo.oxf;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.orbeon.oxf.resources.ResourceManager;
import org.orbeon.oxf.resources.ResourceManagerFactoryFunctor;
import org.orbeon.oxf.resources.URLResourceManagerImpl;

/**
 * This class allows to define the properties for the URLManagerFactory in a
 * different file than web.xml. web.xml cannot be reloaded without redeploying
 * in geronimo.
 */
public class TempoURLResourceManagerFactory implements
		ResourceManagerFactoryFunctor {

	private Properties props;

	public static Properties convertBundleToProperties(ResourceBundle rb) {
		Properties props = new Properties();
		for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
			String key = (String) keys.nextElement();
			props.put(key, rb.getString(key));
		}
		return props;
	}

	public TempoURLResourceManagerFactory(Map<?, ?> _props) {
		ResourceBundle __props = ResourceBundle.getBundle("tempo");
		props = convertBundleToProperties(__props);
	}

	public ResourceManager makeInstance() {
		return new URLResourceManagerImpl(props);
	}
}