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
package org.apache.tamaya.core.spi;

import java.util.*;

/**
 * Simple comparator based on a Collection of {@link OrdinalProvider} instances.
 */
final class DefaultServiceComparator implements Comparator<Object>{

    /**
     * List of ordinal providers loaded.
     */
    private List<OrdinalProvider> ordinalProviders = new ArrayList<>();

    DefaultServiceComparator(Collection<? extends OrdinalProvider> providers){
        ordinalProviders.addAll(Objects.requireNonNull(providers));
        ordinalProviders.sort(this::compare);
    }

    private int compare(OrdinalProvider provider1, OrdinalProvider provider2){
        int o1 = getOrdinal(provider1);
        int o2 = getOrdinal(provider2);
        int order = o1-o2;
        if(order < 0){
            return -1;
        }
        else if(order > 0){
            return 1;
        }
        return 0;
    }

    private int getOrdinal(OrdinalProvider provider){
        if(provider instanceof Orderable){
            return ((Orderable)provider).order();
        }
        return 0;
    }

    public int getOrdinal(Object service){
        for(OrdinalProvider provider: ordinalProviders){
            OptionalInt ord = provider.getOrdinal(service.getClass());
            if(ord.isPresent()){
                return ord.getAsInt();
            }
        }
        if(service instanceof Orderable){
            return ((Orderable)service).order();
        }
        return 0;
    }


    @Override
    public int compare(Object o1, Object o2) {
        int ord1 = getOrdinal(o1);
        int ord2 = getOrdinal(o2);
        int order = ord1-ord2;
        if(order < 0){
            return -1;
        }
        else if(order > 0){
            return 1;
        }
        return 0;
    }
}
