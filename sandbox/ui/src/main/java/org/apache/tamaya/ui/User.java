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
package org.apache.tamaya.ui;

import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.internal.ResourceBundleMessageProvider;
import org.apache.tamaya.ui.services.MessageProvider;

import java.util.Date;
import java.util.Objects;

/**
 * Created by atsticks on 29.03.16.
 */
public class User {

    private String userID = "-";
    private String fulLName = ServiceContextManager.getServiceContext().getService(MessageProvider.class)
            .getMessage("default.label.unknown");
    private Date logInDate = new Date();

    public User(String userID, String fullName){
        this.userID = Objects.requireNonNull(userID);
        this.fulLName = fullName;
        if(fullName==null){
            this.fulLName = userID;
        }
    }

    public String getUserID() {
        return userID;
    }

    public String getFullName() {
        return fulLName;
    }

    public String getLoginDate(){
        return logInDate.toString();
    }
}
