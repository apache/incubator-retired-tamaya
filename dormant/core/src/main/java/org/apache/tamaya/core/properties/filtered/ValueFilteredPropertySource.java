package org.apache.tamaya.core.properties.filtered;

import org.apache.tamaya.spi.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Property source which filters any key/values dynamically.
 */
class ValueFilteredPropertySource implements PropertySource{

    private String name;
    private BiFunction<String, String, String> valueFilter;
    private PropertySource source;

    public ValueFilteredPropertySource(String name, BiFunction<String, String, String> valueFilter, PropertySource current) {
        this.name = Optional.ofNullable(name).orElse("<valueFiltered> -> name="+current.getName()+", valueFilter="+valueFilter.toString());
    }

    @Override
    public int getOrdinal() {
        return source.getOrdinal();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<String> get(String key) {
        String value = this.source.get(key).orElse(null);
        value = valueFilter.apply(key, value);
        return Optional.ofNullable(value);
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<>(source.getProperties());
        map.replaceAll(valueFilter);
        return map;
    }

    @Override
    public String toString() {
        return "ValueFilteredPropertySource{" +
                "source=" + source.getName() +
                ", name='" + name + '\'' +
                ", valueFilter=" + valueFilter +
                '}';
    }
}
