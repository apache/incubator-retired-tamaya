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
package org.apache.tamaya.core.internal.properties;

import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.core.env.ConfiguredSystemProperties;
import org.apache.tamaya.core.properties.AbstractPropertyProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class SystemPropertiesPropertyProvider extends AbstractPropertyProvider {


    private static final long serialVersionUID = -5935940312707001199L;

    public SystemPropertiesPropertyProvider(){
        super(MetaInfoBuilder.of().setType("sys-properties").build());
    }

    @Override
    public Map<String,String> toMap(){
        Properties sysProps = System.getProperties();
        if(sysProps instanceof ConfiguredSystemProperties){
            sysProps = ((ConfiguredSystemProperties)sysProps).getInitialProperties();
        }
        Map<String,String> props = new HashMap<>();
        for (Map.Entry<Object,Object> en : sysProps.entrySet()) {
            props.put(en.getKey().toString(), en.getValue().toString());
        }
        return Collections.unmodifiableMap(props);
    }


}
