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
package org.apache.tamaya.core.converters;

import org.apache.tamaya.base.convert.ConversionContext;
import org.osgi.service.component.annotations.Component;

import javax.config.spi.Converter;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Class, hereby using the following classloaders:
 * <ul>
 *     <li>The current ThreadContext ClassLoader</li>
 *     <li>The Classloader of this class</li>
 *     <li>The system Classloader</li>
 * </ul>
 */
@Component(service = Converter.class)
public class ClassConverter implements Converter<Class<?>> {

    private final Logger LOG = Logger.getLogger(getClass().getName());

    @Override
    public Class<?> convert(String value) {
        ConversionContext context = ConversionContext.getContext();
        if(value==null){
            return null;
        }
        context.addSupportedFormats(getClass(),"<fullyQualifiedClassName>");
        String trimmed = Objects.requireNonNull(value).trim();
        try{
            return Class.forName(trimmed, false, Thread.currentThread().getContextClassLoader());
        }
        catch(Exception e){
            LOG.finest("Class not found in context CL: " + trimmed);
        }
        try{
            return Class.forName(trimmed, false, ClassConverter.class.getClassLoader());
        }
        catch(Exception e){
            LOG.finest("Class not found in ClassConverter's CL: " + trimmed);
        }
        try{
            return Class.forName(trimmed, false, ClassLoader.getSystemClassLoader());
        }
        catch(Exception e){
            LOG.finest("Class not found in System CL (giving up): " + trimmed);
            return null;
        }
    }

    @Override
    public boolean equals(Object o){
        return getClass().equals(o.getClass());
    }

    @Override
    public int hashCode(){
        return getClass().hashCode();
    }
}
