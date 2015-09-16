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
package org.apache.tamaya.integration.cdi.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.integration.cdi.annot.Webapp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by Anatole on 16.09.2015.
 */
@RequestScoped @Webapp
public class CDIManagedSessionConfiguration implements Configuration{

    @Inject @org.apache.tamaya.integration.cdi.annot.System
    private Configuration systemConfig;


    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        return null;
    }
}
