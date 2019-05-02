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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.Configuration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.tamaya.core.internal.BannerManager.BANNER_RESOURCE_PATH;

/**
 * Controls the output of the banner of Tamaya.
 *
 * <p>This class controls if and how the banner of Tamaya is presented the user.
 * The banner is provided by the Tamaya Core under the resource path
 * {@value BANNER_RESOURCE_PATH}.</p>
 *
 * <p>The behavior of the banner manager can be controlled by
 * specifying the configuration key {@code tamaya.banner} with one of
 * the three folowing values:
 *
 * <dl>
 *     <dt>OFF</dt>
 *     <dd>Banner will not be shown</dd>
 *     <dt>CONSOLE</dt>
 *     <dd>The banner will be printed on STDOUT</dd>
 *     <dt>LOGGER</dt>
 *     <dd>The banner will be logged</dd>
 * </dl>
 *
 * In case of any other createValue the banner will not be shown.
 * </p>
 *
 *
 *
 * @see BannerTarget
 */
class BannerManager {
    /**
     * The resouce path to the file containing the banner of Tamaya.
     */
    protected static final String BANNER_RESOURCE_PATH = "/tamaya-banner.txt";

    /**
     * The target for the Tamaya banner output.
     */
    enum BannerTarget {
        OFF, CONSOLE, LOGGER
    }

    private BannerTarget bannerTarget;

    public BannerManager(String value) {
        value = Objects.requireNonNull(value).toUpperCase(Locale.getDefault());

        try {
            bannerTarget = BannerTarget.valueOf(value);
        } catch (NullPointerException | IllegalArgumentException e) {
            bannerTarget = BannerTarget.OFF;
        }
    }

    public void outputBanner() {
        BannerPrinter bp = new SilentBannerPrinter();

        switch (bannerTarget) {
            case CONSOLE:
                bp = new ConsoleBannerPrinter();
                break;
            case LOGGER:
                bp = new LoggingBannerPrinter();
                break;
            case OFF:
            default:
                break;
        }

        bp.outputBanner();
    }
}

/**
 * An abstract Tamaya banner printer.
 */
abstract class AbstractBannerPrinter implements BannerPrinter {
    private static final Logger LOG = Logger.getLogger(AbstractBannerPrinter.class.getName());

    @Override
    public void outputBanner() {
        try (InputStream in = Configuration.class.getResourceAsStream(BANNER_RESOURCE_PATH)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;

            while ((line = reader.readLine()) != null) {
                outputSingleLine(line);
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to output the banner of tamaya.", e);
        }
    }

    abstract void outputSingleLine(String line);
}


/**
 * Outputs the Tamaya banner to an implementation specific output channel
 * as STDOUT or the logging system.
 */
interface BannerPrinter {
    /**
     * Outputs the banner to the output channel
     * used by the implementation.
     */
    void outputBanner();
}

/**
 * Silent implementation of a {@link BannerPrinter}.
 */
class SilentBannerPrinter implements BannerPrinter {
    @Override
    public void outputBanner() {
    }
}

/**
 * Logs the banner via JUL at level {@link java.util.logging.Level#INFO}.
 */
class LoggingBannerPrinter extends AbstractBannerPrinter {
    private static final Logger LOG = Logger.getLogger(LoggingBannerPrinter.class.getName());

    @Override
    void outputSingleLine(String line) {
        LOG.log(Level.INFO, line);
    }
}

/**
 * Prints the banner to the console.
 */
class ConsoleBannerPrinter extends AbstractBannerPrinter {
    @Override
    void outputSingleLine(String line) {
        System.out.println(line);
    }
}


