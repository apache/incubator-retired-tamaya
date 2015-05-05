package org.apache.tamaya.functions;

import org.apache.tamaya.spi.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration that filters part of the entries defined by a filter predicate.
 */
class MetaEnrichedPropertySource implements PropertySource {

    private final PropertySource basePropertySource;
    private final Map<String, String> metaInfo;

    MetaEnrichedPropertySource(PropertySource basePropertySource, Map<String, String> metaInfo) {
        this.basePropertySource = Objects.requireNonNull(basePropertySource);
        this.metaInfo = Objects.requireNonNull(metaInfo);
    }

    // [meta:origin]a.b.c
    @Override
    public String get(String key) {
        if(key.startsWith("[meta:")){
            key = key.substring(6);
            int index = key.indexOf(']');
            String metaKey = key.substring(0,index);
            String entryKey = key.substring(index+1);
            String value =  basePropertySource.get(entryKey);
            if(value!=null) {
                return metaInfo.get(metaKey);
            }
        }
        return basePropertySource.get(key);
    }

    @Override
    public String getName() {
        return basePropertySource.getName();
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> baseProperties = basePropertySource.getProperties();
        Map<String, String> allProperties = new HashMap<>(baseProperties);
        baseProperties.forEach((k,v) -> {
            this.metaInfo.forEach(
                (mk, mv) -> allProperties.put("[meta:"+mk+']'+k, mv));
            });
        return allProperties;
    }

    @Override
    public String toString() {
        return "MetaEnrichedPropertySource{" +
                "basePropertySource=" + basePropertySource +
                ", metaInfo=" + metaInfo +
                '}';
    }

}
