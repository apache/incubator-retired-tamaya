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
package org.apache.tamaya;


/**
 * Interface for an property that converts a configured String into something else.
 * This is used for implementing type conversion from a property (String) to a certain target
 * type. Hereby the target type can be multivalued (eg collections) or complex if needed.
 * @param <T> the result/target type of the converter.
 */
public interface PropertyConverter<T> {

    /**
     * Convert the given configuration keys from it's String representation into the required target type.
     * <b>IMPORTANT NOTE: </b> Multiple instances of this type are ordered in a chain of converters that
     * try to parse/convert a configured value. The first non-null result returned by a converter is
     * used as the final result of the conversion. As a consequence implementations of this class must
     * only return non-null values that are the result of a successful conversion of an entry.
     *
     * @param value the configuration value, not null.
     * @return the converted value, or {@code null} if the value could not be converted. Returning a non-null
     *         value will stop the conversion process and return the value as result (converted value).
     */
    T convert(String value);

    //X TODO probably add some diagnostic info which explains what kind of
    //X format(s) is supported.
    //X This could be useful if e.g. no converter in the chain felt responsible
    //X because a wrongly formatted configuration string had been used.
    //X This could probably also be handled via an additional Annotation on the converter.
    //X Collection<String> getSupportedFormats();
}
