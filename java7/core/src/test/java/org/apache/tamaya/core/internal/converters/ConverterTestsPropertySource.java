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
    public String getName(){
        return "ConverterTestsPropertySource";
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
            case "tests.converter.byte.min":
                return "min";
            case "tests.converter.byte.max":
                return "MAX_Value";
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
            // Character
            case "tests.converter.char.f":
                return "f";
            case "tests.converter.char.d":
                return "'d'";
            case "tests.converter.char.f-before":
                return "  f";
            case "tests.converter.char.f-after":
                return "f   ";
            case "tests.converter.char.f-around":
                return "   f      ";
            case "tests.converter.char.f-numeric":
                return "101";
            // currency
            case "tests.converter.currency.code1":
                return "CHF";
            case "tests.converter.currency.code2":
                return "cHf";
            case "tests.converter.currency.code3":
                return "  CHF";
            case "tests.converter.currency.code4":
                return "CHF   ";
            case "tests.converter.currency.code5":
                return "  CHF   ";
            case "tests.converter.currency.code-numeric1":
                return "100";
            case "tests.converter.currency.code-numeric2":
                return "  100";
            case "tests.converter.currency.code-numeric3":
                return "100  ";
            case "tests.converter.currency.code-numeric4":
                return "  100  ";
            case "tests.converter.currency.code-locale1":
                return "DE";
            case "tests.converter.currency.code-locale2":
                return "  DE";
            case "tests.converter.currency.code-locale3":
                return "DE  ";
            case "tests.converter.currency.code-locale4":
                return "  DE  ";
            //double
            case "tests.converter.double.decimal":
                return "1.23456789";
            case "tests.converter.double.decimalNegative":
                return "-1.23456789";
            case "tests.converter.double.integer":
                return "  100";
            case "tests.converter.double.hex1":
                return " 0XFF";
            case "tests.converter.double.hex2":
                return "-0xFF  ";
            case "tests.converter.double.hex3":
                return "#FF";
            case "tests.converter.double.octal":
                return "0013";
            case "tests.converter.double.min":
                return "MIN_Value";
            case "tests.converter.double.max":
                return "max";
            case "tests.converter.double.nan":
                return "NAN";
            case "tests.converter.double.pi":
                return "positive_infinity";
            case "tests.converter.double.ni":
                return "Negative_Infinity";
            //float
            case "tests.converter.float.decimal":
                return "1.23456789";
            case "tests.converter.float.decimalNegative":
                return "-1.23456789";
            case "tests.converter.float.integer":
                return "  100";
            case "tests.converter.float.hex1":
                return " 0XFF";
            case "tests.converter.float.hex2":
                return "-0xFF  ";
            case "tests.converter.float.hex3":
                return "#FF";
            case "tests.converter.float.octal":
                return "0013";
            case "tests.converter.float.min":
                return "MIN_Value";
            case "tests.converter.float.max":
                return "max";
            case "tests.converter.float.nan":
                return "NAN";
            case "tests.converter.float.pi":
                return "positive_infinity";
            case "tests.converter.float.ni":
                return "Negative_Infinity";
            // Integer
            case "tests.converter.integer.decimal":
                return "101";
            case "tests.converter.integer.octal":
                return "02";
            case "tests.converter.integer.hex.lowerX":
                return "0x2F";
            case "tests.converter.integer.hex.upperX":
                return "0X3F";
            case "tests.converter.integer.min":
                return "min";
            case "tests.converter.integer.max":
                return "MAX_Value";
            // Long
            case "tests.converter.long.decimal":
                return "101";
            case "tests.converter.long.octal":
                return "02";
            case "tests.converter.long.hex.lowerX":
                return "0x2F";
            case "tests.converter.long.hex.upperX":
                return "0X3F";
            case "tests.converter.long.min":
                return "min";
            case "tests.converter.long.max":
                return "MAX_Value";
            // Short
            case "tests.converter.short.decimal":
                return "101";
            case "tests.converter.short.octal":
                return "02";
            case "tests.converter.short.hex.lowerX":
                return "0x2F";
            case "tests.converter.short.hex.upperX":
                return "0X3F";
            case "tests.converter.short.min":
                return "min";
            case "tests.converter.short.max":
                return "MAX_Value";
            // BigDecimal
            case "tests.converter.bd.decimal":
                return "101";
            case "tests.converter.bd.float":
                return "101.36438746";
            case "tests.converter.bd.big":
                return "101666666666666662333337263723628763821638923628193612983618293628763";
            case "tests.converter.bd.bigFloat":
                return "1016666666666666623333372637236287638216389293628763.101666666666666662333337263723628763821638923628193612983618293628763";
            case "tests.converter.bd.hex.lowerX":
                return "0x2F";
            case "tests.converter.bd.hex.upperX":
                return "0X3F";
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
