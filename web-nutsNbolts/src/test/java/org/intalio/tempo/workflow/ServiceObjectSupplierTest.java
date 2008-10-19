package org.intalio.tempo.workflow;

import junit.framework.Assert;

import org.apache.axis2.description.AxisService;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.BeforeSpecification;
import com.googlecode.instinct.marker.annotate.Specification;
import com.googlecode.instinct.marker.annotate.Subject;

@RunWith(InstinctRunner.class)
public class ServiceObjectSupplierTest {
    final static ExpectThat expect = new ExpectThatImpl();
    private AxisService as;
    
    @Subject
    ServiceObjectSupplier sos;
    
    @BeforeSpecification
    void before() throws Exception {
        sos = new ServiceObjectSupplier();
        as = new AxisService();
        as.addParameter("SpringContextFile", "file:src/test/resources/tempo-tas.xml");
        
    }
    
    @Specification
    void testGetServiceObject() throws Exception{
        new SpringInit().startUp(null, as);
        as.addParameter("SpringBeanName", "tas.serviceImplementation");
        Object bean = sos.getServiceObject(as);
        Assert.assertEquals(bean.getClass().getName(), "org.intalio.tempo.workflow.FakeTASAxis2SingleInstanceFacade");
    }
    
    @Specification
    void testGetServiceObject2() throws Exception{
        as.addParameter("LoadOnStartup", "true");
        as.addParameter("SpringBeanName", "tas.serviceImplementation");
        new SpringInit().startUp(null, as);
        Object bean = sos.getServiceObject(as);
        Assert.assertEquals(bean.getClass().getName(), "org.intalio.tempo.workflow.FakeTASAxis2SingleInstanceFacade");
    }
    
    @Specification
    void testGetFactoryBean() throws Exception {
        as.addParameter("SpringBeanFactory", "test.factoryBean");
        as.addParameter("SpringBeanName", "tas.serviceImplementation");
        new SpringInit().startUp(null, as);
        Object bean = sos.getServiceObject(as);
        Assert.assertEquals(bean.getClass().getName(), "org.intalio.tempo.workflow.FakeTASAxis2SingleInstanceFacade");
        new SpringInit().shutDown(null, null);
    }
}
