package org.apache.tamaya.resolver;

import org.apache.tamaya.spi.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Anatole on 04.01.2015.
 */
public class MyTestPropertySource implements PropertySource{

    private Map<String,String> properties = new HashMap<>();

    public MyTestPropertySource(){
        properties.put("Expression Only", "${java.version}");
        properties.put("Expression Only (prefixed)", "${sys:java.version}");
        properties.put("Before Text", "My Java version is ${java.version}");
        properties.put("Before Text (prefixed)", "My Java version is ${sys:java.version}");
        properties.put("Before and After Text", "My Java version is ${java.version}.");
        properties.put("Before and After Text (prefixed)", "My Java version is ${sys:java.version}.");
        properties.put("Multi-expression", "Java version ${sys:java.version} and line.separator ${line.separator}.");
    }

    @Override
    public int getOrdinal() {
        return 0;
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(properties.get(key));
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }
}
