package org.apache.directory.intalio.embed.webapp;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.directory.shared.ldap.ldif.Entry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.name.LdapDN;

public class ImportLDIF implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void contextInitialized(ServletContextEvent event) {
        try {
            InputStream in = event.getServletContext().getResourceAsStream("/WEB-INF/classes/intalio-apacheds.ldif");

            Iterator<Entry> iterator = new LdifReader(in);
            while (iterator.hasNext()) {
                Entry entry = iterator.next();
                LdapDN dn = new LdapDN(entry.getDn());
                LdapContext rootDSE = new InitialLdapContext(EnvHelper.createEnv(), null);
                rootDSE.createSubcontext(dn, entry.getAttributes());
            }
        } catch (Exception e) {
            
        }

    }

}
