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
package org.apache.tamaya.integration.osgi.test;

import org.apache.log4j.Priority;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.integration.osgi.Activator;
import org.apache.tamaya.integration.osgi.OSGIConfigRootMapper;
import org.apache.tamaya.integration.osgi.TamayaConfigAdminImpl;
import org.apache.tamaya.integration.osgi.TamayaConfigurationImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.osgi.metadata.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
@Ignore
public class TestConfigIntegration {

    private static final String TAMAYA_VERSION = "0.3-incubating-SNAPSHOT";
    private static final String DEFAULT = "default";

    static {
        System.setProperty("[bundle:tamaya]systemTestKey", "foo");
        System.setProperty("org.osgi.framework.bootdelegation",
                "org.apache.tamaya,org.apache.tamaya.integration.osgi,org.apache.tamaya.integration.osgi.test");
    }

    @ArquillianResource
    BundleContext context;

    //////////////////////////////////////////////////////// Test setup //////////////////////////////////

    @Deployment(name = "felix.main", order = 1)
    public static JavaArchive deployMain() {
        return ShrinkWrap.create(ZipImporter.class, "felix.main-5.4.0.jar")
                .importFrom(getBundleFile("org.apache.felix:org.apache.felix.main:5.4.0"))
                .as(JavaArchive.class);
    }

    @Deployment(name = "osgi.config", order = 1)
    public static JavaArchive deployOSGIConfig() {
        return ShrinkWrap.create(ZipImporter.class, "felix.configadmin-1.8.8.jar")
                .importFrom(getBundleFile("org.apache.felix:org.apache.felix.configadmin:1.8.8"))
                .as(JavaArchive.class);
    }

    @Deployment(name = "javax.annotation", order = 4)
    public static JavaArchive deployJavaxAnnotation() {
        return ShrinkWrap.create(ZipImporter.class, "javax.annotation.jar")
                .importFrom(getBundleFile("org.apache.geronimo.specs:geronimo-annotation_1.2_spec:1.0-alpha-1"))
                .as(JavaArchive.class);
    }

    @Deployment(name = "tamaya-api", order = 5)
    public static JavaArchive deployTamayaAPI() {
        return ShrinkWrap.create(ZipImporter.class, "tamaya-api.jar")
                .importFrom(getBundleFile("org.apache.tamaya:tamaya-api:" + TAMAYA_VERSION))
                .as(JavaArchive.class);
    }

    @Deployment(name = "tamaya-core", order = 6)
    public static JavaArchive deployTamayaCore() {
        return ShrinkWrap.create(ZipImporter.class, "tamaya-core.jar")
                .importFrom(getBundleFile("org.apache.tamaya:tamaya-core:" + TAMAYA_VERSION))
                .as(JavaArchive.class);
    }

    @Deployment(name = "tamaya-functions", order = 7)
    public static JavaArchive deployTamayaFunctions() {
        return ShrinkWrap.create(ZipImporter.class, "tamaya-functions.jar")
                .importFrom(getBundleFile("org.apache.tamaya.ext:tamaya-functions:" + TAMAYA_VERSION))
                .as(JavaArchive.class);
    }

    @Deployment(name = "java.atInject", order = 8)
    public static JavaArchive deployAtInject() {
        return ShrinkWrap.create(ZipImporter.class, "java.atinject.jar")
                .importFrom(getBundleFile("org.apache.geronimo.specs:geronimo-atinject_1.0_spec:1.0"))
                .as(JavaArchive.class);
    }

    @Deployment(name = "javax.el", order = 9)
    public static JavaArchive deployEL() {
        return ShrinkWrap.create(ZipImporter.class, "javax.el.jar")
                .importFrom(getBundleFile("org.apache.geronimo.specs:geronimo-el_2.2_spec:1.0.4"))
                .as(JavaArchive.class);
    }

    @Deployment(name = "javax.interceptor", order = 9)
    public static JavaArchive deployInterceptor() {
        return ShrinkWrap.create(ZipImporter.class, "javax.interceptor.jar")
                .importFrom(getBundleFile("org.apache.geronimo.specs:geronimo-interceptor_1.1_spec:1.0"))
                .as(JavaArchive.class);
    }

    @Deployment(name = "javax.inject", order = 10)
    public static JavaArchive deployCDISpec() {
        return ShrinkWrap.create(ZipImporter.class, "javax.inject.jar")
                .importFrom(getBundleFile("org.apache.geronimo.specs:geronimo-jcdi_1.1_spec:1.0"))
                .as(JavaArchive.class);
    }

    @Deployment(name = "tamaya-injection-api", order = 11)
    public static JavaArchive deployTamayaInjectionAPI() {
        return ShrinkWrap.create(ZipImporter.class, "tamaya-injection-api.jar")
                .importFrom(getBundleFile("org.apache.tamaya.ext:tamaya-injection-api:" + TAMAYA_VERSION))
                .as(JavaArchive.class);
    }

