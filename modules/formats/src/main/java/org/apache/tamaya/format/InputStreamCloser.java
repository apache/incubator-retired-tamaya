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

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Wrapper for a given {@link InputStream} to be able to close
 * it via the try-with-resources construct of Java 7.
 *
 * <h1>Usage example</h1>
 *
 * <pre>
 *public void readIt(InputStream inputStream) {
 *    try (InputStream is = new InputStreamCloser(inputStream) {
 *        // Consume the stream
 *    }
 *}
 * </pre>
 */
public class InputStreamCloser extends InputStream {
    private InputStream wrapped;

    public InputStreamCloser(InputStream original) {
        Objects.requireNonNull(original);

        wrapped = original;
    }


    @Override
    public int read(byte[] b) throws IOException {
        return wrapped.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return wrapped.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return wrapped.skip(n);
    }

    @Override
    public int available() throws IOException {
        return wrapped.available();
    }

    @Override
    public void close() throws IOException {
        wrapped.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        wrapped.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        wrapped.reset();
    }

    @Override
    public boolean markSupported() {
        return wrapped.markSupported();
    }

    @Override
    public int read() throws IOException {
        return wrapped.read();
    }
}
