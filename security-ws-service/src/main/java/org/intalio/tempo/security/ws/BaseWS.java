package org.intalio.tempo.security.ws;

import java.io.File;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.intalio.tempo.security.provider.SecurityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

public class BaseWS {
    private static final Logger LOG = LoggerFactory.getLogger(BaseWS.class);

    private static boolean _initialized;

    protected static SecurityProvider _securityProvider;

    protected static org.intalio.tempo.security.token.TokenService _tokenService;

    protected static File _configDir;

    protected BaseWS() {
        initStatics();
    }

    protected void initStatics() {
        try {
            synchronized (BaseWS.class) {
                if (_initialized)
                    return;
                LOG.debug("Initializing configuration.");
                String configDir = System.getProperty(Constants.CONFIG_DIR_PROPERTY);
                if (configDir == null) {
                    throw new RuntimeException("System property " + Constants.CONFIG_DIR_PROPERTY + " not defined.");
                }
                _configDir = new File(configDir);
                if (!_configDir.exists()) {
                    throw new RuntimeException("Configuration directory " + _configDir.getAbsolutePath()
                            + " doesn't exist.");
                }
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                FileSystemResource config = new FileSystemResource(new File(_configDir, "securityConfig.xml"));
                XmlBeanFactory factory = new XmlBeanFactory(config);
    
                PropertyPlaceholderConfigurer propsCfg = new PropertyPlaceholderConfigurer();
                propsCfg.setSearchSystemEnvironment(true);
                propsCfg.postProcessBeanFactory(factory);
                _securityProvider = (SecurityProvider) factory.getBean("securityProvider");
                _tokenService = (org.intalio.tempo.security.token.TokenService) factory.getBean("tokenService");
                _initialized = true;
            }
        } catch (RuntimeException except) {
            LOG.error("Error during initialization of security service", except);
            throw except;
        }
    }

    protected String getRequiredParameter(OMElement element, QName parameter) {
        OMElement e = element.getFirstChildWithName(parameter);
        if (e == null)
            throw new IllegalArgumentException("Missing parameter: " + parameter);
        return e.getText();
    }
}
