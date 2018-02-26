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
package org.apache.tamaya.core.internal;

import javax.annotation.Priority;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author William.Lieurance 2018-02-05
 */
public class OSGIServiceComparatorTest {

    /**
     * Test of compare method, of class OSGIServiceComparator.
     */
    @Test
    public void testCompare() {
        ServiceReference low = new MockLowPriorityServiceReference();
        ServiceReference nullPriority = new MockServiceReference();
        ServiceReference high = new MockHighPriorityServiceReference();
        OSGIServiceComparator instance = new OSGIServiceComparator();
        assertThat(1).isEqualTo(instance.compare(low, high));
        assertThat(-1).isEqualTo(instance.compare(high, low));
        assertThat(0).isEqualTo(instance.compare(low, low));

        assertThat(1).isEqualTo(instance.compare(nullPriority, high));
        assertThat(-1).isEqualTo(instance.compare(high, nullPriority));
        assertThat(0).isEqualTo(instance.compare(nullPriority, low));
    }

    /**
     * Test of getPriority method, of class OSGIServiceComparator.
     */
    @Test
    public void testGetPriority_Object() {
        ServiceReference low = new MockLowPriorityServiceReference();
        assertThat(OSGIServiceComparator.getPriority(low)).isEqualTo(1);
        
        ServiceReference nullPriority = new MockServiceReference();
        assertThat(OSGIServiceComparator.getPriority(nullPriority)).isEqualTo(1);
        
        ServiceReference high = new MockHighPriorityServiceReference();
        assertThat(OSGIServiceComparator.getPriority(high)).isEqualTo(10);
    }

    /**
     * Test of getPriority method, of class OSGIServiceComparator.
     */
    @Test
    public void testGetPriority_Class() {
        assertThat(OSGIServiceComparator.getPriority(MockHighPriorityServiceReference.class)).isEqualTo(10);
        assertThat(OSGIServiceComparator.getPriority(MockLowPriorityServiceReference.class)).isEqualTo(1);
        assertThat(OSGIServiceComparator.getPriority(MockServiceReference.class)).isEqualTo(1);
    }
    
    private class MockServiceReference implements ServiceReference {
        @Override
        public Object getProperty(String string) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String[] getPropertyKeys() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Bundle getBundle() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Bundle[] getUsingBundles() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isAssignableTo(Bundle bundle, String string) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int compareTo(Object o) {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
    
    @Priority(1)
    private class MockLowPriorityServiceReference extends MockServiceReference {};
    @Priority(10)
    private class MockHighPriorityServiceReference extends MockServiceReference {};
    
}
