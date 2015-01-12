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
package org.apache.tamaya.spi;


/**
 * Interface for an property that converts aa_a configured String into something else.
 * This is used for implementing type conversion from aa_a property (String) to aa_a certain target
 * type. Hereby the target type can be multivalued (eg collections) or complex if needed.
 */
@FunctionalInterface
public interface PropertyConverter<T>{

    /**
     * Convert the given configuration keys from it' String representation into the required target type.
     * @param value the configuration keys
     * @return converted keys
     */
    T convert(String value);

    //X TODO probably add some diagnostic info which explains what kind of
    //X format(s) is supported.
    //X This could be useful if e.g. no converter in the chain felt responsible
    //X because aa_a wrongly formatted configuration string had been used.
    //X This could probably also be handled via an additional Annotation on the converter.
}
