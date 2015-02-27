package org.apache.tamaya.events.tests;

import org.apache.tamaya.ConfigurationProvider;
import org.junit.Test;

/**
 * Created by Anatole on 25.02.2015.
 */
public class ObservedConfigTest {

    @Test
    public void testInitialConfig(){
        for(int i=0;i<100;i++){
            System.out.println("1: " + ConfigurationProvider.getConfiguration().get("1"));
            System.out.println("2: " + ConfigurationProvider.getConfiguration().get("2"));
            System.out.println("3: " + ConfigurationProvider.getConfiguration().get("3"));
            System.out.println("4: " + ConfigurationProvider.getConfiguration().get("4"));
            System.out.println("5: " + ConfigurationProvider.getConfiguration().get("5"));
            System.out.println("6: " + ConfigurationProvider.getConfiguration().get("6"));
            System.out.println("=======================================================================");
            try{
                Thread.sleep(2000L);
            }
            catch(Exception e){
                // ignore
                e.printStackTrace();
            }
        }

    }
}
