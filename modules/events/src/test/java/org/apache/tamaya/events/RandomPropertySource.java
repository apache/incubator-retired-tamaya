package org.apache.tamaya.events;

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.HashMap;
import java.util.Map;

/**
 * PropertySource that provides a randome entry, different on each access!
 */
public class RandomPropertySource implements PropertySource{

    private Map<String, String> data = new HashMap<>();

    @Override
    public int getOrdinal() {
        return 0;
    }

    @Override
    public String getName() {
        return "random";
    }

    @Override
    public PropertyValue get(String key) {
        if(key.equals("random.new")){
            return PropertyValue.of(key, String.valueOf(Math.random()),getName());
        }
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        synchronized(data) {
            data.put("random.new", String.valueOf(Math.random()));
            data.put("_random.new.source", getName());
            data.put("_random.new.timestamp", String.valueOf(System.currentTimeMillis()));
            return new HashMap<>(data);
        }
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
