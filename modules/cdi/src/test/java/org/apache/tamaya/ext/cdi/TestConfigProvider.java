package org.apache.tamaya.ext.cdi;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.core.config.Configurations;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anatole on 29.09.2014.
 */
public class TestConfigProvider implements ConfigurationProviderSpi{

    private Configuration testConfig;

    public TestConfigProvider(){
        final Map<String,String> config = new HashMap<>();
        config.put("a.b.c.key1", "value current a.b.c.key1");
        config.put("a.b.c.key2", "value current a.b.c.key2");
        config.put("a.b.key3", "value current a.b.key3");
        config.put("a.b.key4", "value current a.b.key4");
        config.put("a.key5", "value current a.key5");
        config.put("a.key6", "value current a.key6");
        config.put("int1", "123456");
        config.put("int2", "111222");
        config.put("testProperty", "testPropertyValue!");
        config.put("booleanT", "true");
        config.put("double1", "1234.5678");
        config.put("BD", "123456789123456789123456789123456789.123456789123456789123456789123456789");
        config.put("testProperty", "value current testProperty");
        config.put("runtimeVersion", "${java.version}");
        testConfig = Configurations.getConfiguration(MetaInfoBuilder.of().setName("test").build(),
                () -> config);
    }

    @Override
    public String getConfigName(){
        return "test";
    }

    @Override
    public Configuration getConfiguration(){
        return testConfig;
    }
}
