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

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.PrintStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;

public class BannerManagerTest {

    @Test
    public void valueConsoleSendsBannerToSystemOut() {

        SecurityManager sm = new SecurityManager();
        AccessControlContext con = AccessController.getContext();

        Permission p = new RuntimePermission("setIO");

        /*
         * Here we check the precondition for this unit test
         * and the correct setup of the test enviroment
         * The JVM must have been started with
         * -Djava.security.policy=<path_to_core_module</src/test/resources/java-security.policy
         */
        sm.checkPermission(p, con);

        PrintStream standard = System.out;
        PrintStream printStream = Mockito.mock(PrintStream.class);

        System.setOut(printStream);
        standard.println("Changed stream for STDOUT successfully");

        try {
            BannerManager bm = new BannerManager("console");
            bm.outputBanner();

        } finally {
            System.setOut(standard);
        }

        Mockito.verify(printStream, Mockito.atLeastOnce()).println(Mockito.anyString());
    }

    @Test
    public void invalidValueAvoidsLoggingToConsonle() {

        PrintStream standard = System.out;
        PrintStream printStream = Mockito.mock(PrintStream.class);

        System.setOut(printStream);

        try {
            BannerManager bm = new BannerManager("snafu");
            bm.outputBanner();

        } finally {
            System.setOut(standard);
        }

        Mockito.verify(printStream, Mockito.never()).println(Mockito.anyString());
    }

}
