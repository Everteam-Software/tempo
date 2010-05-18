package org.intalio.tempo.uiframework;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(locations={"/tempo-ui-fw.xml"})

public class ConfigurationMockTest {
    @Autowired  
    private Configuration _configuration;
    
    @Test
    public void testToolbarIconSets(){
        System.out.println("start...");
        System.out.println(_configuration.getToolbarIconSets());
        System.out.println(_configuration.getTaskIconSetByRole(new String[]{"administrator"}));
        System.out.println(_configuration.getTaskIconSetByRole(new String[]{"employee"}));
        System.out.println(_configuration.getTaskIconSetByRole(new String[]{"employee", "administrator", "employee"}));
        System.out.println(_configuration.getNotificationIconSetByRole(new String[]{"administrator"}));
        System.out.println(_configuration.getNotificationIconSetByRole(new String[]{"employee"}));
        System.out.println(_configuration.getNotificationIconSetByRole(new String[]{"employee", "administrator"}));
    }

}

