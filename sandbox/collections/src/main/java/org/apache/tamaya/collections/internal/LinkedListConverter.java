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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  PropertyConverter for gnerating LinkedList representation of a values.
 */
public class LinkedListConverter implements PropertyConverter<LinkedList> {
    private static final Logger LOG = Logger.getLogger(LinkedListConverter.class.getName());

    @Override
    public LinkedList convert(String value, ConversionContext context) {
        List<String> rawList = ArrayListConverter.split(value);
        String converterClass = context.getConfiguration().get('_' + context.getKey()+".collection-valueParser");
        if(converterClass!=null){
            try {
                PropertyConverter<?> valueConverter = (PropertyConverter<?>) Class.forName(converterClass).newInstance();
                LinkedList<Object> mlist = new LinkedList<>();
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
        return new LinkedList(rawList);
    }

}
