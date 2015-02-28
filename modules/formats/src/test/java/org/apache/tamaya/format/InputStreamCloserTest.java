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

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InputStreamCloserTest {

    @Test(expected = NullPointerException.class)
    public void ctorEnforcesNonNullOriginal() {
        new InputStreamCloser(null);
    }

    @Test
    public void givenStreamIsClosedInTryWithResourcesConstruct() throws Exception {
        InputStream stream = mock(InputStream.class);

        doReturn(34).when(stream).read();

        try (InputStream in = new InputStreamCloser(stream)) {
            in.read();
        }

        verify(stream).close();
    }

    @Test
    public void callToReadIsForwardedCallToWrapped() throws IOException {
        InputStream stream = mock(InputStream.class);

        doReturn(34).when(stream).read(Mockito.anyVararg());

        InputStreamCloser closer = new InputStreamCloser(stream);

        byte[] byteArray = new byte[4];
        assertThat(closer.read(byteArray), equalTo(34));

        verify(stream).read(byteArray);
    }


    @Test
    public void callToReadWithOffsetIsForwardedCallToWrapped() throws IOException {
        byte[] array = new byte[10];
        ArgumentCaptor<byte[]> arrayCaptor = ArgumentCaptor.forClass(byte[].class);
        ArgumentCaptor<Integer> offsetCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);
        InputStream stream = mock(InputStream.class);

        doReturn(23).when(stream).read(Mockito.anyVararg(), Mockito.anyInt(), Mockito.anyInt());

        InputStreamCloser closer = new InputStreamCloser(stream);

        assertThat(closer.read(array, 10, 20), equalTo(23));

        verify(stream).read(arrayCaptor.capture(), offsetCaptor.capture(), lengthCaptor.capture());

        assertThat(offsetCaptor.getValue(), equalTo(10));
        assertThat(lengthCaptor.getValue(), equalTo(20));
        assertThat(arrayCaptor.getValue(), equalTo(array));
    }

    @Test
    public void callToSkipIsForwardedToWrapped() throws IOException {
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        InputStream stream = mock(InputStream.class);

        doReturn(1234L).when(stream).skip(anyLong());

        InputStreamCloser closer = new InputStreamCloser(stream);

        assertThat(closer.skip(4567L), equalTo(1234L));

        verify(stream).skip(captor.capture());

        assertThat(captor.getValue(), equalTo(4567L));
    }


    @Test
    public void callToAvailableIsForwardedToWrapped() throws IOException {
        InputStream stream = mock(InputStream.class);

        doReturn(123).when(stream).available();

        InputStreamCloser closer = new InputStreamCloser(stream);

        assertThat(closer.available(), equalTo(123));

        verify(stream).available();
    }

    @Test
    public void callToCloseIsForwardedToWrapped() throws IOException {
        InputStream stream = mock(InputStream.class);
        InputStreamCloser closer = new InputStreamCloser(stream);

        closer.close();

        verify(stream).close();
    }

    @Test
    public void callToMarkIsForwardedToWrapped() {
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        InputStream stream = mock(InputStream.class);
        InputStreamCloser closer = new InputStreamCloser(stream);

        closer.mark(456);

        verify(stream).mark(captor.capture());

        assertThat(captor.getValue(), equalTo(456));
    }


    @Test
    public void callToResetIsForwardedToWrapped() throws IOException {
        InputStream stream = mock(InputStream.class);

        InputStreamCloser closer = new InputStreamCloser(stream);

        closer.reset();

        verify(stream).reset();
    }

    @Test
    public void callToMarkSupportedIsForwardedToWrapped() {
        InputStream stream = mock(InputStream.class);

        doReturn(false).when(stream).markSupported();

        InputStreamCloser closer = new InputStreamCloser(stream);

        assertThat(closer.markSupported(), is(false));

        verify(stream).markSupported();
    }

    @Test
    public void callToReadIsForwardedToWrapped() throws IOException {
        InputStream stream = mock(InputStream.class);
        InputStreamCloser closer = new InputStreamCloser(stream);

        doReturn(4).when(stream).read();

        assertThat(closer.read(), equalTo(4));

        verify(stream).read();
    }

}