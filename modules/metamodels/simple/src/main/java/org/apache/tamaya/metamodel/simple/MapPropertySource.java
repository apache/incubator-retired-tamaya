package org.apache.tamaya.metamodel.simple;

import org.apache.tamaya.spi.PropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Anatole on 17.01.2015.
 */
public class MapPropertySource implements PropertySource {
    private int ordinal;
    private String name;
    private Map<String, String> properties = new HashMap<>();

    public MapPropertySource(int ordinal, String name, Map<String, String> properties) {
        this.ordinal = ordinal;
        this.name = Objects.requireNonNull(name);
        this.properties.putAll(properties);
        this.properties = Collections.unmodifiableMap(this.properties);
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        return properties.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }
}
