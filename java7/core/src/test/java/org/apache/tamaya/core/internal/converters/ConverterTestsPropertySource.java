/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.core.internal.converters;

import org.apache.tamaya.spi.PropertySource;

import java.util.Collections;
import java.util.Map;

/**
 * Test Property Source used by converter tests.
 */
public class ConverterTestsPropertySource implements PropertySource{
    @Override
    public int getOrdinal() {
        return 0;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public String get(String key) {
        switch(key){
            // Bytes
            case "tests.converter.byte.decimal":
                return "101";
            case "tests.converter.byte.octal":
                return "02";
            case "tests.converter.byte.hex.lowerX":
                return "0x2F";
            case "tests.converter.byte.hex.upperX":
                return "0X3F";
            // Boolean
            case "tests.converter.boolean.y1":
                return "y";
            case "tests.converter.boolean.y2":
                return "Y";
            case "tests.converter.boolean.yes1":
                return "yes";
            case "tests.converter.boolean.yes2":
                return "Yes";
            case "tests.converter.boolean.yes3":
                return "yeS";
            case "tests.converter.boolean.true1":
                return "true";
            case "tests.converter.boolean.true2":
                return "True";
            case "tests.converter.boolean.true3":
                return "trUe";
            case "tests.converter.boolean.t1":
                return "t";
            case "tests.converter.boolean.t2":
                return "T";
            case "tests.converter.boolean.n1":
                return "n";
            case "tests.converter.boolean.n2":
                return "N";
            case "tests.converter.boolean.no1":
                return "no";
            case "tests.converter.boolean.no2":
                return "No";
            case "tests.converter.boolean.no3":
                return "nO";
            case "tests.converter.boolean.false1":
                return "false";
            case "tests.converter.boolean.false2":
                return "False";
            case "tests.converter.boolean.false3":
                return "falSe";
            case "tests.converter.boolean.f1":
                return "f";
            case "tests.converter.boolean.f2":
                return "F";
        }
        return null;
    }

    /*
    case "yes":
            case "y":
            case "true":
            case "t":
                return Boolean.TRUE;
            case "no":
            case "n":
            case "false":
            case "f":
     */
    @Override
    public Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    @Override
    public boolean isScannable() {
        return false;
    }
}
