package org.apache.tamaya.core.config;

import org.apache.tamaya.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Created by Anatole on 07.12.2014.
 */
class MappedConfiguration extends AbstractConfiguration implements Configuration {

    private UnaryOperator<String> keyMapper;
    private Configuration config;

    public MappedConfiguration(Configuration config, UnaryOperator<String> keyMapper) {
        super(MetaInfoBuilder.of(config.getMetaInfo()).setInfo("Mapped configuration, mapper=" + keyMapper).build());
        this.config = Objects.requireNonNull(config);
        this.keyMapper = Objects.requireNonNull(keyMapper);
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> result = new HashMap<>();
        Map<String, String> map = this.config.toMap();
        map.forEach((k,v) -> {
            String targetKey = keyMapper.apply(k);
            if(targetKey!=null){
                result.put(targetKey, v);
            }
        });
        return result;
    }

    @Override
    public boolean isMutable() {
        return this.config.isMutable();
    }

    @Override
    public void apply(ConfigChangeSet change) {
        this.config.apply(change);
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