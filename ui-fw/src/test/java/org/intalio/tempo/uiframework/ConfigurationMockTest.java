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
        outputStringArray(_configuration.getTaskIconSetByRole(new String[]{"intalio\\ProcessManager"}));
        outputStringArray(_configuration.getTaskIconSetByRole(new String[]{"intalio\\Employee"}));
        outputStringArray(_configuration.getTaskIconSetByRole(new String[]{"intalio\\ProcessManager", "intalio\\ProcessManager", "intalio\\Employee"}));
        outputStringArray(_configuration.getTaskIconSetByRole(new String[]{"examples\\employee"}));
        outputStringArray(_configuration.getNotificationIconSetByRole(new String[]{"intalio\\ProcessManager"}));
        outputStringArray(_configuration.getNotificationIconSetByRole(new String[]{"intalio\\Employee"}));
        outputStringArray(_configuration.getNotificationIconSetByRole(new String[]{"intalio\\ProcessManager", "intalio\\Employee", "intalio\\ProcessManager"}));
    }

    @Test
    public void testConvertIconsToHTMLCode(){
        System.out.println(_configuration.convertIconsToHTMLCode(new String[]{"export","delete", "reassign", "claim", "update", "skip"}));
    }
    
    private void outputStringArray(String[] array){
        for(int i = 0; i < array.length; i++){
            System.out.print(array[i] + ",");
        }
        System.out.println("end");
    }
}
