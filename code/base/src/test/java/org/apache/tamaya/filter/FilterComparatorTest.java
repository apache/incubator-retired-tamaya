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
package org.apache.tamaya.filter;

import org.apache.tamaya.base.filter.FilterComparator;
import org.apache.tamaya.base.filter.Filter;
import org.junit.Test;

import javax.annotation.Priority;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterComparatorTest {

    @Test
    public void comparationOfFiltersWithSamePriorityIsCorrect() {
        Comparator<Filter> comparator = FilterComparator.getInstance();

        int result = comparator.compare(new FilterA(), new FilterA());

        assertThat(result).isEqualTo(0);
    }

    @Test
    public void comparationOfFiltersFirstHigherThenSecondWorksCorrectly() {
        Comparator<Filter> comparator = FilterComparator.getInstance();

        int result = comparator.compare(new FilterB(), new FilterA());

        assertThat(result).isGreaterThan(0);
    }

    @Test
    public void comparationOfFiltersSecondHigherThenFirstWorksCorrectly() {
        Comparator<Filter> comparator = FilterComparator.getInstance();

        int result = comparator.compare(new FilterA(), new FilterB());

        assertThat(result).isLessThan(0);
    }


    @Priority(1)
    private static class FilterA implements Filter {
        public String filterProperty(String key, String value) {
            throw new RuntimeException("Not implemented or look at me!");
        }
    }

    @Priority(2)
    private static class FilterB implements Filter {
        public String filterProperty(String key, String value) {
            throw new RuntimeException("Not implemented or look at me!");
        }
    }
}