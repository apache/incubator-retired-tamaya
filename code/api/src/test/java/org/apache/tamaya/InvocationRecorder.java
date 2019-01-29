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

import static org.assertj.core.api.Assertions.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvocationRecorder {

    private List<Invocation> invocations = new ArrayList<>();

    private Object record(Object instance, Method method, Object[] args) throws Throwable {
        Invocation invocation = new Invocation(method.getName(), args);
        this.invocations.add(invocation);
        return method.invoke(instance, args);
    }

    public <T> T createProxy(Object instance, Class<T>... types) {
        return (T) Proxy.newProxyInstance(
                getClass().getClassLoader(), types,
                (proxy,method,params) -> this.record(instance, method, params));
    }

    public void recordMethodCall(Object... params) {
        Exception e = new Exception();
        String methodName = e.getStackTrace()[1].getMethodName();
        invocations.add(new Invocation(methodName, params));
    }

    public static final class Invocation{
        public String methodName;
        public Object[] params;

        public Invocation(String methodName, Object[] params) {
            this.methodName = methodName;
            this.params = params;
        }
    }

    public List<Invocation> getInvocations(){
        return invocations;
    }

    public void assertInvocation(String method, Object... params){
        for(Invocation invocation:invocations){
            if(invocation.methodName.equals(method)){
                if(Arrays.equals(invocation.params, params)){
                    return;
                }
            }
        }
        fail("No such invocation: "+method + Arrays.toString(params));
    }
}
