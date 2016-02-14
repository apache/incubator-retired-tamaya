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
package org.apache.tamaya.collections.internal;

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  PropertyConverter for gnerating HashMap representation of a values.
 */
public class HashMapConverter implements PropertyConverter<HashMap> {
    private static final Logger LOG = Logger.getLogger(ArrayListConverter.class.getName());

    /** The shared instance, used by other collection converters in this package.*/
    private static HashMapConverter INSTANCE = new HashMapConverter();

    /**
     * Provide a shared instance, used by other collection converters in this package.
     * @return the shared instance, never null.
     */
    static HashMapConverter getInstance(){
        return INSTANCE;
    }

    @Override
    public HashMap convert(String value, ConversionContext context) {
        List<String> rawList = ArrayListConverter.split(value);
        String converterClass = context.getConfiguration().get('_' + context.getKey()+".collection-valueParser");
        if(converterClass!=null){
            try {
                PropertyConverter<?> valueConverter = (PropertyConverter<?>) Class.forName(converterClass).newInstance();
                HashMap<String,Object> mlist = new HashMap<>();
                ConversionContext ctx = new ConversionContext.Builder(context.getConfiguration(), context.getKey(),
                        TypeLiteral.of(context.getTargetType().getType())).build();
                for(String raw:rawList){
                    String[] items = splitItems(raw);
                    Object convValue = valueConverter.convert(items[1], ctx);
                    if(convValue!=null){
                        mlist.put(items[0], convValue);
                        continue;
                    }
                }
                return mlist;

            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error convertion config to HashMap type.", e);
            }
        }
        return null;
    }

    static String[] splitItems(String raw) {
        String[] items = new String[2];
        // check for '[]'
        // else split with ':'
        return raw.split("::");
    }
}
