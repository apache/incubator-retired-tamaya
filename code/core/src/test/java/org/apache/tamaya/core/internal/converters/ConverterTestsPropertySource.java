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
import org.apache.tamaya.spi.PropertyValue;

import java.util.Collections;
import java.util.Map;

/**
 * Test Property Source used by converter tests.
 */
public class ConverterTestsPropertySource implements PropertySource {

    @Override
    public int getOrdinal() {
        return 0;
    }

    @Override
    public String getName() {
        return "ConverterTestsPropertySource";
    }

    @Override
    public PropertyValue get(String key) {
        switch (key) {
            // Bytes
            case "tests.converter.byte.decimal":
                return PropertyValue.createValue(key, "101");
            case "tests.converter.byte.octal":
                return PropertyValue.createValue(key, "02");
            case "tests.converter.byte.hex.lowerX":
                return PropertyValue.createValue(key, "0x2F");
            case "tests.converter.byte.hex.upperX":
                return PropertyValue.createValue(key, "0X3F");
            case "tests.converter.byte.min":
                return PropertyValue.createValue(key, "min");
            case "tests.converter.byte.max":
                return PropertyValue.createValue(key, "MAX_Value");
            case "tests.converter.byte.invalid":
                return PropertyValue.createValue(key, "invalid");
            // Boolean
            case "tests.converter.boolean.y1":
                return PropertyValue.createValue(key, "y");
            case "tests.converter.boolean.y2":
                return PropertyValue.createValue(key, "Y");
            case "tests.converter.boolean.yes1":
                return PropertyValue.createValue(key, "yes");
            case "tests.converter.boolean.yes2":
                return PropertyValue.createValue(key, "Yes");
            case "tests.converter.boolean.yes3":
                return PropertyValue.createValue(key, "yeS");
            case "tests.converter.boolean.true1":
                return PropertyValue.createValue(key, "true");
            case "tests.converter.boolean.true2":
                return PropertyValue.createValue(key, "True");
            case "tests.converter.boolean.true3":
                return PropertyValue.createValue(key, "trUe");
            case "tests.converter.boolean.t1":
                return PropertyValue.createValue(key, "t");
            case "tests.converter.boolean.t2":
                return PropertyValue.createValue(key, "T");
            case "tests.converter.boolean.n1":
                return PropertyValue.createValue(key, "n");
            case "tests.converter.boolean.n2":
                return PropertyValue.createValue(key, "N");
            case "tests.converter.boolean.no1":
                return PropertyValue.createValue(key, "no");
            case "tests.converter.boolean.no2":
                return PropertyValue.createValue(key, "No");
            case "tests.converter.boolean.no3":
                return PropertyValue.createValue(key, "nO");
            case "tests.converter.boolean.false1":
                return PropertyValue.createValue(key, "false");
            case "tests.converter.boolean.false2":
                return PropertyValue.createValue(key, "False");
            case "tests.converter.boolean.false3":
                return PropertyValue.createValue(key, "falSe");
            case "tests.converter.boolean.f1":
                return PropertyValue.createValue(key, "f");
            case "tests.converter.boolean.f2":
                return PropertyValue.createValue(key, "F");
            case "tests.converter.boolean.invalid":
                return PropertyValue.createValue(key, "invalid");
            // Character
            case "tests.converter.char.f":
                return PropertyValue.createValue(key, "f");
            case "tests.converter.char.d":
                return PropertyValue.createValue(key, "'d'");
            case "tests.converter.char.f-before":
                return PropertyValue.createValue(key, "  f");
            case "tests.converter.char.f-after":
                return PropertyValue.createValue(key, "f   ");
            case "tests.converter.char.f-around":
                return PropertyValue.createValue(key, "   f      ");
            case "tests.converter.char.f-numeric":
                return PropertyValue.createValue(key, "101");
            case "tests.converter.char.single-quote":
                return PropertyValue.createValue(key, "'");
            case "tests.converter.char.two-single-quotes":
                return PropertyValue.createValue(key, "''");
            case "tests.converter.char.three-single-quotes":
                return PropertyValue.createValue(key, "'''");
            case "tests.converter.char.invalid":
                return PropertyValue.createValue(key, "invalid");
            case "tests.converter.char.quoted-invalid":
                return PropertyValue.createValue(key, "'invalid'");
            case "tests.converter.char.あ":
                return PropertyValue.createValue(key, "あ");
            case "tests.converter.char.กขฃคฅฆงจฉช":
                return PropertyValue.createValue(key, "กขฃคฅฆงจฉช");

            // currency
            case "tests.converter.currency.code1":
                return PropertyValue.createValue(key, "CHF");
            case "tests.converter.currency.code2":
                return PropertyValue.createValue(key, "cHf");
            case "tests.converter.currency.code3":
                return PropertyValue.createValue(key, "  CHF");
            case "tests.converter.currency.code4":
                return PropertyValue.createValue(key, "CHF   ");
            case "tests.converter.currency.code5":
                return PropertyValue.createValue(key, "  CHF   ");
            case "tests.converter.currency.code-numeric1":
                return PropertyValue.createValue(key, "100");
            case "tests.converter.currency.code-numeric2":
                return PropertyValue.createValue(key, "  100");
            case "tests.converter.currency.code-numeric3":
                return PropertyValue.createValue(key, "100  ");
            case "tests.converter.currency.code-numeric4":
                return PropertyValue.createValue(key, "  100  ");
            case "tests.converter.currency.code-locale1":
                return PropertyValue.createValue(key, "DE");
            case "tests.converter.currency.code-locale2":
                return PropertyValue.createValue(key, "  DE");
            case "tests.converter.currency.code-locale3":
                return PropertyValue.createValue(key, "DE  ");
            case "tests.converter.currency.code-locale4":
                return PropertyValue.createValue(key, "  DE  ");
            case "tests.converter.currency.code-locale-twopart":
                return PropertyValue.createValue(key, "jp_JP");
            case "tests.converter.currency.code-locale-threepart":
                return PropertyValue.createValue(key, "jp_JP_JP");
            case "tests.converter.currency.code-locale-fourpart":
                return PropertyValue.createValue(key, "jp_JP_JP_JP");
            case "tests.converter.currency.invalid":
                return PropertyValue.createValue(key, "invalid");
            //double
            case "tests.converter.double.decimal":
                return PropertyValue.createValue(key, "1.23456789");
            case "tests.converter.double.decimalNegative":
                return PropertyValue.createValue(key, "-1.23456789");
            case "tests.converter.double.integer":
                return PropertyValue.createValue(key, "  100");
            case "tests.converter.double.hex1":
                return PropertyValue.createValue(key, " 0XFF");
            case "tests.converter.double.hex2":
                return PropertyValue.createValue(key, "-0xFF  ");
            case "tests.converter.double.hex3":
                return PropertyValue.createValue(key, "#FF");
            case "tests.converter.double.octal":
                return PropertyValue.createValue(key, "0013");
            case "tests.converter.double.min":
                return PropertyValue.createValue(key, "MIN_Value");
            case "tests.converter.double.max":
                return PropertyValue.createValue(key, "max");
            case "tests.converter.double.nan":
                return PropertyValue.createValue(key, "NAN");
            case "tests.converter.double.pi":
                return PropertyValue.createValue(key, "positive_infinity");
            case "tests.converter.double.ni":
                return PropertyValue.createValue(key, "Negative_Infinity");
            case "tests.converter.double.invalid":
                return PropertyValue.createValue(key, "invalid");
            //float
            case "tests.converter.float.decimal":
                return PropertyValue.createValue(key, "1.23456789");
            case "tests.converter.float.decimalNegative":
                return PropertyValue.createValue(key, "-1.23456789");
            case "tests.converter.float.integer":
                return PropertyValue.createValue(key, "  100");
            case "tests.converter.float.hex1":
                return PropertyValue.createValue(key, " 0XFF");
            case "tests.converter.float.hex2":
                return PropertyValue.createValue(key, "-0xFF  ");
            case "tests.converter.float.hex3":
                return PropertyValue.createValue(key, "#FF");
            case "tests.converter.float.octal":
                return PropertyValue.createValue(key, "0013");
            case "tests.converter.float.min":
                return PropertyValue.createValue(key, "MIN_Value");
            case "tests.converter.float.max":
                return PropertyValue.createValue(key, "max");
            case "tests.converter.float.nan":
                return PropertyValue.createValue(key, "NAN");
            case "tests.converter.float.pi":
                return PropertyValue.createValue(key, "positive_infinity");
            case "tests.converter.float.ni":
                return PropertyValue.createValue(key, "Negative_Infinity");
            case "tests.converter.float.invalid":
                return PropertyValue.createValue(key, "invalid");
            // Integer
            case "tests.converter.integer.decimal":
                return PropertyValue.createValue(key, "101");
            case "tests.converter.integer.octal":
                return PropertyValue.createValue(key, "02");
            case "tests.converter.integer.hex.lowerX":
                return PropertyValue.createValue(key, "0x2F");
            case "tests.converter.integer.hex.upperX":
                return PropertyValue.createValue(key, "0X3F");
            case "tests.converter.integer.min":
                return PropertyValue.createValue(key, "min");
            case "tests.converter.integer.max":
                return PropertyValue.createValue(key, "MAX_Value");
            case "tests.converter.integer.invalid":
                return PropertyValue.createValue(key, "invalid");
            // Long
            case "tests.converter.long.decimal":
                return PropertyValue.createValue(key, "101");
            case "tests.converter.long.octal":
                return PropertyValue.createValue(key, "02");
            case "tests.converter.long.hex.lowerX":
                return PropertyValue.createValue(key, "0x2F");
            case "tests.converter.long.hex.upperX":
                return PropertyValue.createValue(key, "0X3F");
            case "tests.converter.long.min":
                return PropertyValue.createValue(key, "min");
            case "tests.converter.long.max":
                return PropertyValue.createValue(key, "MAX_Value");
            case "tests.converter.long.invalid":
                return PropertyValue.createValue(key, "invalid");
            // Short
            case "tests.converter.short.decimal":
                return PropertyValue.createValue(key, "101");
            case "tests.converter.short.octal":
                return PropertyValue.createValue(key, "02");
            case "tests.converter.short.hex.lowerX":
                return PropertyValue.createValue(key, "0x2F");
            case "tests.converter.short.hex.upperX":
                return PropertyValue.createValue(key, "0X3F");
            case "tests.converter.short.min":
                return PropertyValue.createValue(key, "min");
            case "tests.converter.short.max":
                return PropertyValue.createValue(key, "MAX_Value");
            case "tests.converter.short.invalid":
                return PropertyValue.createValue(key, "invalid");
            // BigDecimal & BigInteger
            case "tests.converter.bd.decimal":
                return PropertyValue.createValue(key, "101");
            case "tests.converter.bd.float":
                return PropertyValue.createValue(key, "101.36438746");
            case "tests.converter.bd.big":
                return PropertyValue.createValue(key, "101666666666666662333337263723628763821638923628193612983618293628763");
            case "tests.converter.bd.bigFloat":
                return PropertyValue.createValue(key, "1016666666666666623333372637236287638216389293628763.101666666666666662333337263723628763821638923628193612983618293628763");
            case "tests.converter.bd.hex.lowerX":
                return PropertyValue.createValue(key, "0x2F");
            case "tests.converter.bd.hex.upperX":
                return PropertyValue.createValue(key, "0X3F");
            case "tests.converter.bd.hex.negLowerX":
                return PropertyValue.createValue(key, "-0x2F");
            case "tests.converter.bd.hex.negUpperX":
                return PropertyValue.createValue(key, "-0X3F");
            case "tests.converter.bd.hex.badX":
                return PropertyValue.createValue(key, "0X3G2");
            case "tests.converter.bd.hex.negBadX":
                return PropertyValue.createValue(key, "-0X3G2");
            case "tests.converter.bd.hex.subTenX":
                return PropertyValue.createValue(key, "0XFFFFFF");
            case "tests.converter.bd.hex.negSubTenX":
                return PropertyValue.createValue(key, "-0X0107");
            case "tests.converter.bd.invalid":
                return PropertyValue.createValue(key, "invalid");
            default:
                return null;
        }
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        return Collections.emptyMap();
    }
}
