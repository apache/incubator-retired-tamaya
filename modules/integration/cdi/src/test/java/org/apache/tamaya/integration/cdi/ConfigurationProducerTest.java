/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tamaya.integration.cdi;

import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Module;
import org.apache.tamaya.inject.api.Config;
import org.apache.tamaya.inject.api.ConfigDefault;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tomitribe.util.Duration;

import javax.inject.Inject;
import java.io.File;

import static org.junit.Assert.*;

@RunWith(ApplicationComposer.class)
public class ConfigurationProducerTest {

    @Module
    @Classes(cdi = true, value = {
        AllTypes.class,
        ConfigurationExtension.class,
        ConfigurationProducer.class
    })
    public EjbJar jar() {
        return new EjbJar("config");
    }

    @Inject
    private AllTypes allTypes;

    @Test
    public void defaultValues() {
        assertNotNull(allTypes);
        assertEquals("defaultString", allTypes.getDefaultString());
        assertEquals(new File("./"), allTypes.getDefaultFile());
        assertEquals(new Duration("2 hours and 54 minutes"), allTypes.getDefaultDuration());
        assertEquals(true, allTypes.getDefaultBoolean());
        assertEquals(45, (int) allTypes.getDefaultInteger());
    }

    @Test
    public void actualPropertyValues() {
        assertNotNull(allTypes);
        assertEquals("hello", allTypes.getString());
        assertEquals(new File("./conf"), allTypes.getFile());
        assertEquals(new Duration("10 minutes and 57 seconds"), allTypes.getDuration());
        assertEquals(true, allTypes.getaBoolean());
        assertEquals(123, (int) allTypes.getInteger());
    }

    static class AllTypes {

        @Inject
        @Config("string.value")
        @ConfigDefault("defaultString")
        private String string;

        @Inject
        @Config("defaultString.value")
        @ConfigDefault("defaultString")
        private String defaultString;

        @Inject
        @Config("file.value")
        @ConfigDefault("./")
        private File file;

        @Inject
        @Config("defaultFile.value")
        @ConfigDefault("./")
        private File defaultFile;

        @Inject
        @Config("duration.value")
        @ConfigDefault("2 hours and 54 minutes")
        private Duration duration;

        @Inject
        @Config("defaultDuration.value")
        @ConfigDefault("2 hours and 54 minutes")
        private Duration defaultDuration;

        @Inject
        @Config("boolean.value")
        @ConfigDefault("true")
        private Boolean aBoolean;

        @Inject
        @Config("defaultBoolean.value")
        @ConfigDefault("true")
        private Boolean defaultBoolean;

        @Inject
        @Config("integer.value")
        @ConfigDefault("45")
        private Integer integer;

        @Inject
        @Config("defaultInteger.value")
        @ConfigDefault("45")
        private Integer defaultInteger;

        public String getString() {
            return string;
        }

        public File getFile() {
            return file;
        }

        public Duration getDuration() {
            return duration;
        }

        public Boolean getaBoolean() {
            return aBoolean;
        }

        public Integer getInteger() {
            return integer;
        }

        public String getDefaultString() {
            return defaultString;
        }

        public File getDefaultFile() {
            return defaultFile;
        }

        public Duration getDefaultDuration() {
            return defaultDuration;
        }

        public Boolean getDefaultBoolean() {
            return defaultBoolean;
        }

        public Integer getDefaultInteger() {
            return defaultInteger;
        }

        @Override
        public String toString() {
            return "AllTypes{" +
                    "string='" + string + '\'' +
                    ", defaultString='" + defaultString + '\'' +
                    ", file=" + file +
                    ", defaultFile=" + defaultFile +
                    ", duration=" + duration +
                    ", defaultDuration=" + defaultDuration +
                    ", aBoolean=" + aBoolean +
                    ", defaultBoolean=" + defaultBoolean +
                    ", integer=" + integer +
                    ", defaultInteger=" + defaultInteger +
                    '}';
        }
    }

}
