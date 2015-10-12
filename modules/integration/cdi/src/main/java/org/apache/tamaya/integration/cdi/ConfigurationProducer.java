/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tamaya.integration.cdi;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.PropertyConverter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Producer bean for configuration properties.
 */
@ApplicationScoped
public class ConfigurationProducer {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationProducer.class.getName());

    @Produces
    @ConfigProperty
    public Object resolveAndConvert(final InjectionPoint injectionPoint) {

        final ConfigProperty annotation = injectionPoint.getAnnotated().getAnnotation(ConfigProperty.class);
        final ConfigDefault defaultAnnot = injectionPoint.getAnnotated().getAnnotation(ConfigDefault.class);
        final ConfigDefaultSections typeAnnot = injectionPoint.getAnnotated().getAnnotation(ConfigDefaultSections.class);
        final List<String> keys = ConfigurationExtension.evaluateKeys(injectionPoint.getMember().getName(),
                annotation!=null?annotation.value():null,
                typeAnnot!=null?typeAnnot.value():null);

        final WithConfigOperator withOperatorAnnot = injectionPoint.getAnnotated().getAnnotation(WithConfigOperator.class);
        ConfigOperator operator = null;
        if(withOperatorAnnot!=null){
            operator = ConfigurationExtension.CUSTOM_OPERATORS.get(withOperatorAnnot.value());
        }
        PropertyConverter customCnverter = null;
        final WithPropertyConverter withConverterAnnot = injectionPoint.getAnnotated().getAnnotation(WithPropertyConverter.class);
        if(withConverterAnnot!=null){
            customCnverter = ConfigurationExtension.CUSTOM_CONVERTERS.get(withConverterAnnot.value());
        }

        // unless the extension is not installed, this should never happen because the extension
        // enforces the resolvability of the config
        Configuration config = ConfigurationProvider.getConfiguration();
        if(operator!=null){
            config = operator.operate(config);
        }
        final Class<?> toType = (Class<?>) injectionPoint.getAnnotated().getBaseType();
        String textValue = null;
        String defaultTextValue = defaultAnnot!=null?defaultAnnot.value():null;
        String keyFound = null;
        for(String key:keys) {
            textValue = config.get(key);
            if(textValue!=null){
                keyFound = key;
                break;
            }
        }
        Object value = null;
        if(keyFound!=null){
            if(customCnverter!=null) {
                value = customCnverter.convert(textValue);
            }
            if(value==null){
                value = config.get(keyFound, toType);
            }
        } else if(defaultTextValue!=null){
            if(customCnverter!=null) {
                value = customCnverter.convert(defaultTextValue);
            }
            if(value==null) {
                List<PropertyConverter<Object>> converters = ConfigurationProvider.getConfigurationContext()
                        .getPropertyConverters(TypeLiteral.of(toType));
                for (PropertyConverter<Object> converter : converters) {
                    try {
                        value = converter.convert(defaultTextValue);
                        if (value != null) {
                            LOGGER.log(Level.FINEST, "Parsed default value from '" + defaultTextValue + "' into " +
                                    injectionPoint);
                            break;
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.FINEST, "Failed to convert default value '" + defaultTextValue + "' for " +
                                injectionPoint, e);
                    }
                }
            }
        }
        if(value==null){
            throw new ConfigException(String.format(
                    "Can't resolve any of the possible config keys: %s to the required target type: %s",
                    keys.toString(), toType.getName()));
        }
        LOGGER.finest(String.format("Injecting %s for key %s in class %s", keyFound, value.toString(), injectionPoint.toString()));
        return value;
    }

}
