//package org.apache.tamaya.ext.cdi;
//
//import org.apache.tamaya.ConfigOperator;
//import org.apache.tamaya.Configuration;
//import org.apache.tamaya.PropertyAdapter;
//import org.apache.tamaya.PropertyAdapters;
//import org.apache.tamaya.annot.*;
//
//import javax.enterprise.inject.InjectionException;
//import javax.enterprise.inject.spi.AnnotatedField;
//import javax.enterprise.inject.spi.ProcessInjectionTarget;
//import java.lang.ref.WeakReference;
//import java.lang.reflect.AnnotatedType;
//import java.lang.reflect.Field;
//import java.util.*;
//
///**
// * Created by Anatole on 01.10.2014.
// */
//class ConfiguredFIeld {
//
//    private AnnotatedType annotatedType;
//    private AnnotatedField annotatedField;
//    private List<String> keys;
//    private WeakReference<Object> instance;
//
//    public ConfiguredFIeld(Object instance, AnnotatedType type, AnnotatedField field) {
//        this.annotatedType = type;
//        this.annotatedField = field;
//        this.instance = new WeakReference<Object>(instance);
//    }
//
//    public void applyValue(ProcessInjectionTarget<?> processInjectionTarget) throws IllegalAccessException {
//        ConfiguredProperties repeatableTypeAnnot = this.annotatedField.getAnnotation(ConfiguredProperties.class);
//        List<String> keys = new ArrayList<>();
//        if(repeatableTypeAnnot!=null) {
//            for(ConfiguredProperty configProp:repeatableTypeAnnot.value()) {
//                keys.add(configProp.value());
//            }
//        }
//        else {
//            ConfiguredProperty prop = annotatedField.getAnnotation(ConfiguredProperty.class);
//            if (prop != null) {
//                keys.add(prop.value());
//            }
//        }
//        WithConfigOperator operatorAnnot = annotatedType.getAnnotation(WithConfigOperator.class);
//        Class<? extends ConfigOperator> configOperatorType = null;
//        if(operatorAnnot!=null) {
//            configOperatorType = operatorAnnot.value();
//        }
//        DefaultAreas areasAnnotation = annotatedType.getAnnotation(DefaultAreas.class);
//        String[] areas = null;
//        if(areasAnnotation!=null) {
//            areas = areasAnnotation.value();
//        }
//        Field javaField = annotatedField.getJavaMember();
//        javaField.setAccessible(true);
//        Class baseType = javaField.getType();
//        if (keys.isEmpty()) {
//            keys.add(javaField.getName());
//        }
//        WithConfig configAnnot = annotatedField.getAnnotation(WithConfig.class);
//        if(configAnnot==null){
//            configAnnot = annotatedType.getAnnotation(WithConfig.class);
//        }
//        Configuration config = null;
//        if(configAnnot!=null){
//            config = Configuration.of(configAnnot.value());
//            processInjectionTarget.addDefinitionError(new InjectionException(
//                    "No such config (" + configAnnot.value() + ") for type " + baseType.getName() + " of Field " + javaField.getName() +
//                            "!"));
//            return;
//        }
//        else{
//            config = Configuration.of();
//        }
//        String configValue = null;
//        for (String key : keys) {
//            configValue = config.get(key).orElse(null);
//            if (configValue != null) {
//                break;
//            }
//        }
//        if (configValue == null) {
//            DefaultValue defaultValueAnnot = annotatedField.getAnnotation(DefaultValue.class);
//            if(defaultValueAnnot!=null) {
//                configValue = defaultValueAnnot.value();
//            }
//        }
//        if (configValue == null) {
//            processInjectionTarget.addDefinitionError(new InjectionException(
//                    "No such config (" + Arrays.asList(keys) + ") for type " + baseType.getName() + " of Field " + javaField.getName() +
//                            "!"));
//            return;
//        }
//        // net step perform exression resolution, if any
//        configValue = Configuration.evaluateValue(configValue);
//        WithPropertyAdapter adapterAnnot = annotatedField.getAnnotation(WithPropertyAdapter.class);
//        PropertyAdapter<?> adapter = null;
//        if(adapterAnnot!=null) {
//            // TODO cache here...
//            try {
//                adapter = adapterAnnot.value().newInstance();
//            } catch (InstantiationException e) {
//                processInjectionTarget.addDefinitionError(new InjectionException(
//                        "Failed to instantiate adapter " + adapterAnnot.value().getName() + " of Field " + javaField.getName() +
//                                "!", e));
//                return;
//            }
//        }
//        if (String.class.equals(baseType)) {
//            if(adapter!=null){
//                javaField.set(instance, adapter.adapt(configValue));
//            }
//            else{
//                javaField.set(instance, configValue);
//            }
//        } else {
//            adapter = PropertyAdapters.getAdapter(baseType);
//            if (adapter == null) {
//                processInjectionTarget.addDefinitionError(new InjectionException(
//                        "Unsupported configured type " + baseType.getName() + " of Field " + javaField.getName() +
//                                "!"));
//                return;
//            }
//            javaField.set(instance, adapter.adapt(configValue));
//        }
//    }
//
//}
