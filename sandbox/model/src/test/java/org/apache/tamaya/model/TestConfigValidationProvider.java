package org.apache.tamaya.model;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.model.spi.AreaValidation;
import org.apache.tamaya.model.spi.ParameterValidation;
import org.apache.tamaya.model.spi.ValidationGroup;
import org.apache.tamaya.model.spi.ValidationProviderSpi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Anatole on 09.08.2015.
 */
public class TestConfigValidationProvider implements ValidationProviderSpi{

    private List<Validation> validations = new ArrayList<>(1);

    public TestConfigValidationProvider(){
        validations.add(new TestConfigValidation());
        validations = Collections.unmodifiableList(validations);
    }

    @Override
    public Collection<Validation> getValidations() {
        return validations;
    }

    private static final class TestConfigValidation extends ValidationGroup{

        public TestConfigValidation(){
            super("TestConfig", new AreaValidation.Builder("a.test.existing").setRequired(true).build(),
                    ParameterValidation.of("a.test.existing.aParam", true),
                    ParameterValidation.of("a.test.existing.optionalParam"),
                    ParameterValidation.of("a.test.existing.aABCParam", false, "[ABC].*"),
                    new AreaValidation.Builder("a.test.notexisting").setRequired(true).build(),
                    ParameterValidation.of("a.test.notexisting.aParam", true),
                    ParameterValidation.of("a.test.notexisting.optionalParam"),
                    ParameterValidation.of("a.test.existing.aABCParam2", false, "[ABC].*"));
        }
        @Override
        public String getName() {
            return "TestConfigValidation";
        }

    }
}
