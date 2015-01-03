package old;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.config.AbstractConfiguration;

/**
 * Configuration implementation that maps certain parts (defined by an {@code UnaryOperator<String>}) to alternate areas.
 */
class MappedConfiguration extends AbstractConfiguration implements Configuration {

	private static final long serialVersionUID = 8690637705511432083L;

	/** The mapping operator. */
    private UnaryOperator<String> keyMapper;
    /** The base configuration. */
    private Configuration config;

    /**
     * Creates a new instance.
     * @param config the base configuration, not null
     * @param keyMapper The mapping operator, not null
     */
    public MappedConfiguration(Configuration config, UnaryOperator<String> keyMapper) {
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

    @Override
    public Configuration toConfiguration() {
        return this;
    }

}