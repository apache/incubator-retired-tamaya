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
package org.apache.tamaya.ui.event;


import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * Convenience class for accessing the _UI Scoped_ EventBus. If you are using something like the CDI event
 * bus, you don't need a class like this.
 */
public final class EventBus {

    private static final com.google.common.eventbus.EventBus EVENT_BUS =
            new com.google.common.eventbus.EventBus(new SubscriberExceptionHandler(){
                @Override
                public void handleException(Throwable throwable, SubscriberExceptionContext subscriberExceptionContext) {
                    throwable.printStackTrace();
                }
            });

    private EventBus(){}

    public static void register(final Object listener) {
        EVENT_BUS.register(listener);
    }

    public static void unregister(final Object listener) {
        EVENT_BUS.unregister(listener);
    }

    public static void post(final Object event) {
        EVENT_BUS.post(event);
    }
}