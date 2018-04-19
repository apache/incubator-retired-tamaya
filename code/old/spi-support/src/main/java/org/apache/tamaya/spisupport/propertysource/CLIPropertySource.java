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
package org.apache.tamaya.spisupport.propertysource;

import org.apache.tamaya.spi.PropertyValue;

import java.util.*;

/**
 * PropertySource that allows adding the program's main arguments as configuration entries. Unix syntax using '--' and
 * '-' params is supported.
 */
public class CLIPropertySource extends BasePropertySource {

    /** The original main arguments. */
    private static String[] args = new String[0];

    /** The map of parsed main arguments. */
    private static Map<String,PropertyValue> mainArgs;

    /** Initializes the initial state. */
    static{
        initMainArgs(args);
    }

    /**
     * Creates a new instance.
     */
    public CLIPropertySource(){
        this((String[])null);
    }

    /**
     * Creates a new instance, allows optionally to pass the main arguments.
     * @param args the args, or null.
     */
    public CLIPropertySource(String... args){
        super("CLI");
        if(args!=null){
            initMainArgs(args);
        }
    }

    /**
     * Creates a new instance, allows optionally to pass the main arguments.
     * @param args the args, or null.
     * @param ordinal the ordinal to be applied.
     */
    public CLIPropertySource(int ordinal, String... args){
        if(args!=null){
            initMainArgs(args);
        }
        setOrdinal(ordinal);
    }



    /**
     * Configure the main arguments, hereby parsing and mapping the main arguments into
     * configuration properties as key-value pairs.
     * @param args the main arguments, not null.
     */
    public static void initMainArgs(String... args){
        CLIPropertySource.args = Objects.requireNonNull(args);
        // TODO is there a way to figure out the args?
        String argsProp = System.getProperty("main.args");
        if(argsProp!=null){
            CLIPropertySource.args = argsProp.split("\\s");
        }
        Map<String,String> result = null;
        if(CLIPropertySource.args==null){
            result = Collections.emptyMap();
        }else{
            result = new HashMap<>();
            String prefix = System.getProperty("main.args.prefix");
            if(prefix==null){
                prefix="";
            }
            String key = null;
            for(String arg:CLIPropertySource.args){
                if(arg.startsWith("--")){
                    arg = arg.substring(2);
                    int index = arg.indexOf("=");
                    if(index>0){
                        key = arg.substring(0,index).trim();
                        result.put(prefix+key, arg.substring(index+1).trim());
                        key = null;
                    }else{
                        result.put(prefix+arg, arg);
                    }
                }else if(arg.startsWith("-")){
                    key = arg.substring(1);
                }else{
                    if(key!=null){
                        result.put(prefix+key, arg);
                        key = null;
                    }else{
                        result.put(prefix+arg, arg);
                    }
                }
            }
        }
        Map<String,PropertyValue> finalProps = new HashMap<>();
        for(Map.Entry<String,String> en:result.entrySet()) {
            finalProps.put(en.getKey(),
                    PropertyValue.of(en.getKey(), en.getValue(), "main-args"));
        }
        CLIPropertySource.mainArgs = Collections.unmodifiableMap(finalProps);
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        return Collections.unmodifiableMap(mainArgs);
    }

    @Override
    protected String toStringValues() {
        return  super.toStringValues() +
                "  args=" + Arrays.toString(args) + '\n';
    }
}
