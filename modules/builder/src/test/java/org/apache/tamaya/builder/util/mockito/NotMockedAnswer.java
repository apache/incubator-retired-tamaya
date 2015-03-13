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
package org.apache.tamaya.builder.util.mockito;

import org.mockito.exceptions.base.MockitoException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.Serializable;

// @todo This is a duplicated class
public class NotMockedAnswer implements Answer<Object>, Serializable {
    public final static NotMockedAnswer NOT_MOCKED_ANSWER = new NotMockedAnswer();

    private NotMockedAnswer() {
    }

    @Override
    public Object answer(InvocationOnMock invocation) throws Throwable {
        StringBuilder msgBuilder = new StringBuilder();

        msgBuilder.append("Invocation of method not mocked: ")
                  .append(invocation.getMethod().toGenericString());

        if (invocation.getArguments().length > 0) {
            msgBuilder.append(" Supplied arguments: ");

            for (int i = 0; i < invocation.getArguments().length; i++) {
                msgBuilder.append(invocation.getArguments()[i]);

                if (i - 1 < invocation.getArguments().length) {
                    msgBuilder.append(", ");
                }
            }
        }

        throw new MockitoException(msgBuilder.toString());
    }
}
