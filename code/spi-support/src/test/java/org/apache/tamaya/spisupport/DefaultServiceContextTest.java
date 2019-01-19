/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.spisupport;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.ConfigurationProviderSpi;
import org.junit.Test;

import javax.annotation.Priority;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

public class DefaultServiceContextTest {

    /**
     * context to test
     */
    private final DefaultServiceContext context = new DefaultServiceContext();


    @Test
    public void testGetService() {
        ConfigurationProviderSpi providerSpi = context.getService(ConfigurationProviderSpi.class);
        assertThat(providerSpi).isNotNull();
        assertThat(providerSpi instanceof TestConfigurationProvider).isTrue();
    }

    @Test(expected = ConfigException.class)
    public void testGetService_multipleServicesWithoutPriority_shouldThrowConfigException() {
        context.getService(InvalidPriorityInterface.class);
    }

    @Test
    public void testGetService_multipleService_shouldReturnServiceWithHighestPriority() {
        MultiImplsInterface service = context.getService(MultiImplsInterface.class);

        assertThat(service).isNotNull();
        assertThat(service instanceof MultiImpl2).isTrue();
    }

    @Test
    public void testGetService_noImpl_shouldReturnEmptyOpional() {
        NoImplInterface service = context.getService(NoImplInterface.class);
        assertThat(service).isNull();
    }


    @Test
    public void testGetServices_shouldReturnServices() {
        {
            Collection<InvalidPriorityInterface> services = context.getServices(InvalidPriorityInterface.class);
            assertThat(services).isNotNull();
            assertThat(services).hasSize(2);

            for (InvalidPriorityInterface service : services) {
                assertThat(service instanceof InvalidPriorityImpl1 || service instanceof InvalidPriorityImpl2).isTrue();
            }
        }

        {
            List<MultiImplsInterface> services = context.getServices(MultiImplsInterface.class);
            assertThat(services).isNotNull();
            assertThat(services).hasSize(3);

            assertThat(services.get(0) instanceof MultiImpl2).isTrue();
            assertThat(services.get(1) instanceof MultiImpl1).isTrue();
            assertThat(services.get(2) instanceof MultiImpl3).isTrue();
        }
    }

    @Test
    public void testGetServices_redundantAccessToServices() {
        for(int i=0;i<10;i++){
            Collection<InvalidPriorityInterface> services = context.getServices(InvalidPriorityInterface.class);
            assertThat(services).isNotNull();
            assertThat(services).hasSize(2);
            for (InvalidPriorityInterface service : services) {
                assertThat(service instanceof InvalidPriorityImpl1 || service instanceof InvalidPriorityImpl2).isTrue();
            }
        }
    }

    @Test
    public void testGetServices_noImpl_shouldReturnEmptyList() {
        Collection<NoImplInterface> services = context.getServices(NoImplInterface.class);
        assertThat(services).isNotNull();
        assertThat(services).isEmpty();
    }

    @Test
    public void testRegister_One() throws Exception {
        context.register(Long.class, Long.valueOf(11L), true);
        Long service = context.getService(Long.class);
        assertThat(service).isNotNull();
    }

    @Test
    public void testRegister_Many() throws Exception {
        context.register(Double.class, Arrays.asList(Double.valueOf(1.2345), Double.valueOf(2345), Double.valueOf(345)), false);
        List<Double> services = context.getServices(Double.class);
        assertThat(services).isNotNull().isNotEmpty().hasSize(3);
    }


    // some test interfaces and classes

    public interface InvalidPriorityInterface {
    }

    @Priority(value = 50)
    public static class InvalidPriorityImpl1 implements InvalidPriorityInterface {
    }

    @Priority(value = 50)
    public static class InvalidPriorityImpl2 implements InvalidPriorityInterface {
    }


    public interface MultiImplsInterface {
    }

    public static class MultiImpl1 implements MultiImplsInterface {
    }

    @Priority(value = 500)
    public static class MultiImpl2 implements MultiImplsInterface {
    }

    @Priority(value = -10)
    public static class MultiImpl3 implements MultiImplsInterface {
    }

    private interface NoImplInterface {
    }
}
