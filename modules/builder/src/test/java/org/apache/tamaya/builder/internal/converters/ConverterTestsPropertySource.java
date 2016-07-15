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

import org.apache.tamaya.builder.spi.PropertySource;
import org.apache.tamaya.builder.spi.PropertyValue;

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
    public String getName(){
        return "ConverterTestsPropertySource";
    }

    @Override
    public PropertyValue get(String key) {
        switch(key){
            // Bytes
            case "tests.converter.byte.decimal":
                return PropertyValue.of(key, "101", getName());
            case "tests.converter.byte.octal":
                return PropertyValue.of(key, "02", getName());
            case "tests.converter.byte.hex.lowerX":
                return PropertyValue.of(key, "0x2F", getName());
            case "tests.converter.byte.hex.upperX":
                return PropertyValue.of(key, "0X3F", getName());
            case "tests.converter.byte.min":
                return PropertyValue.of(key, "min", getName());
            case "tests.converter.byte.max":
                return PropertyValue.of(key, "MAX_Value", getName());
            // Boolean
            case "tests.converter.boolean.y1":
                return PropertyValue.of(key, "y", getName());
            case "tests.converter.boolean.y2":
                return PropertyValue.of(key, "Y", getName());
            case "tests.converter.boolean.yes1":
                return PropertyValue.of(key, "yes", getName());
            case "tests.converter.boolean.yes2":
                return PropertyValue.of(key, "Yes", getName());
            case "tests.converter.boolean.yes3":
                return PropertyValue.of(key, "yeS", getName());
            case "tests.converter.boolean.true1":
                return PropertyValue.of(key, "true", getName());
            case "tests.converter.boolean.true2":
                return PropertyValue.of(key, "True", getName());
            case "tests.converter.boolean.true3":
                return PropertyValue.of(key, "trUe", getName());
            case "tests.converter.boolean.t1":
                return PropertyValue.of(key, "t", getName());
            case "tests.converter.boolean.t2":
                return PropertyValue.of(key, "T", getName());
            case "tests.converter.boolean.n1":
                return PropertyValue.of(key, "n", getName());
            case "tests.converter.boolean.n2":
                return PropertyValue.of(key, "N", getName());
            case "tests.converter.boolean.no1":
                return PropertyValue.of(key, "no", getName());
            case "tests.converter.boolean.no2":
                return PropertyValue.of(key, "No", getName());
            case "tests.converter.boolean.no3":
                return PropertyValue.of(key, "nO", getName());
            case "tests.converter.boolean.false1":
                return PropertyValue.of(key, "false", getName());
            case "tests.converter.boolean.false2":
                return PropertyValue.of(key, "False", getName());
            case "tests.converter.boolean.false3":
                return PropertyValue.of(key, "falSe", getName());
            case "tests.converter.boolean.f1":
                return PropertyValue.of(key, "f", getName());
            case "tests.converter.boolean.f2":
                return PropertyValue.of(key, "F", getName());
            // Character
            case "tests.converter.char.f":
                return PropertyValue.of(key, "f", getName());
            case "tests.converter.char.d":
                return PropertyValue.of(key, "'d'", getName());
            case "tests.converter.char.f-before":
                return PropertyValue.of(key, "  f", getName());
            case "tests.converter.char.f-after":
                return PropertyValue.of(key, "f   ", getName());
            case "tests.converter.char.f-around":
                return PropertyValue.of(key, "   f      ", getName());
            case "tests.converter.char.f-numeric":
                return PropertyValue.of(key, "101", getName());
            // currency
            case "tests.converter.currency.code1":
                return PropertyValue.of(key, "CHF", getName());
            case "tests.converter.currency.code2":
                return PropertyValue.of(key, "cHf", getName());
            case "tests.converter.currency.code3":
                return PropertyValue.of(key, "  CHF", getName());
            case "tests.converter.currency.code4":
                return PropertyValue.of(key, "CHF   ", getName());
            case "tests.converter.currency.code5":
                return PropertyValue.of(key, "  CHF   ", getName());
            case "tests.converter.currency.code-numeric1":
                return PropertyValue.of(key, "100", getName());
            case "tests.converter.currency.code-numeric2":
                return PropertyValue.of(key, "  100", getName());
            case "tests.converter.currency.code-numeric3":
                return PropertyValue.of(key, "100  ", getName());
            case "tests.converter.currency.code-numeric4":
                return PropertyValue.of(key, "  100  ", getName());
            case "tests.converter.currency.code-locale1":
                return PropertyValue.of(key, "DE", getName());
            case "tests.converter.currency.code-locale2":
                return PropertyValue.of(key, "  DE", getName());
            case "tests.converter.currency.code-locale3":
                return PropertyValue.of(key, "DE  ", getName());
            case "tests.converter.currency.code-locale4":
                return PropertyValue.of(key, "  DE  ", getName());
            //double
            case "tests.converter.double.decimal":
                return PropertyValue.of(key, "1.23456789", getName());
            case "tests.converter.double.decimalNegative":
                return PropertyValue.of(key, "-1.23456789", getName());
            case "tests.converter.double.integer":
                return PropertyValue.of(key, "  100", getName());
            case "tests.converter.double.hex1":
                return PropertyValue.of(key, " 0XFF", getName());
            case "tests.converter.double.hex2":
                return PropertyValue.of(key, "-0xFF  ", getName());
            case "tests.converter.double.hex3":
                return PropertyValue.of(key, "#FF", getName());
            case "tests.converter.double.octal":
                return PropertyValue.of(key, "0013", getName());
            case "tests.converter.double.min":
                return PropertyValue.of(key, "MIN_Value", getName());
            case "tests.converter.double.max":
                return PropertyValue.of(key, "max", getName());
            case "tests.converter.double.nan":
                return PropertyValue.of(key, "NAN", getName());
            case "tests.converter.double.pi":
                return PropertyValue.of(key, "positive_infinity", getName());
            case "tests.converter.double.ni":
                return PropertyValue.of(key, "Negative_Infinity", getName());
            //float
            case "tests.converter.float.decimal":
                return PropertyValue.of(key, "1.23456789", getName());
            case "tests.converter.float.decimalNegative":
                return PropertyValue.of(key, "-1.23456789", getName());
            case "tests.converter.float.integer":
                return PropertyValue.of(key, "  100", getName());
            case "tests.converter.float.hex1":
                return PropertyValue.of(key, " 0XFF", getName());
            case "tests.converter.float.hex2":
                return PropertyValue.of(key, "-0xFF  ", getName());
            case "tests.converter.float.hex3":
                return PropertyValue.of(key, "#FF", getName());
            case "tests.converter.float.octal":
                return PropertyValue.of(key, "0013", getName());
            case "tests.converter.float.min":
                return PropertyValue.of(key, "MIN_Value", getName());
            case "tests.converter.float.max":
                return PropertyValue.of(key, "max", getName());
            case "tests.converter.float.nan":
                return PropertyValue.of(key, "NAN", getName());
            case "tests.converter.float.pi":
                return PropertyValue.of(key, "positive_infinity", getName());
            case "tests.converter.float.ni":
                return PropertyValue.of(key, "Negative_Infinity", getName());
            // Integer
            case "tests.converter.integer.decimal":
                return PropertyValue.of(key, "101", getName());
            case "tests.converter.integer.octal":
                return PropertyValue.of(key, "02", getName());
            case "tests.converter.integer.hex.lowerX":
                return PropertyValue.of(key, "0x2F", getName());
            case "tests.converter.integer.hex.upperX":
                return PropertyValue.of(key, "0X3F", getName());
            case "tests.converter.integer.min":
                return PropertyValue.of(key, "min", getName());
            case "tests.converter.integer.max":
                return PropertyValue.of(key, "MAX_Value", getName());
            // Long
            case "tests.converter.long.decimal":
                return PropertyValue.of(key, "101", getName());
            case "tests.converter.long.octal":
                return PropertyValue.of(key, "02", getName());
            case "tests.converter.long.hex.lowerX":
                return PropertyValue.of(key, "0x2F", getName());
            case "tests.converter.long.hex.upperX":
                return PropertyValue.of(key, "0X3F", getName());
            case "tests.converter.long.min":
                return PropertyValue.of(key, "min", getName());
            case "tests.converter.long.max":
                return PropertyValue.of(key, "MAX_Value", getName());
            // Short
            case "tests.converter.short.decimal":
                return PropertyValue.of(key, "101", getName());
            case "tests.converter.short.octal":
                return PropertyValue.of(key, "02", getName());
            case "tests.converter.short.hex.lowerX":
                return PropertyValue.of(key, "0x2F", getName());
            case "tests.converter.short.hex.upperX":
                return PropertyValue.of(key, "0X3F", getName());
            case "tests.converter.short.min":
                return PropertyValue.of(key, "min", getName());
            case "tests.converter.short.max":
                return PropertyValue.of(key, "MAX_Value", getName());
            // BigDecimal
            case "tests.converter.bd.decimal":
                return PropertyValue.of(key, "101", getName());
            case "tests.converter.bd.float":
                return PropertyValue.of(key, "101.36438746", getName());
            case "tests.converter.bd.big":
                return PropertyValue.of(key, "101666666666666662333337263723628763821638923628193612983618293628763", getName());
            case "tests.converter.bd.bigFloat":
                return PropertyValue.of(key, "1016666666666666623333372637236287638216389293628763.101666666666666662333337263723628763821638923628193612983618293628763", getName());
            case "tests.converter.bd.hex.lowerX":
                return PropertyValue.of(key, "0x2F", getName());
            case "tests.converter.bd.hex.upperX":
                return PropertyValue.of(key, "0X3F", getName());
        }
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    @Override
    public boolean isScannable() {
        return false;
    }
}
