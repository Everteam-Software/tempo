package org.intalio.tempo.workflow.util;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class SecurityProviderProperty {

    public static final String CONFIG_DIR_PROPERTY = "org.intalio.tempo.configDirectory";
    private static boolean isCaseSensitive = false;

    public static boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    public static void setCaseSensitive(boolean isCaseSensitive) {
        SecurityProviderProperty.isCaseSensitive = isCaseSensitive;
    }

    static {
        String configDir = System.getProperty(CONFIG_DIR_PROPERTY);
        if (configDir == null) {
            throw new RuntimeException("System property " + CONFIG_DIR_PROPERTY + " not defined.");
        }
        File _configDir = new File(configDir, "securityConfig.xml");
        if (!_configDir.exists()) {
            throw new RuntimeException("Configuration directory " + _configDir.getAbsolutePath() + " doesn't exist.");
        }

        try {
            Document doc = new SAXReader().read(_configDir);
            Node node = doc.selectSingleNode("//*[@name='caseSensitive']/value");
            isCaseSensitive = "true".equalsIgnoreCase(node.getText());
        } catch (Exception e) {
            throw new RuntimeException("Unable to find caseSensitive property in securityConfig.xml file",e);
        }
    }
}
