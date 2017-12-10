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
package org.apache.tamaya.base;

/**
 * Some helper functions used when creating formatted text out.put.
 */
public final class FormatUtils {

    private FormatUtils(){}

    public static void appendFormatted(StringBuilder b, String text, int length) {
        int padding;
        if(text.length() <= (length)){
            b.append(text);
            padding = length - text.length();
        }else{
            b.append(text.substring(0, length-1));
            padding = 1;
        }
        for(int i=0;i<padding;i++){
            b.append(' ');
        }
    }

    public static String removeNewLines(String s) {
        return s.replace('\n', ' ').replace('\r', ' ');
    }


}
