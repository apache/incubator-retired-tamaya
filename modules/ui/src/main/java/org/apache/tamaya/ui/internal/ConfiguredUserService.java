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
package org.apache.tamaya.ui.internal;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.functions.ConfigurationFunctions;
import org.apache.tamaya.ui.User;
import org.apache.tamaya.ui.services.UserService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User service reading users and credentials from the configuration. Users are configured as follows (e.g. using
 * properties format):
 * <pre>
 * tamaya.users.admin.pwd=admin
 * tamaya.users.admin.fullName=Administrator
 * tamaya.users.admin.roles=admin
 * tamaya.users.john.pwd=meymey
 * tamaya.users.john.fullName=John Doe
 * tamaya.users.john.roles=admin,user
 * </pre>
 */
public class ConfiguredUserService implements UserService{

    private Map<String, User> users = new ConcurrentHashMap<>();

    /**
     * Constructor reading the configuration and initializing the users table.
     */
    public ConfiguredUserService(){
        // read from config
        Map<String,String> config = ConfigurationProvider.getConfiguration().with(
                ConfigurationFunctions.section("tamaya.users.", true)).getProperties();
        for(Map.Entry<String,String> en:config.entrySet()){
            if(en.getKey().endsWith(".pwd")){
                String uid = en.getKey().substring(0,en.getKey().length()-4);
                String pwd = en.getValue();
                String fullName = config.get(uid+".fullName");
                String roles = config.get(uid+".roles");
                if(roles==null){
                    roles="";
                }
                users.put(uid.toLowerCase(), new User(uid, fullName, pwd, roles.split(",")));
            }
        }

    }

    @Override
    public User login(String userId, String credentials) {
        User user = this.users.get(userId.toLowerCase());
        if(user!=null && user.login(credentials)){
            return user;
        }
        return null;
    }

}
