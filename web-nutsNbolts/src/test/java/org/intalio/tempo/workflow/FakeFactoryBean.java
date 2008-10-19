package org.intalio.tempo.workflow;

import org.springframework.beans.factory.FactoryBean;

public class FakeFactoryBean implements FactoryBean {

    public Object getObject() throws Exception {
        return new Object();
    }

    public Class getObjectType() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isSingleton() {
        // TODO Auto-generated method stub
        return false;
    }

}
