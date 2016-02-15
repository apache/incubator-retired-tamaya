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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  PropertyConverter for gnerating ArrayList representation of a values.
 */
public class ArrayListConverter implements PropertyConverter<ArrayList> {

    private static final Logger LOG = Logger.getLogger(ArrayListConverter.class.getName());

    /** The shared instance, used by other collection converters in this package.*/
    private static ArrayListConverter INSTANCE = new ArrayListConverter();

    /**
     * Provide a shared instance, used by other collection converters in this package.
     * @return the shared instance, never null.
     */
    static ArrayListConverter getInstance(){
        return INSTANCE;
    }

    @Override
    public ArrayList convert(String value, ConversionContext context) {
        ArrayList<String> rawList = split(value);
        String converterClass = context.getConfiguration().get('_' + context.getKey()+".collection-parser");
        if(converterClass!=null){
            try {
                PropertyConverter<?> valueConverter = (PropertyConverter<?>) Class.forName(converterClass).newInstance();
                ArrayList<Object> mlist = new ArrayList<>();
                ConversionContext ctx = new ConversionContext.Builder(context.getConfiguration(), context.getKey(),
                        TypeLiteral.of(context.getTargetType().getType())).build();
                for(String raw:rawList){
                    Object convValue = valueConverter.convert(raw, ctx);
                    if(convValue!=null){
                        mlist.add(convValue);
                        continue;
                    }
                }
                return mlist;

            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error convertion config to ArrayList type.", e);
            }
        }
        return rawList;
    }

    static ArrayList<String>  split(String value) {
        ArrayList<String> result = new ArrayList<>();
        int start = 0;
        int end = value.indexOf(',',start);
        while(end>0) {
            if (end>0 && (value.charAt(end - 1) != '\\')) {
                result.add(value.substring(start, end));
                start = end + 1;
                end = value.indexOf(',',start);
            }else{
                end = value.indexOf(',',end + 1);
            }
            end = value.indexOf(',',start);
        }
        if(start < value.length()){
            result.add(value.substring(start));
        }
        return result;
    }
}
