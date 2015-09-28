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
package org.apache.tamaya.core.internal;

import java.io.IOException;
import java.nio.CharBuffer;

/**
 * Test class for testing transitively evaluated property converters.
 */
public class C extends B implements Readable{

    private String inValue;

    public C(String inValue){
        this.inValue = inValue;
    }

    @Override
    public int read(CharBuffer cb) throws IOException {
        return 0;
    }

    /**
     * Returns the input value, set on creation. Used for test assertion.
     * @return the in value.
     */
    public String getInValue() {
        return inValue;
    }

    @Override
    public String toString() {
        return "C{" +
                "inValue='" + inValue + '\'' +
                '}';
    }


}
