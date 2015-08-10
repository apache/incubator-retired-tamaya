package org.apache.tamaya.model;

import org.junit.Test;

/**
 * Created by Anatole on 10.08.2015.
 */
public class ValidationTests {

    @Test
    public void testDefaults(){
        System.err.println(ConfigValidator.validate());
    }

    @Test
    public void testAllValidations(){
        System.err.println(ConfigValidator.getValidations());
    }

    @Test
    public void testAllValidationsInclUndefined(){
        System.err.println("Inclusing UNDEFINED: \n" + ConfigValidator.validate(true));
    }
}
