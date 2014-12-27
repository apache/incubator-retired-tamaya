package org.apache.tamaya.core.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.PropertySource;
import org.apache.tamaya.core.properties.AbstractPropertySource;

/**
 * PropertySource implementation that maps certain parts (defined by an {@code UnaryOperator<String>}) to alternate areas.
 */
class MappedPropertySource extends AbstractPropertySource {

	private static final long serialVersionUID = 8690637705511432083L;

	/** The mapping operator. */
    private UnaryOperator<String> keyMapper;
    /** The base configuration. */
    private PropertySource config;

    /**
     * Creates a new instance.
     * @param config the base configuration, not null
     * @param keyMapper The mapping operator, not null
     */
    public MappedPropertySource(PropertySource config, UnaryOperator<String> keyMapper) {
        super(config.getName());
        this.config = Objects.requireNonNull(config);
        this.keyMapper = Objects.requireNonNull(keyMapper);
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();
        Map<String, String> map = this.config.getProperties();
        map.forEach((k,v) -> {
            String targetKey = keyMapper.apply(k);
            if(targetKey!=null){
                result.put(targetKey, v);
            }
        });
        return result;
    }

    @Override
    public boolean isEmpty() {
        return this.config.isEmpty();
    }

}