package org.apache.tamaya.resolver;

import org.apache.tamaya.Configuration;
import org.junit.Test;

/**
 * Created by Anatole on 04.01.2015.
 */
public class MyResolutionTest {

    @Test
    public void testConfig(){
        System.out.println(Configuration.current().getProperties());
    }
}
