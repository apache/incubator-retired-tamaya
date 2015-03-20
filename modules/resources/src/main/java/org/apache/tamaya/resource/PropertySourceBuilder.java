package org.apache.tamaya.resource;

import org.apache.tamaya.spi.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Simple builder for building a {@link org.apache.tamaya.spi.PropertySource}.
 */
public final class PropertySourceBuilder {
    private int ordinal;
    private String name;
    private Map<String,String> properties = new HashMap<>();

    /** private constructor. */
    private PropertySourceBuilder(String name){
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Gets a new instance of a builder.
     * @param name The name of the property source, not null.
     * @return a new instance.
     */
    public static PropertySourceBuilder of(String name){
        return new PropertySourceBuilder(name);
    }

    /**
     * Gets a new instance of a builder.
     * @param name The name of the property source, not null.
     * @return a new instance.
     */
    public static PropertySourceBuilder from(String name){
        return new PropertySourceBuilder(name);
    }

    public PropertySourceBuilder add(String key, String value){
        this.properties.put(key, value);
        return this;
    }

    public PropertySourceBuilder addAll(Map<String,String> values){
        this.properties.putAll(values);
        return this;
    }

    public PropertySourceBuilder withOrdinal(int ordinal){
        this.ordinal = ordinal;
        return this;
    }

    public PropertySourceBuilder addValues(PropertySource propertySource){
        this.properties.putAll(propertySource.getProperties());
        return this;
    }
}
