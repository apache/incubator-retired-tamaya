package org.apache.tamaya.functions;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Configuration that filters part of the entries defined by a filter predicate.
 */
class FilteredConfiguration implements Configuration {

    private final Configuration baseConfiguration;
    private final BiPredicate<String, String> filter;
    private String filterType;

    FilteredConfiguration(Configuration baseConfiguration, BiPredicate<String, String> filter, String filterType) {
        this.baseConfiguration = Objects.requireNonNull(baseConfiguration);
        this.filter = Objects.requireNonNull(filter);
        this.filterType = Optional.ofNullable(filterType).orElse(this.filter.toString());
    }

    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        String value = baseConfiguration.get(key);
        if (filter.test(key, value)) {
            return baseConfiguration.get(key, type);
        }
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        return baseConfiguration.getProperties().entrySet().stream().filter(
                en -> filter.test(en.getKey(), en.getValue())).collect(Collectors.toMap(
                en -> en.getKey(), en -> en.getValue()
        ));
    }

    @Override
    public String toString() {
        return "FilteredConfiguration{" +
                "baseConfiguration=" + baseConfiguration +
                ", filter=" + filter +
                '}';
    }

}
