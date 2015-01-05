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
package org.apache.tamaya.inject.internal;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.inject.ConfiguredProperties;
import org.apache.tamaya.inject.ConfiguredProperty;
import org.apache.tamaya.inject.DefaultAreas;
import org.apache.tamaya.inject.DefaultValue;
import org.apache.tamaya.inject.WithLoadPolicy;
import org.apache.tamaya.inject.WithPropertyConverter;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.ServiceContext;

/**
 * Created by Anatole on 19.12.2014.
 */
@SuppressWarnings("unchecked")
final class InjectionUtils {

    private static final boolean resolutionModuleLoaded = checkResolutionModuleLoaded();

    private static boolean checkResolutionModuleLoaded() {
        try{
            Class.forName("org.apache.tamaya.resolver.internal.DefaultExpressionEvaluator");
            return true;
        }
        catch(ClassNotFoundException e){
            return false;
        }
    }

    private InjectionUtils(){}

    /**
     * Evaluates all absolute configuration key based on the annotations found on a class.
     *
     * @param areasAnnot          the (optional) annotation definining areas to be looked up.
     * @param propertyAnnotation  the annotation on field/method level that may defined one or
     *                            several keys to be looked up (in absolute or relative form).
     * @return the list current keys in order how they should be processed/looked up.
     */
    public static List<String> evaluateKeys(Member member, DefaultAreas areasAnnot, ConfiguredProperty propertyAnnotation) {
        List<String> keys = new ArrayList<>(Arrays.asList(propertyAnnotation.keys()));
        if (keys.isEmpty()) //noinspection UnusedAssignment
            keys.add(member.getName());
        ListIterator<String> iterator = keys.listIterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.startsWith("[") && next.endsWith("]")) {
                // absolute key, strip away brackets, take key as is
                iterator.set(next.substring(1, next.length() - 1));
            } else {
                if (areasAnnot != null) {
                    // Remove original entry, since it will be replaced with prefixed entries
                    iterator.remove();
                    // Add prefixed entries, including absolute (root) entry for "" area keys.
                    for (String area : areasAnnot.value()) {
                        iterator.add(area.isEmpty() ? next : area + '.' + next);
                    }
                }
            }
        }
        return keys;
    }

    /**
     * Evaluates all absolute configuration key based on the member name found.
     *
     * @param areasAnnot          the (optional) annotation definining areas to be looked up.
     * @return the list current keys in order how they should be processed/looked up.
     */
    public static List<String> evaluateKeys(Member member, DefaultAreas areasAnnot) {
        List<String> keys = new ArrayList<>();
        String name = member.getName();
        String mainKey;
        if(name.startsWith("get") || name.startsWith("set")){
            mainKey = Character.toLowerCase(name.charAt(3)) + name.substring(4);
        }
        else{
            mainKey = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        keys.add(mainKey);
        if (areasAnnot != null) {
            // Add prefixed entries, including absolute (root) entry for "" area keys.
            for (String area : areasAnnot.value()) {
                if(!area.isEmpty()) {
                    keys.add(area + '.' + mainKey);
                }
            }
        }
        else{ // add package name
            keys.add(member.getDeclaringClass().getName()+'.'+mainKey);
        }
        return keys;
    }

    /**
     * Internally evaluated the current valid configuration keys based on the given annotations present.
     *
     * @return the keys to be returned, or null.
     */
    public static String getConfigValue(Method method, Configuration... configurations) {
        DefaultAreas areasAnnot = method.getDeclaringClass().getAnnotation(DefaultAreas.class);
        WithLoadPolicy loadPolicy = Utils.getAnnotation(WithLoadPolicy.class, method, method.getDeclaringClass());
        return getConfigValueInternal(method, areasAnnot, loadPolicy, configurations);
    }


    /**
     * Internally evaluated the current valid configuration keys based on the given annotations present.
     *
     * @return the keys to be returned, or null.
     */
    public static String getConfigValue(Field field, Configuration... configurations) {
        DefaultAreas areasAnnot = field.getDeclaringClass().getAnnotation(DefaultAreas.class);
        WithLoadPolicy loadPolicy = Utils.getAnnotation(WithLoadPolicy.class, field, field.getDeclaringClass());
        return getConfigValueInternal(field, areasAnnot, loadPolicy, configurations);
    }

    /**
     * Internally evaluated the current valid configuration keys based on the given annotations present.
     *
     * @return the keys to be returned, or null.
     */
    private static String getConfigValueInternal(AnnotatedElement element, DefaultAreas areasAnnot, WithLoadPolicy loadPolicy, Configuration... configurations) {
        Collection<ConfiguredProperty> configuredProperties = Utils.getAnnotations(
                element, ConfiguredProperty.class, ConfiguredProperties.class);
        DefaultValue defaultAnnot = element.getAnnotation(DefaultValue.class);
        String configValue = null;
        if(configuredProperties.isEmpty()){
            List<String> keys = InjectionUtils.evaluateKeys((Member)element, areasAnnot);
            Configuration config = InjectionUtils.getConfiguration("default", configurations);
            configValue = evaluteConfigValue(configValue, keys, config);
        }
        else {
            for (ConfiguredProperty prop : configuredProperties) {
                List<String> keys = InjectionUtils.evaluateKeys((Member) element, areasAnnot, prop);
                Configuration config = InjectionUtils.getConfiguration(prop, configurations);
                configValue = evaluteConfigValue(configValue, keys, config);
            }
        }
        if (configValue == null && defaultAnnot != null) {
            return defaultAnnot.value();
        }
        return configValue;
    }

    private static String evaluteConfigValue(String configValue, List<String> keys, Configuration config) {
        for (String key : keys) {
            configValue = config.get(key).orElse(null);
            if (configValue != null) {
                break;
            }
        }
        if (configValue != null) {
            // net step perform expression resolution, if any
            configValue =  Configuration.evaluateValue(configValue, config);
        }
        return configValue;
    }


    @SuppressWarnings("rawtypes")
	public static <T> T adaptValue(AnnotatedElement element, Class<T> targetType, String configValue){
        try {
            // Check for adapter/filter
//            T adaptedValue = null;
            WithPropertyConverter codecAnnot = element.getAnnotation(WithPropertyConverter.class);
            Class<? extends WithPropertyConverter> codecType;
            if (codecAnnot != null) {
                codecType = codecAnnot.value();
                if (!codecType.equals(WithPropertyConverter.class)) {
                    // TODO cache here...
//                    Codec<String> codec = codecType.newInstance();
//                    adaptedValue = (T) codec.adapt(configValue);
                }
            }
            if (String.class.equals(targetType)) {
                 return (T)configValue;
            } else {
                PropertyConverter<?> adapter = PropertyConverter.getInstance(targetType);
                 return (T)adapter.convert(configValue);
            }
        } catch (Exception e) {
            throw new ConfigException("Failed to annotate configured member: " + element, e);
        }
    }

    /**
     * This method evaluates the {@link Configuration} that currently is valid for the given target field/method.
     * @param configurations Configuration instances that replace configuration served by services. This allows
     *                       more easily testing and adaption.
     * @return the {@link Configuration} instance to be used, never null.
     */
    public static Configuration getConfiguration(String name, Configuration... configurations) {
        if(name!=null) {
            for(Configuration conf: configurations){
                if(name.equals(conf.getName())){
                    return conf;
                }
            }
            return Configuration.current(name);
        }
        else{
            for(Configuration conf: configurations){
                if("default".equals(conf.getName())){
                    return conf;
                }
            }
        }
        return Configuration.current();
    }

    public static boolean isResolutionModuleLoaded(){
        return resolutionModuleLoaded;
    }

    public static String evaluateValue(String value){
        if(!resolutionModuleLoaded){
            return value;
        }
        ExpressionEvaluator evaluator = ServiceContext.getInstance().getService(ExpressionEvaluator.class).orElse(null);
        if(evaluator!=null){
            return evaluator.filterProperty("<injection>", value, (k) -> Configuration.current().get(k)){
        }
        return value;
    }
}
