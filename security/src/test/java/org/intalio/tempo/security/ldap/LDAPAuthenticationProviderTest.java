package org.intalio.tempo.security.ldap;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

import junit.framework.TestCase;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;

public class LDAPAuthenticationProviderTest extends TestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		junit.textui.TestRunner.run(LDAPAuthenticationProviderTest.class);
	}

	public void testInitialization() throws Exception {
		LDAPSecurityProvider securityProvider = new LDAPSecurityProvider();
		String baseDN = "baseDN";
		LDAPQueryEngine engine = new LDAPQueryEngine(securityProvider, baseDN);

		String realm = "realm";
		String dn = "dn";
		Map<String, String> env = new HashMap<String, String>();

		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.INITIAL_CONTEXT_FACTORY, "INITIAL_CONTEXT_FACTORY");
		env.put(Context.PROVIDER_URL, "PROVIDER_URL");

		LDAPAuthenticationProvider authProvider = new LDAPAuthenticationProvider(
				realm, engine, dn, env);
		assertNotNull(authProvider);

		Object config = null;
		Exception e = null;
		try {
			authProvider.initialize(config);
		} catch (Exception iae) {
			e = iae;
		}
		assertEquals(e.getClass(), IllegalArgumentException.class);

		config = "anything but a map";
		e = null;
		try {
			authProvider.initialize(config);
		} catch (Exception iae) {
			e = iae;
		}
		assertEquals(e.getClass(), AuthenticationException.class);

		Map<String, String> mapConfig = new HashMap<String, String>();
		mapConfig.put(LDAPProperties.SECURITY_LDAP_USER_BASE, "true");
		mapConfig.put(LDAPProperties.SECURITY_LDAP_USER_ID, "user");
		mapConfig.put(LDAPProperties.SECURITY_LDAP_USER_CREDENTIAL + ".0",
				"someone:user");
		mapConfig.put(LDAPProperties.SECURITY_LDAP_USER_CREDENTIAL + ".1",
				"******:password");
		mapConfig.put(LDAPProperties.SECURITY_LDAP_PRINCIPAL_SYNTAX, "url");
		try {
			authProvider.initialize(mapConfig);
		} catch (Exception iae) {
			assertTrue(false);
		}

		assertEquals(authProvider.getName(), "LDAP Authetication Provider");

		try {
			authProvider.getAdmin();
		} catch (Exception re) {
			assertEquals(re.getMessage(), "Method not implemented");
			assertEquals(re.getClass(), RuntimeException.class);
		}

		assertNotNull(authProvider.getRuntime());
		assertEquals(authProvider.getRuntime().getClass(),
				LDAPAuthenticationProvider.LDAPAuthentication.class);
		assertEquals(authProvider.getQuery(), authProvider.getRuntime());

		authProvider.dispose();

		String user = "user";
		Property[] credentials = new Property[1];

		credentials[0] = new Property(
				AuthenticationConstants.PROPERTY_PASSWORD, "");
		assertFalse(authProvider.getRuntime().authenticate(user, credentials));

		credentials[0] = new Property(
				AuthenticationConstants.PROPERTY_PASSWORD, " ");
		assertFalse(authProvider.getRuntime().authenticate(user, credentials));

		credentials[0] = new Property(
				AuthenticationConstants.PROPERTY_PASSWORD, "secretgarden");
		assertFalse(authProvider.getRuntime().authenticate(user, credentials));

		try {
			authProvider.getQuery().getUserCredentials(user);
		} catch (Exception ae) {
			assertEquals(ae.getClass(), AuthenticationException.class);
		}
	}
}
