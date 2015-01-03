package org.apache.tamaya.core.properties.filtered;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.apache.tamaya.PropertySource;
import org.apache.tamaya.spi.PropertySource;

/**
 * PropertySource implementation that maps certain parts (defined by an {@code UnaryOperator<String>}) to alternate areas.
 */
class MappedPropertySource implements PropertySource {

	private static final long serialVersionUID = 8690637705511432083L;

	/** The mapping operator. */
    private UnaryOperator<String> keyMapper;
    /** The base configuration. */
    private PropertySource propertySource;

    /**
     * Creates a new instance.
     * @param config the base configuration, not null
     * @param keyMapper The mapping operator, not null
     */
    public MappedPropertySource(PropertySource config, UnaryOperator<String> keyMapper) {
        this.propertySource = Objects.requireNonNull(config);
        this.keyMapper = Objects.requireNonNull(keyMapper);
    }

    @Override
    public int getOrdinal(){
        return this.propertySource.getOrdinal();
    }

    @Override
    public String getName(){
        return this.propertySource.getName()+"[mapped]";
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();
        Map<String, String> map = this.propertySource.getProperties();
        map.forEach((k,v) -> {
            String targetKey = keyMapper.apply(k);
            if(targetKey!=null){
                result.put(targetKey, v);
            }
        });
        return result;
    }

    @Override
    public Optional<String> get(String key){
        return Optional.of(getProperties().get(key));
    }

    @Override
    public boolean isEmpty() {
        return this.propertySource.isEmpty();
    }

}