package org.apache.tamaya.core.internal.properties;

import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.PropertyProvider;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Anatole on 07.12.2014.
 */
class BuildablePropertyProvider implements PropertyProvider {

    private MetaInfo metaInfo;
    private PropertyProvider baseProvider;

    public BuildablePropertyProvider(MetaInfo metaInfo, PropertyProvider baseProvider) {
        this.metaInfo = Objects.requireNonNull(metaInfo);
        this.baseProvider = Objects.requireNonNull(baseProvider);
    }

    @Override
    public Optional<String> get(String key) {
        return this.baseProvider.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return this.baseProvider.containsKey(key);
    }

    @Override
    public Map<String, String> toMap() {
        return this.baseProvider.toMap();
    }

    @Override
    public MetaInfo getMetaInfo() {
        return this.metaInfo;
    }

    @Override
    public String toString(){
        return "BuildablePropertyProvider -> " + getMetaInfo().toString();
    }

}
