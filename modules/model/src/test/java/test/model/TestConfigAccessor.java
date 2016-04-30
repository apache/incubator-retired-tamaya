package test.model;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.util.Map;

/**
 * Created by atsticks on 30.04.16.
 */
public final class TestConfigAccessor {

    private TestConfigAccessor(){}

    public static Map<String,String> readAllProperties(){
        return ConfigurationProvider.getConfiguration()
                .getProperties();
    }

    public static Configuration readConfiguration(){
        return ConfigurationProvider.getConfiguration();
    }

    public static String readProperty(Configuration config, String key){
        return config.get(key);
    }
}
