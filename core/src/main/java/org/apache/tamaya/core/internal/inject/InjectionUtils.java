package org.apache.tamaya.core.internal.inject;

import org.apache.tamaya.Codec;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.annotation.*;
import org.apache.tamaya.core.internal.Utils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Anatole on 19.12.2014.
 */
final class InjectionUtils {

    private InjectionUtils(){}

    /**
     * This method evaluates the {@link org.apache.tamaya.Configuration} that currently is valid for the given target field/method.
     *
     * @return the {@link org.apache.tamaya.Configuration} instance to be used, never null.
     */
    public static Configuration getConfiguration(ConfiguredProperty prop, Configuration... configuration) {
        String name = prop.config();
        if (name != null && !name.trim().isEmpty()) {
            return Configuration.current(name.trim());
        }
        return Configuration.current();
    }

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
        for(ConfiguredProperty prop: configuredProperties){
            List<String> keys = InjectionUtils.evaluateKeys((Member)element, areasAnnot, prop);
            Configuration config = InjectionUtils.getConfiguration(prop, configurations);
            for (String key : keys) {
                if (config.containsKey(key)) {
                    configValue = config.get(key).orElse(null);
                }
                if (configValue != null) {
                    break;
                }
            }
            if (configValue != null) {
                // net step perform expression resolution, if any
                return Configuration.evaluateValue(configValue, config);
            }
        }
        if (configValue == null && defaultAnnot != null) {
            return defaultAnnot.value();
        }
        return null;
    }

    public static <T> T adaptValue(AnnotatedElement element, Class<T> targetType, String configValue){
        try {
            // Check for adapter/filter
            T adaptedValue = null;
            WithCodec codecAnnot = element.getAnnotation(WithCodec.class);
            Class<? extends Codec> codecType;
            if (codecAnnot != null) {
                codecType = codecAnnot.value();
                if (!codecType.equals(Codec.class)) {
                    // TODO cache here...
                    Codec<String> codec = codecType.newInstance();
                    adaptedValue = (T) codec.deserialize(configValue);
                }
            }
            if (String.class.equals(targetType)) {
                 return (T)configValue;
            } else {
                 Codec<?> adapter = Codec.getInstance(targetType);
                 return (T)adapter.deserialize(configValue);
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
                if(name.equals(conf.getMetaInfo().getName())){
                    return conf;
                }
            }
            return Configuration.current(name);
        }
        else{
            for(Configuration conf: configurations){
                if("default".equals(conf.getMetaInfo().getName())){
                    return conf;
                }
            }
        }
        return Configuration.current();
    }
}
