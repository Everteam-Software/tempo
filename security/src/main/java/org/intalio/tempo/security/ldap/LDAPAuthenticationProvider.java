/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *
 * $Id: LDAPAuthenticationProvider.java,v 1.10 2005/02/24 18:14:12 boisvert Exp $
 */
package org.intalio.tempo.security.ldap;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationAdmin;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.authentication.AuthenticationQuery;
import org.intalio.tempo.security.authentication.AuthenticationRuntime;
import org.intalio.tempo.security.authentication.UserNotFoundException;
import org.intalio.tempo.security.authentication.provider.AuthenticationProvider;
import org.intalio.tempo.security.util.IdentifierUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LDAPAuthenticationProvider
 * 
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class LDAPAuthenticationProvider 
implements AuthenticationProvider, LDAPProperties {

    final static Logger LOG = LoggerFactory.getLogger("tempo.security");

    private final static Property[] EMPTY_PROPERTIES = new Property[0];
    
    private Map                     _env;
    
    private String                  _realm;
    
    private LDAPQueryEngine         _engine;
    
    private String                  _dn;
    
    private String                  _principleSyntax;
    
    private LDAPAuthentication      _queryRuntime;
    
    
    /**
     * Constructor
     */
    public LDAPAuthenticationProvider(String realm, LDAPQueryEngine engine, String dn, Map env) {
        super();
        _realm  = realm;
        _engine = engine;
        _dn     = dn;
        _env    = env;
    }

    /**
     */
    @SuppressWarnings("unchecked")
    public void initialize(Object config) throws AuthenticationException {
        if (config==null)
            throw new IllegalArgumentException("Configuration object is null!");
        if (config instanceof Map==false)
            throw new AuthenticationException("Unexpected configuration type, "+config.getClass().getName()+". Expect java.util.Map");
        _queryRuntime = new LDAPAuthentication((Map<String,String>)config);
    }

    /**
     */
    public String getName() throws AuthenticationException {
        return "LDAP Authetication Provider";
    }

    /**
     * @see org.intalio.tempo.security.authentication.provider.AuthenticationProvider#getAdmin()
     */
    public AuthenticationAdmin getAdmin() throws AuthenticationException {
        throw new RuntimeException("Method not implemented");
    }

    /**
     * @see org.intalio.tempo.security.authentication.provider.AuthenticationProvider#getQuery()
     */
    public AuthenticationQuery getQuery() throws AuthenticationException {
        return _queryRuntime;
    }

    /**
     * @see org.intalio.tempo.security.authentication.provider.AuthenticationProvider#getRuntime()
     */
    public AuthenticationRuntime getRuntime() throws AuthenticationException {
        return _queryRuntime;
    }

    /**
     */
    public void dispose() throws AuthenticationException {
        // nothing
    }

    private static Map<String,String> readProperties(String keyRoot, Map<String,String> source) 
    throws IllegalArgumentException {
        Map<String,String> result = null;
        for (int i=0; true; i++) {
            String key   = keyRoot+'.'+i;
            String value = (String)source.get(key);
            if (value==null)
                break;
            int colon = value.indexOf(':');
            String front, back;
            if (colon==-1 ) {
                front = back = value;
            } else if ( colon==0 ) {
                front = value.substring(1);
                back = front;
            } else if ( colon==value.length()-1 ) {
                front = value.substring(0, colon);
                back = front;
            } else {
                front = value.substring(0, colon).trim();
                back  = value.substring(colon+1).trim();
            }
            if (front.length()==0 || back.length()==0)
                throw new IllegalArgumentException("Format is not reconized! key: "+key+" value: "+value);
            if (result==null)
                result = new TreeMap<String,String>();
            result.put(back, front);
        }
        return result;
    }

    private static String getNonNull(String key, Map map) 
    throws IllegalArgumentException {
        Object res = map.get(key);
        if (res!=null)
            return res.toString();

        StringBuffer sb = new StringBuffer();
        sb.append(key);
        sb.append(" cannot be null!");
        throw new IllegalArgumentException(sb.toString());
    }
    
    class LDAPAuthentication implements AuthenticationQuery, AuthenticationRuntime {

        private String _userBase;
        
        private String _userId;
        
        private Map<String,String> _userCredential;
        
        LDAPAuthentication( Map<String,String> config ) throws IllegalArgumentException {
            _userBase = getNonNull(SECURITY_LDAP_USER_BASE, config);
            _userId   = getNonNull(SECURITY_LDAP_USER_ID, config);
            _userCredential = readProperties(SECURITY_LDAP_USER_CREDENTIAL, config);
            if (_userCredential==null)
                throw new IllegalArgumentException("Property, "+SECURITY_LDAP_USER_CREDENTIAL+" is not set!");
            _principleSyntax = (String)config.get(SECURITY_LDAP_PRINCIPAL_SYNTAX);
            if (_principleSyntax==null || _principleSyntax.length()==0) {
                _principleSyntax = "dn";
            } else if (!_principleSyntax.equals("dn") && !_principleSyntax.equals("url")) {
                throw new IllegalArgumentException("Property, "+SECURITY_LDAP_USER_CREDENTIAL+" does not allow value of "+_principleSyntax+". Only 'dn' or 'url' is supported.");
            }
        }

        /**
         * @see org.intalio.tempo.security.authentication.AuthenticationQuery#getUserCredentials(java.lang.String)
         */
        public Property[] getUserCredentials(String user) 
        throws AuthenticationException, RemoteException {
            
            user = IdentifierUtils.stripRealm(user);
            try {
                short found;

                Map<String,Property> result = new HashMap<String,Property>();
                found = _engine.queryProperties(user, _userBase, _userId, _userCredential, result);
                if (LOG.isDebugEnabled())
                    LOG.debug("Result: "+found);
                if (found==LDAPQueryEngine.SUBJECT_NOT_FOUND)
                    throw new AuthenticationException("User, "+user+" is not found");
                if (found==LDAPQueryEngine.FIELD_NOT_FOUND)
                    return EMPTY_PROPERTIES;

                return (Property[])result.values().toArray(new Property[result.size()]);
            } catch (NamingException ne) {
                throw new AuthenticationException(ne);
            }
        }

        /**
         * @see AuthenticationRuntime#authenticate(String, Property[])
         */
        public boolean authenticate(String user, Property[] credentials) 
        throws UserNotFoundException, AuthenticationException, RemoteException {
            DirContext ctx;
            
            user = IdentifierUtils.stripRealm(user);
            try {
                if (LOG.isDebugEnabled())
                    LOG.debug("Authenticate("+user+") for realm, "+_realm);
                Property cred = null;
                for (int i = 0; i < credentials.length; i++) {
                    cred = credentials[i];
                    if (AuthenticationConstants.PROPERTY_PASSWORD.equals(cred.getName()) ) {
                        break;
                    } else if (AuthenticationConstants.PROPERTY_X509_CERTIFICATE.equals(cred.getName()) ) {
                        break;
                    } else {
                        cred = null;
                    }
                }
                if (cred==null) // can't find supported credentials
                    return false;

                // query doesn't work, as password is encrypted
                //return _engine.queryExist(user, (String)cred.getValue(), _userBase, _userId, cred.getName(), _userCredential);
                Properties env = new Properties();
                env.put( Context.SECURITY_AUTHENTICATION, _env.get(Context.SECURITY_AUTHENTICATION) );
                env.put( Context.INITIAL_CONTEXT_FACTORY, _env.get(Context.INITIAL_CONTEXT_FACTORY) );
                env.put( Context.PROVIDER_URL, _env.get(Context.PROVIDER_URL));
                
                if (_principleSyntax.equals("url"))
                    env.put( Context.SECURITY_PRINCIPAL, user+"@"+toDot(_dn));
                else
                    env.put( Context.SECURITY_PRINCIPAL, _userId+"="+user+","+_userBase+", "+_dn);
                env.put( Context.SECURITY_CREDENTIALS, cred.getValue() );

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Authenticate env:"+env);
                }

                // workaround for the fact that Sun's JNDI provider does an 
                // anonymous bind if no password is supplied for simple authentication
                // see http://java.sun.com/products/jndi/tutorial/ldap/faq/_context.html
                String auth = (String) env.get( Context.SECURITY_AUTHENTICATION );
                if ( auth.equalsIgnoreCase( "simple" ) ) {
                    String pw = (String) env.get( Context.SECURITY_CREDENTIALS );
                    if ( pw == null || pw.trim().length() == 0 ) {
                        return false;
                    }
                }

                // use the same way as obtaining _context to authenticate
                ctx = new InitialDirContext(env);
                ctx.lookup( _userBase+", "+_dn );
                ctx.close();
                return true;
            } catch (NamingException ne) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Authentication of user, "+user+" failed!", ne);
                return false;
            }
        }
        
        /**
         * Convert an idenitifer from LDAP distingish name syntax to url-like
         * syntax. For example, from cn=admin,dc=intalio,dc=com to admin@intalio.com.
         * 
         * @param dn
         * @return
         */
        private String toDot(String dn) {
            StringBuffer res = null;
            int index = 0;
            int comma = 0;
            int prev = 0;
            int len = dn.length();
            while (prev < len) {
                index = dn.indexOf("dc=", prev)+3;
                if (index==-1)
                    index = dn.indexOf("DC=", prev)+3;
                if (index==-1) {
                    if (res==null) {
                        return dn;
                    } else {
                        throw new IllegalArgumentException("The syntax is of dn invalid: "+dn);
                    }
                } else { 
                    comma = dn.indexOf(',', index);
                    if (res==null)
                        res = new StringBuffer();
                    else
                        res.append('.');
                    if (comma==-1) {
                        res.append(dn.substring(index).trim());
                        break;
                    } else { 
                        res.append(dn.substring(index, comma).trim());
                    }
                    prev = comma + 1;
                }
            }
            return res.toString();
        }
    }
}
