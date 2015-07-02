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
package org.apache.tamaya.format;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InputStreamFactoryTest {

    @Test(expected = NullPointerException.class)
    public void ctorEnforcesNonNullOriginal() throws IOException {
        new InputStreamFactory(null);
    }

    @Test
    public void givenStreamIsClosedInTryWithResourcesConstruct() throws Exception {
        InputStream stream = mock(InputStream.class);
        doReturn(34).when(stream).read();

        InputStreamFactory factory = new InputStreamFactory(stream);
        verify(stream).close();
        for (int i = 0; i < 100; i++) {
            try (InputStream in = factory.createInputStream()) {
                in.read();
            }
        }
        verify(stream).close();
    }

    @Test
    public void callToReadIsNotForwardedCallToWrapped() throws IOException {
        InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
        InputStreamFactory closer = new InputStreamFactory(stream);
        byte[] byteArray = new byte[4];
        for (int i = 0; i < 100; i++) {
            InputStream is = closer.createInputStream();
            assertThat(is.read(byteArray), equalTo(4));
        }
    }


    @Test
    public void callToSkipIsForwardedToWrapped() throws IOException {
        InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
        InputStreamFactory closer = new InputStreamFactory(stream);
        for (int i = 0; i < 100; i++) {
            InputStream is = closer.createInputStream();
            assertThat(is.skip(2L), equalTo(2L));
        }
    }


    @Test
    public void callToAvailableIsNotForwardedToWrapped() throws IOException {
        InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
        InputStreamFactory closer = new InputStreamFactory(stream);
        for (int i = 0; i < 100; i++) {
            InputStream is = closer.createInputStream();
            assertThat(is.available(), equalTo(4));
        }
    }

    @Test
    public void callToCloseIsNotForwardedToWrapped() throws IOException {
        InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
        InputStreamFactory closer = new InputStreamFactory(stream);
        for (int i = 0; i < 100; i++) {
            InputStream is = closer.createInputStream();
            is.close();
        }
    }

    @Test
    public void callToMarkIsNotForwardedToWrapped() throws IOException {
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
        InputStreamFactory closer = new InputStreamFactory(stream);
        for (int i = 0; i < 100; i++) {
            InputStream is = closer.createInputStream();
            is.mark(2);
        }
    }


    @Test
    public void callToResetIsNotForwardedToWrapped() throws IOException {
        InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
        InputStreamFactory closer = new InputStreamFactory(stream);
        for (int i = 0; i < 100; i++) {
            InputStream is = closer.createInputStream();
            is.reset();
        }
    }

    @Test
    public void callToMarkSupportedIsNotForwardedToWrapped() throws IOException {
        InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
        InputStreamFactory closer = new InputStreamFactory(stream);
        for (int i = 0; i < 100; i++) {
            InputStream is = closer.createInputStream();
            assertThat(is.markSupported(), is(true));
        }
    }

    @Test
    public void callToReadIsForwardedToWrapped() throws IOException {
        InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
        InputStreamFactory closer = new InputStreamFactory(stream);
        for (int i = 0; i < 100; i++) {
            InputStream is = closer.createInputStream();
            assertThat(is.read(), equalTo(1));
            assertThat(is.read(), equalTo(2));
            assertThat(is.read(), equalTo(3));
            assertThat(is.read(), equalTo(4));
        }
    }

}