    @Deployment(name = "tamaya-events", order = 14)
    public static JavaArchive deployTamayaEvents() {
        return ShrinkWrap.create(ZipImporter.class, "tamaya-events.jar")
                .importFrom(getBundleFile("org.apache.tamaya.ext:tamaya-events:" + TAMAYA_VERSION))
                .as(JavaArchive.class);
    }

    @Deployment(name = "tamaya-injection", order = 6)
    public static JavaArchive deployTamayaInjectionSE() {
        return ShrinkWrap.create(ZipImporter.class, "tamaya-injection.jar")
                .importFrom(getBundleFile("org.apache.tamaya.ext:tamaya-injection:" + TAMAYA_VERSION))
                .as(JavaArchive.class);
    }

    @Deployment(name = DEFAULT, order = 100)
    public static JavaArchive createdeployment() {
        final String archiveName = "tamaya-osgi-general.jar";
        URL config = ClassLoader.getSystemClassLoader().getResource("META-INF/javaconfiguration.properties");
        return ShrinkWrap.create(JavaArchive.class, archiveName)
                .setManifest(new Asset() {
                    public InputStream openStream() {
                        return OSGiManifestBuilder.newInstance()
                                .addBundleSymbolicName(archiveName)
                                .addBundleManifestVersion(2)
                                .addExportPackages("org.apache.tamaya.integration.osgi",
                                        "org.apache.tamaya.integration.osgi.test")
                                .addImportPackages("org.junit", "org.osgi.service.cm",
                                        "org.apache.tamaya", "org.apache.tamaya.functions","org.apache.tamaya.inject",
                                        "org.apache.tamaya.spi","org.apache.tamaya.spi", "org.osgi.framework",
                                        "org.osgi.util.tracker")
                                .addBundleActivator(Activator.class)
                                .openStream();
                    }
                })
                .addClasses(Test.class, TestConfigIntegration.class, Priority.class, Activator.class,
                        TamayaConfigAdminImpl.class, TamayaConfigurationImpl.class, OSGIConfigRootMapper.class,
                        HelloService.class)
                .addAsResource(config, "META-INF/javaconfiguration.properties");
    }

    @Before
    public void startBundles() {
        for (Bundle bundle : context.getBundles()) {
            try {
                bundle.start();
            } catch (BundleException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    @OperateOnDeployment(DEFAULT)
    public void testTamayaAvailable() throws Exception {
        assertNotNull(ConfigurationProvider.getConfiguration());
    }

    @Test
    @OperateOnDeployment(DEFAULT)
    public void testTamayaConfigAdminAvailable() throws Exception {
        ServiceReference<ConfigurationAdmin> ref = context.getServiceReference(ConfigurationAdmin.class);
        assertNotNull("OSGI ConfigAdmin not loaded.", ref);
        ConfigurationAdmin osgiConfig = context.getService(ref);
        assertNotNull("No config available from Tamaya through OSGI ConfigAdmin.", osgiConfig);
        assertEquals("Override of OSGI ConfigAdmin with Tamaya did not work.", osgiConfig.getClass().getName(), TamayaConfigAdminImpl.class.getName());
    }

    @Test
    @OperateOnDeployment(DEFAULT)
    public void testLoadTamayaConfigFromConfigAdmin() throws Exception {
        ServiceReference<ConfigurationAdmin> ref = context.getServiceReference(ConfigurationAdmin.class);
        assertNotNull("OSGI ConfigAdmin not loaded.", ref);
        ConfigurationAdmin admin = context.getService(ref);
        Configuration osgiConfig = admin.getConfiguration("tamaya");
        assertNotNull("No config available from Tamaya through OSGI ConfigAdmin.", osgiConfig);
        Dictionary<String, Object> config = osgiConfig.getProperties();
        assertNotNull("No config entries loaded from Tamaya.", config);
        assertEquals("Property 'testKey' not loaded from Tamaya.", "foo", config.get("systemTestKey"));
        // TODO: Think on Resource loading in OSGI:
        // this should work with normal resource loading but does not work with OSGI, since this resource is not
        // visible by default, when we have added resource loading as abstraction to the ServiceContext, too.
        // Reason: Resource Loading in OSGI works differently!
//        assertEquals("Property 'testKey' not loaded from Tamaya.", "success!", config.get("my.testProperty"));
    }

    @Test
    @OperateOnDeployment(DEFAULT)
    public void testInjection() throws Exception {
        Dictionary<String, Object> config = new Hashtable<>();
        ServiceRegistration<HelloService> reg = context.registerService(HelloService.class, new HelloService(), config);
        ServiceReference<HelloService> serviceReference = reg.getReference();
        assertNotNull(serviceReference);
        HelloService helloServ = context.getService(serviceReference);
        assertNotNull("HelloService not referenceable.", helloServ);
        assertEquals("A Tamaya default.", helloServ.sayHello());
    }

    private static File getBundleFile(String artifactId) {
        return Maven.configureResolver()
                .withMavenCentralRepo(true)
                .withClassPathResolution(true)
                .resolve(artifactId).withoutTransitivity().asSingleFile();
    }
}