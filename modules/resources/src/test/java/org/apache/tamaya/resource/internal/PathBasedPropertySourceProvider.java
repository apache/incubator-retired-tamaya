package org.apache.tamaya.resource.internal;

import org.apache.tamaya.resource.Resources;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Anatole on 03.03.2015.
 */
public class PathBasedPropertySourceProvider implements PropertySourceProvider{

    @Override
    public Collection<PropertySource> getPropertySources() {
        List<PropertySource> propertySources = new ArrayList<>();
        Collection<URL> resources = Resources.getResourceResolver().getResources("META-INF/cfg/**/*.properties");
        for(URL url:resources){
            Properties props = new Properties();
            try(InputStream is = url.openStream()){
                props.load(is);
                propertySources.add(new PropertiesBasedPropertySource(url.toString(), props));
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return propertySources;
    }


    private final static class PropertiesBasedPropertySource implements PropertySource{

        private String name;
        private Map<String,String> properties = new HashMap<>();

        public PropertiesBasedPropertySource(String name, Properties props) {
            this.name = name;
            props.forEach((k,v) -> this.properties.put(k.toString(), v.toString()));
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String get(String key) {
            return properties.get(key);
        }

        @Override
        public Map<String, String> getProperties() {
            return properties;
        }
    }
}
