package org.intalio.tempo.security.impl;

import junit.framework.TestCase;

import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.rbac.RBACAdmin;
import org.jmock.Expectations;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Mock;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Stub;
import com.googlecode.instinct.marker.annotate.Subject;
@RunWith(InstinctRunner.class)
public class RBACAdminImplTest extends TestCase {
    
    protected transient Logger _log = LoggerFactory.getLogger(getClass());
    final static ExpectThat expect = new ExpectThatImpl();
    @Subject RBACAdminImpl rbac;
    @Mock Realms provider;
    @Mock RBACAdmin admin;
    @Stub(auto = false)   Property[] properties = new Property[0];
    @Stub(auto = false)String role= "test/role1";
    @Stub(auto = false)String user = "test/user1";
    @Stub(auto = false)String operation="read";
    @Stub(auto = false)String object = "doc";
    
    @Specification
    public void testAll()throws Exception{

      
        
        expect.that(new Expectations(){{
            atLeast(1).of(provider).getRBACAdmin("test/role1");will(returnValue(admin));
            atLeast(1).of(admin).addRole("test/role1", properties);
            atLeast(1).of(provider).getRBACAdmin(user);will(returnValue(admin));
            atLeast(1).of(admin).addUser(user, properties);
            atLeast(1).of(admin).assignUser(user, role);
            atLeast(1).of(admin).deassignUser(user, role);
            atLeast(1).of(admin).grantPermission(role, operation, object);
            atLeast(1).of(admin).revokePermission(role, operation, object);
            atLeast(1).of(provider).getRBACAdmin("test/manager");will(returnValue(admin));
            atLeast(1).of(admin).addAscendant("test/manager", properties, role);
            atLeast(1).of(provider).getRBACAdmin("test/worker");will(returnValue(admin));
            atLeast(1).of(admin).addDescendant("test/worker", properties, role);
            atLeast(1).of(admin).addInheritance("test/manager", "test/worker");
            atLeast(1).of(admin).deleteInheritance("test/manager", "test/worker");
            atLeast(1).of(admin).setUserProperties(user, properties);
            atLeast(1).of(admin).setRoleProperties(role, properties);
        }});
        rbac = new RBACAdminImpl(provider);

        rbac.addRole(role, properties);
        rbac.addUser(user, properties);
        rbac.assignUser(user, role);
        rbac.deassignUser(user, role);
        rbac.grantPermission(role, operation, object);
        rbac.revokePermission(role, operation, object);
        rbac.addAscendant("test/manager", properties, role);
        rbac.addDescendant("test/worker", properties, role);
        rbac.addInheritance("test/manager", "test/worker");
        rbac.deleteInheritance("test/manager", "test/worker");
        rbac.setUserProperties(user, properties);
        rbac.setRoleProperties(role, properties);
     }
}
