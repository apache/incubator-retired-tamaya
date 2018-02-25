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
import static org.junit.Assert.*;
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
        
        assertEquals(1, instance.compare(low, high));
        assertEquals(-1, instance.compare(high, low));
        assertEquals(0, instance.compare(low, low));
        
        assertEquals(1, instance.compare(nullPriority, high));
        assertEquals(-1, instance.compare(high, nullPriority));
        assertEquals(0, instance.compare(nullPriority, low));
    }

    /**
     * Test of getPriority method, of class OSGIServiceComparator.
     */
    @Test
    public void testGetPriority_Object() {
        ServiceReference low = new MockLowPriorityServiceReference();
        assertEquals(1, OSGIServiceComparator.getPriority(low));
        
        ServiceReference nullPriority = new MockServiceReference();
        assertEquals(1, OSGIServiceComparator.getPriority(nullPriority));
        
        ServiceReference high = new MockHighPriorityServiceReference();
        assertEquals(10, OSGIServiceComparator.getPriority(high));
    }

    /**
     * Test of getPriority method, of class OSGIServiceComparator.
     */
    @Test
    public void testGetPriority_Class() {
        assertEquals(10, OSGIServiceComparator.getPriority(MockHighPriorityServiceReference.class));
        assertEquals(1, OSGIServiceComparator.getPriority(MockLowPriorityServiceReference.class));
        assertEquals(1, OSGIServiceComparator.getPriority(MockServiceReference.class));
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
