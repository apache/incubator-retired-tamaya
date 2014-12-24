/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.tamaya.spi.ConfigurationSpi;

/**
 * Created by Anatole on 09.09.2014.
 */
public class TestConfigServiceSingletonSpi implements ConfigurationSpi {


    private Map<String, Configuration> configs = new ConcurrentHashMap<>();

    public TestConfigServiceSingletonSpi(){
        Map<String,String> config = new HashMap<>();
        config.put("a.b.c.key1", "keys current a.b.c.key1");
        config.put("a.b.c.key2", "keys current a.b.c.key2");
        config.put("a.b.key3", "keys current a.b.key3");
        config.put("a.b.key4", "keys current a.b.key4");
        config.put("a.key5", "keys current a.key5");
        config.put("a.key6", "keys current a.key6");
        config.put("int1", "123456");
        config.put("int2", "111222");
        config.put("booleanT", "true");
        config.put("double1", "1234.5678");
        config.put("BD", "123456789123456789123456789123456789.123456789123456789123456789123456789");
        config.put("testProperty", "keys current testProperty");
        config.put("runtimeVersion", "${java.version}");
        // configs.put("test", new MapConfiguration(MetaInfoBuilder.current().setName("test").build(), config));
    }



    @Override
    public boolean isConfigurationAvailable(String name){
        return configs.containsKey(name);
    }

    @Override
    public Configuration getConfiguration(String name) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public <T> T createTemplate(Class<T> type, Configuration... configurations) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void configure(Object instance, Configuration... configurations) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String evaluateValue(String expression, Configuration... configurations) {
        // TODO improve this ugly implementation...
        for (Configuration config : configurations) {
            for (Map.Entry<String, String> en : config.getProperties().entrySet()) {
                expression = expression.replaceAll("\\$\\{" + en.getKey() + "\\}", en.getValue());
            }
        }
        return expression;
    }

    @Override
    public void addChangeListener(Consumer<ConfigChangeSet> l) {
        // ignore
    }

    @Override
    public void removeChangeListener(Consumer<ConfigChangeSet> l) {
        // ignore
    }

    @Override
    public void publishChange(ConfigChangeSet configChangeSet) {
        // ignore
    }

}
