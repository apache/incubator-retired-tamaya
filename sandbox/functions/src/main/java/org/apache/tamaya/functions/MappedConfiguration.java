package org.apache.tamaya.functions;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Configuration that filters part of the entries defined by a filter predicate.
 */
class MappedConfiguration implements Configuration {

    private final Configuration baseConfiguration;
    private final Function<String, String> keyMapper;
    private final String mapType;

    MappedConfiguration(Configuration baseConfiguration, Function<String, String> keyMapper, String mapType) {
        this.baseConfiguration = Objects.requireNonNull(baseConfiguration);
        this.keyMapper = Objects.requireNonNull(keyMapper);
        this.mapType = Optional.ofNullable(mapType).orElse(this.keyMapper.toString());
    }

    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        return baseConfiguration.get(this.keyMapper.apply(key), type);
    }

    @Override
    public Map<String, String> getProperties() {
        return baseConfiguration.getProperties().entrySet().stream().collect(Collectors.toMap(
                en -> keyMapper.apply(en.getKey()), en -> en.getValue()
        ));
    }

    @Override
    public String toString() {
        return "FilteredConfiguration{" +
                "baseConfiguration=" + baseConfiguration +
                ", mapping=" + mapType +
                '}';
    }

}
