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
package org.apache.tamaya.model.spi;

import org.apache.tamaya.model.Validation;

import java.util.Objects;

/**
 * Default configuration Model for a configuration area.
 */
public abstract class AbstractValidation implements Validation, Comparable<Validation> {

    private String name;
    private String provider;
    private String description;
    private boolean required = false;


    protected AbstractValidation(String name, boolean required, String description, String provider) {
        this.name = Objects.requireNonNull(name);
        this.description = description;
        this.required = required;
        this.provider = provider;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public int compareTo(Validation validation) {
        int compare = getType().compareTo(validation.getType());
        if (compare != 0) {
            return compare;
        }
        return getName().compareTo(validation.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractValidation that = (AbstractValidation) o;
        return getType().equals(that.getType()) && name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return getType().hashCode() + name.hashCode();
    }
}
