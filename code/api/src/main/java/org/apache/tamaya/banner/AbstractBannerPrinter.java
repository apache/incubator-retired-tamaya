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
package org.apache.tamaya.banner;

import org.apache.tamaya.ConfigurationProvider;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base class for an implementation of the
 * {@link BannerPrinter} providing the common functionality
 * of all implementations.
 */
abstract class AbstractBannerPrinter implements BannerPrinter {
    private static final Logger log = Logger.getLogger(AbstractBannerPrinter.class.getName());

    @Override
    public void outputBanner() {
        try {
            URL url = ConfigurationProvider.class.getResource("/tamaya-banner.txt");

            if (url != null) {
                Path path = Paths.get(url.toURI());
                List<String> content = Files.readAllLines(path, StandardCharsets.UTF_8);

                for (String line : content) {
                    outputSingleLine(line);
                }
            }
        } catch (Exception e) {
            log.log(Level.FINE, "Failed to output the banner of tamaya.", e);
        }
    }

    abstract void outputSingleLine(CharSequence line);
}
