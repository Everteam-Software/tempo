/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *
 * $Id: LDAPRBACProviderTest.java,v 1.10 2005/02/24 18:20:22 boisvert Exp $
 */
package org.intalio.tempo.security.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.provider.SecurityProvider;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.RoleNotFoundException;
import org.intalio.tempo.security.rbac.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LDAPRBACProviderTest
 * 
 * @author <a href="http://www.intalio.com">&copy; Intalio Inc.</a>
 */
public class LDAPRBACProviderTest extends TestCase {

    protected final static Logger LOG = LoggerFactory.getLogger("tempo.security.test");
    
    private final static String REALM = "intalio";

    private final static String ROLE_DRIVERS    = "Drivers";
    
    private final static String ROLE_MGRS       = "Managers";
    
    private final static String ROLE_A_STAFFS   = "Accounting Staffs";
    
    private final static String ROLE_A_MGRS     = "Accounting Managers";

    private final static String ROLE_E_STAFFS   = "Engineering Staffs";
    
    private final static String ROLE_E_MGRS     = "Engineering Managers";
    
    private AbstractSuite    _suite;
    
    private SecurityProvider _security;
    
    private LDAPRBACProvider _rbac;

    private Map _configuration;

    /**
     * Constructor
     * @param arg0
     */
    public LDAPRBACProviderTest(String arg0) {
        super(arg0);
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new LDAPGroupSuite(LDAPRBACProviderTest.class));
        suite.addTest(new LDAPGroupSuiteActiveDirectory(LDAPRBACProviderTest.class));
        //suite.addTest(new LDAPRoleSuite(LDAPRBACProviderTest.class));
        return suite;
    }

    public void setSuite( AbstractSuite suite ) {
        this._suite = suite;
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        initRBACProvider(REALM);
    }
    
    protected void initRBACProvider(String realms) throws Exception {
        _configuration = _suite.getConfiguration();
        _security = new LDAPSecurityProvider();
        _security.initialize(_configuration);
        _rbac = (LDAPRBACProvider)_security.getRBACProvider(realms);
    }
    
    protected void tearDown() throws Exception {
        _security.dispose();
    }

    public String getName() {
        if ( _suite==null )
            return super.getName();
        else
            return super.getName()+" with "+_suite.getName();
    }

    public String toString() {
        if ( _suite==null )
            return super.toString();
        else
            return super.toString()+" with "+_suite.getClass().getName();
    }
    
    public void testQueryAssignedUsersNotExist() throws Exception {
        String role;
        String[] users = null;
        
        RBACQuery query = _rbac.getQuery();

        try {        
            role = REALM+"\\"+ROLE_DRIVERS;
            users = query.assignedUsers(role);
        } catch ( RoleNotFoundException e ) {
            return;
        }
        fail("Expected exception not thrown. "+listArray(users));
    }

    public void testQueryAssignedUsersEmptyRole() throws Exception {
        String role;
        String[] users;
        
        RBACQuery query = _rbac.getQuery();
        
        role = REALM+"\\"+ROLE_MGRS;
        users = query.assignedUsers(role);
        assertEquals("User is not null", 0, users.length);
    }
    
    public void testQueryAssignedUsersSuperRole() throws Exception {
        String role;
        String[] users;
        
        RBACQuery query = _rbac.getQuery();
    
        role = REALM+"\\"+ROLE_A_STAFFS;
        users = query.assignedUsers(role);
        assertEquals("Unexpected number of staffs", 2, users.length);
        assertTrue("Unexpected person "+users[0]+" role!", users[0].equals(REALM+"\\"+"jimm") || users[0].equals(REALM+"\\"+"jimc"));
        assertTrue("Unexpected person "+users[1]+"of role!", users[1].equals(REALM+"\\"+"jimm") || users[1].equals(REALM+"\\"+"jimc"));
        assertTrue("Duplicated person returned!", !users[0].equals(users[1]));
    }
    
    public void testQueryAssignedUsersNormalRole() throws Exception {
        String role;
        String[] users;

        RBACQuery query = _rbac.getQuery();
            
        role = REALM+"\\"+ROLE_E_MGRS;
        users = query.assignedUsers(role);
        assertEquals("Unexpected number of managers", 1, users.length);
        assertEquals("Wrong person returned", users[0], REALM+"\\"+"jima");
    }

    public void testQueryAuthroizedUsersSuperRole() throws Exception {
        String role;
        String[] users;
        
        RBACQuery query = _rbac.getQuery();
    
        role = REALM+"\\"+ROLE_A_STAFFS;
        users = query.authorizedUsers(role);
        assertEquals("Unexpected number of staffs", 3, users.length);
        assertTrue("Wrong person of role!", users[0].equals(REALM+"\\"+"jimm") || users[0].equals(REALM+"\\"+"jimc") || users[0].equals(REALM+"\\"+"jimb") );
        assertTrue("Wrong person of role!", users[1].equals(REALM+"\\"+"jimm") || users[1].equals(REALM+"\\"+"jimc") || users[1].equals(REALM+"\\"+"jimb") );
        assertTrue("Wrong person of role!", users[2].equals(REALM+"\\"+"jimm") || users[2].equals(REALM+"\\"+"jimc") || users[2].equals(REALM+"\\"+"jimb") );
        assertTrue("Wrong person of role!", !users[0].equals(users[1]));
        assertTrue("Wrong person of role!", !users[0].equals(users[2]));
        assertTrue("Wrong person of role!", !users[1].equals(users[2]));
    }

    public void testQueryAuthroizedUsersNestedRole() throws Exception {
        String role;
        String[] users;
        
        RBACQuery query = _rbac.getQuery();
    
        role = REALM+"\\"+ROLE_A_MGRS;
        users = query.authorizedUsers(role);
        assertEquals("Unexpected number of staffs", 1, users.length);
    }
    
    public void testQueryAuthroizedUsersNormalRole() throws Exception {
        String role;
        String[] users;

        RBACQuery query = _rbac.getQuery();
            
        role = REALM+"\\"+ROLE_E_MGRS;
        users = query.authorizedUsers(role);
        assertEquals("Unexpected number of managers", 1, users.length);
    }
    
    public void testQueryAssignedRolesNonExistUser() throws Exception {
        String user;
        String[] roles = null;
        
        RBACQuery query = _rbac.getQuery();
        
        try {
            user = REALM+"\\"+"Wally";
            roles = query.assignedRoles(user);
        } catch ( UserNotFoundException unf ) {
            return;
        }
        fail("Expected exception not thrown. "+listArray(roles));
    }        
    
    public void testQueryAssignedRolesNormalUser() throws Exception {
        String user;
        String[] roles;
        
        RBACQuery query = _rbac.getQuery();
        
        user = REALM+"\\"+"jimm";
        roles = query.assignedRoles(user);
        assertEquals("Unexpected number of roles", 1, roles.length);
        assertEquals("Wrong role!", REALM+"\\"+ROLE_A_STAFFS, roles[0]);
        LOG.info("roles of jimm "+ roles[0]);
    }
    
    public void testQueryAuthorizedRolesNonExistUser() throws Exception {
        String user;
        String[] roles = null;
        
        RBACQuery query = _rbac.getQuery();
        
        try {
            user = REALM+"\\"+"Wally";
            roles = query.authorizedRoles(user);
        } catch ( UserNotFoundException unf ) {
            return;
        }
        fail("Expected exception not thrown. "+listArray(roles));
    }        
    
    public void testQueryAuthorizedRolesNormalUser() throws Exception {
        String user;
        String[] roles;
        
        RBACQuery query = _rbac.getQuery();
        
        user = REALM+"\\"+"jimm";
        roles = query.authorizedRoles(user);
        assertEquals("Unexpected number of roles", 1, roles.length);
        assertEquals("Wrong role!", REALM+"\\"+ROLE_A_STAFFS, roles[0]);
        LOG.info("roles of jimm "+ roles[0]);
    }

    public void testQueryAuthorizedRolesUserManager() throws Exception {
        String user;
        String[] roles;
        
        RBACQuery query = _rbac.getQuery();
        
        user = REALM+"\\"+"jimb";
        roles = query.authorizedRoles(user);
        assertEquals("Unexpected number of roles", 3, roles.length);
        assertTrue("Unexpected role, "+roles[0], (REALM+"\\"+ROLE_A_STAFFS).equals(roles[0])||(REALM+"\\"+ROLE_A_MGRS).equals(roles[0])||(REALM+"\\"+ROLE_MGRS).equals(roles[0]));
        assertTrue("Unexpected role, "+roles[1], (REALM+"\\"+ROLE_A_STAFFS).equals(roles[1])||(REALM+"\\"+ROLE_A_MGRS).equals(roles[1])||(REALM+"\\"+ROLE_MGRS).equals(roles[1]));
        assertTrue("Unexpected role, "+roles[1], (REALM+"\\"+ROLE_A_STAFFS).equals(roles[2])||(REALM+"\\"+ROLE_A_MGRS).equals(roles[2])||(REALM+"\\"+ROLE_MGRS).equals(roles[2]));
        assertTrue("Same role returned twice!", !roles[0].equals(roles[1]));
        assertTrue("Same role returned twice!", !roles[0].equals(roles[2]));
        assertTrue("Same role returned twice!", !roles[2].equals(roles[1]));        
    }

    public void testQueryAscendantsNotExist() throws Exception {
        String role;
        String[] roles;
        
        RBACQuery query = _rbac.getQuery();
        
        try {
            role = REALM+"\\"+ROLE_DRIVERS;
            roles = query.ascendantRoles(role);
        } catch ( RoleNotFoundException re ) {
            return;
        }
        fail("Expected exception not thrown! roles=" + roles);
    }
    
    public void testQueryAscendantsTop() throws Exception {
        String role;
        String[] roles;
        
        RBACQuery query = _rbac.getQuery();
        role = REALM+"\\"+ROLE_MGRS;
        roles = query.ascendantRoles(role);
        assertEquals("Unexpected number of ascendants", 0, roles.length);
    }

    public void testQueryAscendantsNormal() throws Exception {
        String role;
        String[] roles;
        
        RBACQuery query = _rbac.getQuery();
        role = REALM+"\\"+ROLE_E_MGRS;
        roles = query.ascendantRoles(role);
        assertEquals("Unexpected number of ascendants", 2, roles.length);
        assertTrue("Invalid role", (REALM+"\\"+ROLE_E_STAFFS).equals(roles[0]) || (REALM+"\\"+ROLE_MGRS).equals(roles[0]));
        assertTrue("Invalid role", (REALM+"\\"+ROLE_E_STAFFS).equals(roles[1]) || (REALM+"\\"+ROLE_MGRS).equals(roles[1]));
        assertTrue("Same role returned", !roles[0].equals(roles[1]) );
    }

    public void testQueryDescendantsNotExist() throws Exception {
        String role;
        String[] roles;
        
        RBACQuery query = _rbac.getQuery();
        
        try {
            role = REALM+"\\"+ROLE_DRIVERS;
            roles = query.descendantRoles(role);
        } catch ( RoleNotFoundException re ) {
            return;
        }
        fail("Expected exception not thrown! roles=" + roles);
    }
    
    public void testQueryDescendantsTop() throws Exception {
        String role;
        String[] roles;
        
        RBACQuery query = _rbac.getQuery();
        role = REALM+"\\"+ROLE_MGRS;
        roles = query.descendantRoles(role);
        assertEquals("Unexpected number of descendants", 2, roles.length);
        assertTrue("Invalid role", (REALM+"\\"+ROLE_E_MGRS).equals(roles[0]) || (REALM+"\\"+ROLE_A_MGRS).equals(roles[0]));
        assertTrue("Invalid role", (REALM+"\\"+ROLE_E_MGRS).equals(roles[1]) || (REALM+"\\"+ROLE_A_MGRS).equals(roles[1]));
        assertTrue("Invalid role", !roles[0].equals(roles[1]));

    }

    public void testQueryDescendantsNormal() throws Exception {
        String role;
        String[] roles;
        
        RBACQuery query = _rbac.getQuery();
        role = REALM+"\\"+ROLE_E_MGRS;
        roles = query.descendantRoles(role);
        assertEquals("Unexpected number of descendants", 0, roles.length);
    }
    
    public void testQueryTopRoles() throws Exception {
        String[] roles;
        
        RBACQuery query = _rbac.getQuery();
        roles = query.topRoles(REALM);
        assertEquals("Unexpected number of top roles", 3, roles.length);
        assertTrue("Unexpected roles, "+roles[0], (REALM+"\\"+ROLE_E_STAFFS).equals(roles[0])||(REALM+"\\"+ROLE_A_STAFFS).equals(roles[0])||(REALM+"\\"+ROLE_MGRS).equals(roles[0]));
        assertTrue("Unexpected roles, "+roles[1], (REALM+"\\"+ROLE_E_STAFFS).equals(roles[1])||(REALM+"\\"+ROLE_A_STAFFS).equals(roles[1])||(REALM+"\\"+ROLE_MGRS).equals(roles[1]));
        assertTrue("Unexpected roles, "+roles[2], (REALM+"\\"+ROLE_E_STAFFS).equals(roles[2])||(REALM+"\\"+ROLE_A_STAFFS).equals(roles[2])||(REALM+"\\"+ROLE_MGRS).equals(roles[2]));    
        assertTrue("Same role returned twice", !roles[0].equals(roles[1]));
        assertTrue("Same role returned twice", !roles[0].equals(roles[2]));
        assertTrue("Same role returned twice", !roles[2].equals(roles[1]));
    }

    /*
    public void testAllRoles() throws Exception {
        String[] roles;
        
        LDAPRBACQuery query = (LDAPRBACQuery)rbac.getQuery();
        roles = query.allRoles(REALM);
        LOG.info(listArray(roles));
    }*/
    

    public void testUserProperties() throws Exception {
        String user = REALM+"\\"+"jimm";
        Property[] props;
        Collection result;
        Collection<Property> control = new ArrayList<Property>();
        
        RBACQuery query = _rbac.getQuery();
        
        props = query.userProperties(user);
        LOG.info(listArray(props));
        result = toCollection(props);
        
        control.add(new Property("cn", "Jim Mourikis"));
        control.add(new Property("sn", "Mourikis"));
        //control.add(new Property("ou", "Accounting"));
        control.add(new Property("givenname", "Jim"));
        control.add(new Property("objectclass", "top"));
        control.add(new Property("objectclass", "person"));
        control.add(new Property("objectclass", "organizationalPerson"));                
        control.add(new Property("uid", "jimm"));
        //control.add(new Property("roomNumber", "333"));
        control.add(new Property("email", "mourikis@intalio.com"));
        //control.add(new Property("ou", "Accounting"));
        //control.add(new Property("streetaddress", "San Mateo"));
        for (Iterator itor = control.iterator(); itor.hasNext();) {
            Property prop = (Property) itor.next();
            assertTrue("Expected items, "+prop.getName()+" are missing.", result.contains(prop));    
        }
        
    }
    
    public void testRoleProperties() throws Exception {
        String role = REALM+"\\"+ROLE_E_MGRS;
        Property[] props;
        Collection<Property> result;
        Collection<Property> control = new ArrayList<Property>();

        RBACQuery query = _rbac.getQuery();
        
        props = query.roleProperties(role);
        LOG.info(listArray(props));
        result = toCollection(props);
        // too bad... not much common attribute between group or test...
        control.add(new Property("objectclass", "top"));
        assertTrue("Some expected items are missing.", result.containsAll(control));
    }

    @SuppressWarnings("unchecked")
    private Collection<Property> toCollection(Property[] array) {
        if (array==null||array.length==0)
            return Collections.EMPTY_LIST;

        Collection<Property> res = new ArrayList<Property>();
        for (int i = 0; i < array.length; i++) {
            res.add(array[i]);
        }
        return res;
    }
    
    private String listArray(Object[] array) {
        if ( array==null )
            return "[]";
        if ( array.length==0 )
            return "[0]";
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        for (int i = 0; i < array.length; i++) {
            if (i!=0)
                sb.append(", ");
            sb.append(array[i]);
        }
        sb.append(']');
        return sb.toString();
    }
    
}
