/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.model.internal;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.model.ConfigModel;
import org.apache.tamaya.model.spi.ConfigModelReader;
import org.apache.tamaya.model.spi.ModelProviderSpi;
import org.apache.tamaya.spisupport.BasePropertySource;
import org.apache.tamaya.spisupport.MapPropertySource;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ConfigModel provider that reads model metadata from property files from
 * {@code classpath*:META-INF/configmodel.properties} in the following format:
 * <pre>
 * ###################################################################################
 * # Example of a configuration metamodel expressed via properties.
 * ####################################################################################
 *
 * # Metamodel information
 * [model].provider=ConfigModel Extension
 *
 * ####################################################################################
 * # Description of Configuration Sections (minimal, can be extended by other modules).
 * # By default its interpreted as a section !
 * ####################################################################################
 *
 * # a (section)
 * {model}a.class=Section
 * {model}a.params2.class=Parameter
 * {model}a.params2.type=String
 * {model}a.params2.required=true
 * {model}a.params2.description=a required parameter
 *
 * {model}a.paramInt.class=Parameter
 * {model}a.paramInt.ref=MyNumber
 * {model}a.paramInt.description=an optional parameter (default)
 *
 * {model}a._number.class=Parameter
 * {model}a._number.type=Integer
 * {model}a._number.deprecated=true
 * {model}a._number.mappedTo=a.paramInt
 *
 * # a.b.c (section)
 * {model}a.b.c.class=Section
 * {model}a.b.c.description=Just a test section
 *
 * # a.b.c.aRequiredSection (section)
 * {model}a.b.c.aRequiredSection.class=Section
 * {model}a.b.c.aRequiredSection.required=true
 * {model}a.b.c.aRequiredSection.description=A section containing required parameters is called a required section.\
 * Sections can also explicitly be defined to be required, but without\
 * specifying the paramteres to be contained.,
 *
 * # a.b.c.aRequiredSection.subsection (section)
 * {model}a.b.c.aRequiredSection.subsection.class=Section
 *
 * {model}a.b.c.aRequiredSection.subsection.param0.class=Parameter
 * {model}a.b.c.aRequiredSection.subsection.param0.type=String
 * {model}a.b.c.aRequiredSection.subsection.param0.description=a minmally documented String parameter
 * # A minmal String parameter
 * {model}a.b.c.aRequiredSection.subsection.param00.class=Parameter
 * {model}a.b.c.aRequiredSection.subsection.param00.type=String
 *
 * # a.b.c.aRequiredSection.subsection (section)
 * {model}a.b.c.aRequiredSection.subsection.param1.class=Parameter
 * {model}a.b.c.aRequiredSection.subsection.param1.type = String
 * {model}a.b.c.aRequiredSection.subsection.param1.required = true
 * {model}a.b.c.aRequiredSection.subsection.intParam.class=Parameter
 * {model}a.b.c.aRequiredSection.subsection.intParam.type = Integer
 * {model}a.b.c.aRequiredSection.subsection.intParam.description=an optional parameter (default)
 *
 * # a.b.c.aRequiredSection.nonempty-subsection (section)
 * {model}a.b.c.aRequiredSection.nonempty-subsection.class=Section
 * {model}a.b.c.aRequiredSection.nonempty-subsection.required=true
 *
 * # a.b.c.aRequiredSection.optional-subsection (section)
 * {model}a.b.c.aRequiredSection.optional-subsection.class=Section
 *
 * # a.b.c.aValidatedSection (section)
 * {model}a.b.c.aValidatedSection.class=Section
 * {model}a.b.c.aValidatedSection.description=A validated section.
 * {model}a.b.c.aValidatedSection.configModels=org.apache.tamaya.model.TestValidator
 * </pre>
 */
public class ConfiguredPropertiesModelProviderSpi implements ModelProviderSpi {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(ConfiguredPropertiesModelProviderSpi.class.getName());
    /** parameter to disable this provider. By default the provider is active. */
    private static final String MODEL_EANABLED_PARAM = "org.apache.tamaya.model.default.enabled";
    /** The configModels read. */
    private List<ConfigModel> configModels = new ArrayList<>();

    public ConfiguredPropertiesModelProviderSpi() {
        String enabledVal = ConfigurationProvider.getConfiguration().get(MODEL_EANABLED_PARAM);
        boolean enabled = enabledVal == null || "true".equalsIgnoreCase(enabledVal);
        if(!enabled){
            LOG.info("Reading model data from META-INF/configmodel.properties has been disabled.");
            return;
        }
        try {
            LOG.info("Reading model data from META-INF/configmodel.properties...");
            Enumeration<URL> configs = getClass().getClassLoader().getResources("META-INF/configmodel.properties");
            while (configs.hasMoreElements()) {
                URL config = configs.nextElement();
                try (InputStream is = config.openStream()) {
                    Properties props = new Properties();
                    props.load(is);
                    Map<String,String> data = MapPropertySource.getMap(props);
                    configModels.addAll(ConfigModelReader.loadValidations(
                            data));
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                            "Error loading config metadata from " + config, e);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE,
                    "Error loading config metadata from META-INF/configmodel.properties", e);
        }
        configModels = Collections.unmodifiableList(configModels);
    }


    public Collection<ConfigModel> getConfigModels() {
        return configModels;
    }
}
