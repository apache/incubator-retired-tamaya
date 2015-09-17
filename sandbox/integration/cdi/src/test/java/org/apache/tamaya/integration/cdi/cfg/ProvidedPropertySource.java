package org.apache.tamaya.integration.cdi.cfg;

import org.apache.tamaya.spi.PropertySource;

import javax.enterprise.inject.Vetoed;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anatole on 17.09.2015.
 */
@Vetoed
class ProvidedPropertySource implements PropertySource{

    final Map<String,String> config = new HashMap<>();

    public ProvidedPropertySource(){
        config.put("a.b.c.key3", "keys current a.b.c.key3");
        config.put("a.b.c.key4", "keys current a.b.c.key4");
        config.put("{meta}source.type:"+getClass().getName(), "PropertySourceProvider");
    }

    @Override
    public int getOrdinal() {
        return 10;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public String get(String key) {
        return config.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return config;
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
