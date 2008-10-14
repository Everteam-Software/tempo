package org.intalio.tempo.security.impl;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationAdmin;
import org.intalio.tempo.security.authentication.AuthenticationQuery;
import org.intalio.tempo.security.authentication.AuthenticationRuntime;
import org.intalio.tempo.security.authentication.provider.AuthenticationProvider;
import org.intalio.tempo.security.provider.SecurityProvider;
import org.intalio.tempo.security.rbac.RBACAdmin;
import org.intalio.tempo.security.rbac.RBACQuery;
import org.intalio.tempo.security.rbac.RBACRuntime;
import org.intalio.tempo.security.rbac.provider.RBACProvider;
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class RealmsTest extends TestCase {
    protected transient Logger _log = LoggerFactory.getLogger(getClass());
    final static ExpectThat expect = new ExpectThatImpl();
    @Subject
    Realms _realms;
    @Mock
    SecurityProvider sp;
    @Mock
    RBACProvider rbac;
//    @Mock
//    RBACProvider rbac_test;
    @Mock
    RBACAdmin rbacAdmin;
    @Mock
    RBACQuery rbacQuery;
    @Mock
    RBACRuntime rbacRuntime;
    @Mock
    AuthenticationProvider ap;
    @Mock
    AuthenticationQuery authQuery;
    @Mock
    AuthenticationRuntime authRuntime;
    @Mock
    AuthenticationAdmin authAdmin;
    String[] realms = new String[] { "intalio", "test" };

    @Specification
    public void testAll()throws Exception{
        expect.that(new Expectations(){{
            atLeast(1).of(sp).getRealms();will(returnValue(realms));
            atLeast(1).of(sp).getRBACProvider("intalio");will(returnValue(rbac));            
            atLeast(1).of(rbac).getAdmin();will(returnValue(rbacAdmin));
            atLeast(1).of(rbacAdmin).addUser("intalio/eng1", new Property[0]);
            atLeast(1).of(sp).getRBACProvider("test");will(returnValue(rbac));
            atLeast(1).of(rbacAdmin).addRole("test/role1", new Property[0]);            
            atLeast(1).of(sp).getAuthenticationProvider("test");will(returnValue(ap));

            atLeast(1).of(ap).getAdmin();will(returnValue(authAdmin));
            atLeast(1).of(authAdmin).setUserCredentials("test/user1", new Property[0]);
            atLeast(1).of(rbacAdmin).setUserProperties("test/user2", new Property[0]);
            atLeast(1).of(rbacAdmin).setRoleProperties("test/role1",  new Property[0]);
            atLeast(1).of(rbac).getQuery();will(returnValue(rbacQuery));
            atLeast(1).of(rbacQuery).userProperties("test/user2");will(returnValue(new Property[0]));
            atLeast(1).of(sp).getAuthenticationProvider("");will(returnValue(ap));
            atLeast(1).of(ap).getQuery();will(returnValue(authQuery));
            atLeast(1).of(authQuery).getUserCredentials("test/user1");will(returnValue(new Property[0]));
            atLeast(1).of(sp).getAuthenticationProvider("intalio");will(returnValue(ap));
            atLeast(1).of(ap).getRuntime();will(returnValue(authRuntime));
            atLeast(1).of(rbacAdmin).assignUser("test/user1", "test/role1");            
            atLeast(1).of(rbacQuery).assignedUsers("test/role1");will(returnValue(new String[]{"test/user1"}));
            atLeast(1).of(rbacQuery).assignedRoles("test/user1");will(returnValue(new String[]{"test/role1"}));           
            atLeast(1).of(rbacQuery).roleProperties("test/role1");will(returnValue(new Property[0]));
            atLeast(1).of(rbacAdmin).deassignUser("test/user1", "test/role1");
            atLeast(1).of(rbacAdmin).addAscendant("test/role2", new Property[0], "test/role1");
            atLeast(1).of(rbacAdmin).addDescendant("test/role3", new Property[0], "test/role1");
            atLeast(1).of(rbacQuery).descendantRoles("test/role2");will(returnValue(new String[]{"test/role1", "test/role3"}));
            atLeast(1).of(rbacQuery).ascendantRoles("test/role3");will(returnValue(new String[]{"test/role2", "test/role1"}));
            atLeast(1).of(rbacAdmin).grantPermission("test/role1", "read", "secret doc");
            atLeast(1).of(rbacQuery).roleOperationsOnObject("test/role1", "secret doc");will(returnValue(new String[]{"read"}));
            atLeast(1).of(rbacQuery).userOperationsOnObject("test/user1", "secret doc");will(returnValue(new String[]{"read"}));
            atLeast(1).of(rbacAdmin).deleteRole("test/role1");
            atLeast(1).of(rbacAdmin).deleteUser("test/user1");
            atLeast(1).of(rbacQuery).authorizedUsers("test/role1");will(returnValue(new String[]{"test/user1"}));
            atLeast(1).of(rbacQuery).topRoles("test");will(returnValue(new String[]{"test/role2"}));
            atLeast(1).of(rbacAdmin).addInheritance("test/role2", "test/role1");
            atLeast(1).of(rbacAdmin).deleteInheritance("test/role2", "test/role1");
            atLeast(1).of(rbacAdmin).revokePermission("test/role1", "read", "secret doc");
            
            atLeast(1).of(rbac).getRuntime();will(returnValue(rbacRuntime));
            atLeast(1).of(rbacRuntime).checkAccess("test/uesr1", new String[]{"test/role1"}, "read", "secret doc");will(returnValue(true));
      
        }
        });

        // create Realms
        Realms realms = new Realms();
        ArrayList<SecurityProvider> list = new ArrayList<SecurityProvider>();
        list.add(sp);
        realms.setSecurityProviders(list);
        realms.setDefaultRealm("intalio");
        assertTrue(realms.getSecurityProvider(null) == (realms.getSecurityProvider("intalio")));
        assertTrue(realms.getSecurityProviders().size() > 0);
        assertTrue(realms.getRealmIdentifiers().length > 0);
        
        
        
        
        // user and role   
        Property[] properties = new Property[0];
        realms.addUser("intalio/eng1", properties);
        realms.addRole("test/role1", properties);

        Property[] credentials = new Property[0];
        realms.setUserCredentials("test/user1", credentials);
        realms.setUserProperties("test/user2", properties);
        realms.setRoleProperties("test/role1", properties);
        assertNotNull(realms.userProperties("test/user2"));
        assertNotNull(realms.getAuthenticationProvider("intalio"));
        assertNotNull(realms.getUserCredentials("test/user1"));
        assertNotNull(realms.getAuthenticationRuntime("test"));
        assertNotNull(realms.roleProperties("test/role1"));
        assertNotNull(realms.roleProperties("test/role1"));

        realms.assignUser("test/user1", "test/role1");
        assertTrue(realms.assignedUsers("test/role1").length>0);
        assertTrue(realms.assignedRoles("test/user1").length>0);
        realms.deassignUser("test/user1", "test/role1");
        assertTrue(realms.authorizedUsers("test/role1").length>0);
        assertTrue(realms.topRoles("test").length >0);
      
        
        
        // ascendant and descendant
        realms.addAscendant("test/role2", new Property[0], "test/role1");
        realms.addDescendant("test/role3", properties, "test/role1");
        assertTrue(realms.descendantRoles("test/role2").length == 2);
        assertTrue(realms.ascendantRoles("test/role3").length == 2);
        realms.addInheritance("test/role2", "test/role1");
        realms.deleteInheritance("test/role2", "test/role1");

        
        // permission 
        realms.grantPermission("test/role1", "read", "secret doc");
        
        assertTrue(realms.roleOperationsOnObject("test/role1", "secret doc").length>0);
        assertTrue(realms.userOperationsOnObject("test/user1", "secret doc").length>0);
        realms.revokePermission("test/role1", "read", "secret doc");
        assertTrue(realms.checkAccess("test/uesr1", new String[]{"test/role1"}, "read", "secret doc"));
        
    
        // finish
        realms.deleteRole("test/role1");
        realms.deleteUser("test/user1");
             
    }
    // private SimpleSecurityProvider createSimpleSecurityProvider() throws
    // Exception{
    // SimpleSecurityProvider provider;
    // HashMap<String,String> props;
    //        
    // props = new HashMap<String,String>();
    // props.put( "configFile", "testSimpleSecurity.xml" );
    // SimpleSecurityProvider sp = new SimpleSecurityProvider();
    // sp.initialize(props);
    // return sp;
    // }

    // @Specification
    // public Realms createRealms() throws Exception{
    // Realms realms = new Realms();
    //        
    // ArrayList<SecurityProvider> list = new ArrayList<SecurityProvider>();
    //        
    // SimpleSecurityProvider sp = createSimpleSecurityProvider();
    // list.add(sp);
    // realms.setSecurityProviders(list);
    // realms.setDefaultRealm("intalio");
    // assertTrue(realms.getSecurityProvider(null) ==
    // (realms.getSecurityProvider("intalio")));
    // 
    //        
    // //realms.getRBACAdmin(identifier);
    //        
    // Property[] properties = new Property[0];
    // realms.addUser("intalio/eng1", properties);
    // // realms.addRole("test/role1", properties);
    //        
    // Property[] credentials = new Property[0];
    // realms.setUserCredentials("test/user1", credentials);
    // realms.setUserProperties("test/user2", properties);
    //        
    // assertNotNull(realms.getAuthenticationProvider("intalio"));
    //        
    //
    // return realms;
    //        
    //        
    // }

}
