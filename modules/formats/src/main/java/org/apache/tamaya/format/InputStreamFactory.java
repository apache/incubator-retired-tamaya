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

import java.io.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper for a given {@link InputStream} to be able to close
 * it via the try-with-resources construct of Java 7.
 *
 * <h1>Usage example</h1>
 *
 * <pre>
 * public void readIt(InputStream inputStream) {
 *    try (InputStream is = new ParallelInputStream(inputStream) {
 *        // Consume the stream
 *    }
 * }
 * </pre>
 */
public class InputStreamFactory implements Closeable {
    private static final Logger LOG = Logger.getLogger(InputStreamFactory.class.getName());

    private byte[] data;

    /**
     * Creates a new InputStreamFactory.
     *
     * @param original the InputStream to be read for extract its data into memory.
     * @throws IOException if thrown by the original during read.
     */
    public InputStreamFactory(InputStream original) throws IOException {
        Objects.requireNonNull(original);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[256];
        try {
            int read = original.read(bytes);
            while (read > 0) {
                bos.write(bytes, 0, read);
                read = original.read(bytes);
            }
            this.data = bos.toByteArray();
        } finally {
            try {
                original.close();
            } catch (IOException e) {
                LOG.log(Level.FINEST, "Error closing stream: " + original, e);
            }
        }
    }

    /**
     * Creates a new InputStream with the same data as provided by the InputStream passed on factory creation.
     *
     * @return a new InputStream , never null.
     * @throws IOException if no data is available.
     */
    public InputStream createInputStream() throws IOException {
        byte[] bytes = this.data;
        if (bytes == null) {
            throw new IOException("InputStreamFactory is closed.");
        }
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void close() throws IOException {
        this.data = null;
    }
}
