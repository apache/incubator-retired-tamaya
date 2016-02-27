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

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class that implements the tokenizing of the entries of a configuration value.
 */
final class ItemTokenizer {

    private static final Logger LOG = Logger.getLogger(ItemTokenizer.class.getName());

    /**
     * Private singleton.
     */
    private ItemTokenizer(){}

    /**
     * Splits the given value using the given separator. Matcjhing is done by traversing the String value using
     * {@code indexOf} calls, one by one. The last unresolvable item (without any next separator token)
     * is added at the end of the list.
     * @param value the value, not null.
     * @param context the conversion context.
     * @return the tokenized value as list, in order of occurrence.
     */
    public static List<String> split(String value, ConversionContext context){
        return split(value, ConfigurationProvider.getConfiguration().getOrDefault(
                '_' + context.getKey()+ "" +
                        "item-separator", ","));
    }

    /**
     * Splits the given value using the given separator. Matcjhing is done by traversing the String value using
     * {@code indexOf} calls, one by one. The last unresolvable item (without any next separator token)
     * is added at the end of the list.
     * @param value the value, not null.
     * @param separator the separator to be used.
     * @return the tokenized value as list, in order of occurrence.
     */
    public static List<String> split(String value, final String separator) {
        ArrayList<String> result = new ArrayList<>();
        int start = 0;
        int end = value.indexOf(separator,start);
        while(end>0) {
            if (value.charAt(end - 1) != '\\') {
                result.add(value.substring(start, end));
                start = end + separator.length();
                end = value.indexOf(separator,start);
            }else{
                end = value.indexOf(separator,end + separator.length());
            }
            end = value.indexOf(separator,start);
        }
        if(start < value.length()){
            result.add(value.substring(start));
        }
        return result;
    }

    /**
     * plits the given String value as a map entry, splitting it into key and value part with the given separator.
     * If the value cannot be split then {@code key = value = mapEntry} is used for further processing. key or value
     * parts are normally trimmed, unless they are enmcosed with brackets {@code []}.
     * @param mapEntry the entry, not null.
     * @param context the conversion context.
     * @return an array of length 2, with the trimmed and parsed key/value pair.
     */
    public static String[] splitMapEntry(String mapEntry, ConversionContext context){
        return splitMapEntry(mapEntry, ConfigurationProvider.getConfiguration().getOrDefault(
                '_' + context.getKey()+".map-entry-separator", "::"));
    }

    /**
     * Splits the given String value as a map entry, splitting it into key and value part with the given separator.
     * If the value cannot be split then {@code key = value = mapEntry} is used for further processing. key or value
     * parts are normally trimmed, unless they are enmcosed with brackets {@code []}.
     * @param mapEntry the entry, not null.
     * @param separator the separator, not null.
     * @return an array of length 2, with the trimmed and parsed key/value pair.
     */
    public static String[] splitMapEntry(final String mapEntry, final String separator) {
        int index = mapEntry.indexOf(separator);
        String[] items;
        if(index<0) {
            items = new String[]{mapEntry, mapEntry};
        }else {
            items = new String[]{mapEntry.substring(0,index),
                                 mapEntry.substring(index+separator.length())};
        }
        if(items[0].trim().startsWith("[")){
            items[0]= items[0].trim();
            items[0] = items[0].substring(1);
        }else{
            items[0]= items[0].trim();
        }
        if(items[1].trim().endsWith("]")){
            items[1] = items[1].substring(0,items[1].length()-1);
        }else{
            items[1]= items[1].trim();
        }
        return items;
    }

    /**
     * Parses the given value into the required collection target type, defined by the context.
     * @param value the raw String value.
     * @param context the context
     * @return the parsed value, or null.
     */
    public static Object convertValue(String value, ConversionContext context) {
        String converterClass = context.getConfiguration().get('_' + context.getKey() + ".item-converter");
        List<PropertyConverter<Object>> valueConverters = new ArrayList<>(1);
        if (converterClass != null) {
            try {
                valueConverters.add((PropertyConverter<Object>) Class.forName(converterClass).newInstance());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error convertion config to ArrayList type.", e);
            }
        }
        if (TypeLiteral.getTypeParameters(context.getTargetType().getType()).length>0) {
            valueConverters.addAll(context.getConfigurationContext().getPropertyConverters(
                    TypeLiteral.of(TypeLiteral.getTypeParameters(context.getTargetType().getType())[0])));
        }
        ConversionContext ctx = new ConversionContext.Builder(context.getConfiguration(),
                context.getConfigurationContext(), context.getKey(),
                TypeLiteral.of(context.getTargetType().getType())).build();
        Object result = null;
        if (valueConverters.isEmpty()) {
            return value;
        } else {
            for (PropertyConverter<Object> conv : valueConverters) {
                try {
                    result = conv.convert(value, ctx);
                    if (result != null) {
                        return result;
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error convertion config to ArrayList type.", e);
                }
            }
        }
        LOG.log(Level.SEVERE, "Failed to convert collection value type for '" + value + "'.");
        return null;
    }

}
