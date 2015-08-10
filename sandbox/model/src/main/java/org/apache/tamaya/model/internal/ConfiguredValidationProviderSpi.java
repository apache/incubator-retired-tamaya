package org.apache.tamaya.model.internal;

import org.apache.tamaya.model.Validation;
import org.apache.tamaya.model.spi.AreaValidation;
import org.apache.tamaya.model.spi.ParameterValidation;
import org.apache.tamaya.model.spi.ValidationProviderSpi;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validation provider that reads model metadata from property files with the following format:
 * <pre>
 * ###################################################################################
 * # Example of a configuration metamodel expressed via properties.
 * ####################################################################################
 *
 * # Metamodel information
 * [model].provider=ConfigModel Extension
 *
 * # reusable parameter definition, referenceable as MyNumber
 * [MyNumber].class=Parameter
 * [MyNumber].type=Integer
 * [MyNumber].description=a (reusable) number type parameter (optional)
 *
 * ####################################################################################
 * # Description of Configuration Sections (minimal, can be extended by other modules).
 * # By default its interpreted as a section !
 * ####################################################################################
 *
 * # a (section)
 * a.class=Section
 * a.params2.class=Parameter
 * a.params2.type=String
 * a.params2.required=true
 * a.params2.description=a required parameter
 *
 * a.paramInt.class=Parameter
 * a.paramInt.ref=MyNumber
 * a.paramInt.description=an optional parameter (default)
 *
 * a._number.class=Parameter
 * a._number.type=Integer
 * a._number.deprecated=true
 * a._number.mappedTo=a.paramInt
 *
 * # a.b.c (section)
 * a.b.c.class=Section
 * a.b.c.description=Just a test section
 *
 * # a.b.c.aRequiredSection (section)
 * a.b.c.aRequiredSection.class=Section
 * a.b.c.aRequiredSection.required=true
 * a.b.c.aRequiredSection.description=A section containing required parameters is called a required section.\
 * Sections can also explicitly be defined to be required, but without\
 * specifying the paramteres to be contained.,
 *
 * # a.b.c.aRequiredSection.subsection (section)
 * a.b.c.aRequiredSection.subsection.class=Section
 *
 * a.b.c.aRequiredSection.subsection.param0.class=Parameter
 * a.b.c.aRequiredSection.subsection.param0.type=String
 * a.b.c.aRequiredSection.subsection.param0.description=a minmally documented String parameter
 * # A minmal String parameter
 * a.b.c.aRequiredSection.subsection.param00.class=Parameter
 * a.b.c.aRequiredSection.subsection.param00.type=String
 *
 * # a.b.c.aRequiredSection.subsection (section)
 * a.b.c.aRequiredSection.subsection.param1.class=Parameter
 * a.b.c.aRequiredSection.subsection.param1.type = String
 * a.b.c.aRequiredSection.subsection.param1.required = true
 * a.b.c.aRequiredSection.subsection.intParam.class=Parameter
 * a.b.c.aRequiredSection.subsection.intParam.type = Integer
 * a.b.c.aRequiredSection.subsection.intParam.description=an optional parameter (default)
 *
 * # a.b.c.aRequiredSection.nonempty-subsection (section)
 * a.b.c.aRequiredSection.nonempty-subsection.class=Section
 * a.b.c.aRequiredSection.nonempty-subsection.required=true
 *
 * # a.b.c.aRequiredSection.optional-subsection (section)
 * a.b.c.aRequiredSection.optional-subsection.class=Section
 *
 * # a.b.c.aValidatedSection (section)
 * a.b.c.aValidatedSection.class=Section
 * a.b.c.aValidatedSection.description=A validated section.
 * a.b.c.aValidatedSection.validations=org.apache.tamaya.model.TestValidator
 * </pre>
 */
public class ConfiguredValidationProviderSpi implements ValidationProviderSpi {

    private List<Validation> validations = new ArrayList<>();

    public ConfiguredValidationProviderSpi() {
        try {
            Enumeration<URL> configs = getClass().getClassLoader().getResources("META-INF/configmodel/configmodel.properties");
            while (configs.hasMoreElements()) {
                URL config = configs.nextElement();
                try (InputStream is = config.openStream()) {
                    Properties props = new Properties();
                    props.load(is);
                    loadValidations(props, config.toString());
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                            "Error loading config metadata from " + config, e);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    "Error loading config metadata from META-INF/configmodel/configmodel.properties", e);
        }
        validations = Collections.unmodifiableList(validations);
    }

    private void loadValidations(Properties props, String resource) {
        String provider = props.getProperty("[model].provider");
        if (provider == null) {
            provider = resource;
        }
        Set<String> itemKeys = new HashSet<>();
        for (Object key : props.keySet()) {
            if (key.toString().endsWith(".class")) {
                itemKeys.add(key.toString().substring(0, key.toString().length() - ".class".length()));
            }
        }
        for (String baseKey : itemKeys) {
            String clazz = props.getProperty(baseKey + ".class");
            String type = props.getProperty(baseKey + ".type");
            if(type==null){
                type = String.class.getName();
            }
            String description = props.getProperty(baseKey + ".description");
            String regEx = props.getProperty(baseKey + ".expression");
            String validations = props.getProperty(baseKey + ".validations");
            String requiredVal = props.getProperty(baseKey + ".required");
            if ("Parameter".equalsIgnoreCase(clazz)) {
                initParameter(baseKey, description, type, requiredVal, regEx, validations);
            } else if ("Section".equalsIgnoreCase(clazz)) {
                initSection(baseKey, description, requiredVal, validations);
            }
        }
    }

    private void initParameter(String name, String desc, String type, String reqVal, String regEx, String validations) {
        boolean required = "true".equalsIgnoreCase(reqVal);
        ParameterValidation.Builder builder = ParameterValidation.builder(name).setRequired(required)
                .setDescription(desc).setExpression(regEx).setType(type);
        if (validations != null) {
            try {
                // TODO defined validator API
//                builder.addValidations(validations);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Failed to load validations for " + name, e);
            }
        }
        this.validations.add(builder.build());
    }

    private void initSection(String name, String desc, String reqVal, String validations) {
        boolean required = "true".equalsIgnoreCase(reqVal);
        AreaValidation.Builder builder = AreaValidation.builder(name).setRequired(required)
                .setDescription(desc);
        if (validations != null) {
            try {
                // TODO defined validator API
//                builder.addValidations(validations);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Failed to load validations for " + name, e);
            }
        }
        this.validations.add(builder.build());
    }


    @Override
    public Collection<Validation> getValidations() {
        return validations;
    }
}
