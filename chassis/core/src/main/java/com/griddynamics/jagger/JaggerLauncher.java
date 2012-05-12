/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger;

import com.google.common.collect.Sets;
import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.kernel.Kernel;
import com.griddynamics.jagger.launch.LaunchManager;
import com.griddynamics.jagger.launch.LaunchTask;
import com.griddynamics.jagger.launch.Launches;
import com.griddynamics.jagger.master.Master;
import com.griddynamics.jagger.reporting.ReportingService;
import com.griddynamics.jagger.storage.rdb.H2DatabaseServer;
import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.util.JaggerXmlApplicationContext;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Server;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public final class JaggerLauncher {
    private static final Logger log = LoggerFactory.getLogger(JaggerLauncher.class);

    public static final String ROLES = "chassis.roles";
    public static final String MASTER_CONFIGURATION = "chassis.master.configuration";
    public static final String REPORTER_CONFIGURATION = "chassis.reporter.configuration";
    public static final String KERNEL_CONFIGURATION = "chassis.kernel.configuration";
    public static final String COORDINATION_CONFIGURATION = "chassis.coordination.configuration";
    public static final String COORDINATION_HTTP_CONFIGURATION = "chassis.coordination.http.configuration";
    public static final String RDB_CONFIGURATION = "chassis.rdb.configuration";
    public static final String INCLUDE_SUFFIX = ".include";
    public static final String EXCLUDE_SUFFIX = ".exclude";

    public static final String DEFAULT_ENVIRONMENT_PROPERTIES = "jagger.default.environment.properties";
    public static final String ENVIRONMENT_PROPERTIES = "jagger.environment.properties";

    private static final Properties environmentProperties = new Properties();

    private static final Launches.LaunchManagerBuilder builder = Launches.builder();

    public static void main(String[] args) throws Exception {
        Thread memoryMonitorThread = new Thread("memory-monitor") {
            @Override
            public void run() {
                for (;;) {
                    try {
                        log.info("Memory info: totalMemory={}, freeMemory={}", Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory());

                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        memoryMonitorThread.setDaemon(true);
        memoryMonitorThread.start();

        String pid = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println(String.format("PID:%s", pid));

        Properties props = System.getProperties();
        for (Map.Entry<Object, Object> prop : props.entrySet()) {
            log.info("{}: '{}'", prop.getKey(), prop.getValue());
        }
        log.info("");

        URL directory = new URL("file:" + System.getProperty("user.dir") + "/");
        loadBootProperties(directory, args[0], environmentProperties);

        log.debug("Bootstrap properties:");
        for (String propName : environmentProperties.stringPropertyNames()) {
            log.debug("   {}={}", propName, environmentProperties.getProperty(propName));
        }

        String[] roles = environmentProperties.getProperty(ROLES).split(",");
        Set<String> rolesSet = Sets.newHashSet(roles);

        if (rolesSet.contains(Role.COORDINATION_SERVER.toString())) {
            launchCoordinationServer(directory);
        }
        if (rolesSet.contains(Role.HTTP_COORDINATION_SERVER.toString())) {
            launchCometdCoordinationServer(directory);
        }
        if (rolesSet.contains(Role.RDB_SERVER.toString())) {
            launchRdbServer(directory);
        }
        if (rolesSet.contains(Role.MASTER.toString())) {
            launchMaster(directory);
        }
        if (rolesSet.contains(Role.KERNEL.toString())) {
            launchKernel(directory);
        }

        if (rolesSet.contains(Role.REPORTER.toString())) {
            launchReporter(directory);
        }

        LaunchManager launchManager = builder.build();

        int result = launchManager.launch();

        System.exit(result);

    }

    private static void launchMaster(final URL directory) {
        LaunchTask masterTask = new LaunchTask() {
            @Override
            public void run() {
                log.info("Starting Master");
                ApplicationContext context = loadContext(directory, MASTER_CONFIGURATION, environmentProperties);

                final Coordinator coordinator = (Coordinator) context.getBean("coordinator");
                coordinator.waitForReady();
                coordinator.initialize();
                Master master = (Master) context.getBean("master");
                master.run();
            }
        };

        builder.addMainTask(masterTask);
    }

    private static void launchReporter(final URL directory) {
        LaunchTask launchReporter = new LaunchTask() {

            @Override
            public void run() {
                ApplicationContext context = loadContext(directory, REPORTER_CONFIGURATION, environmentProperties);
                final ReportingService reportingService = (ReportingService) context.getBean("reportingService");

                reportingService.renderReport(true);
            }
        };

        builder.addMainTask(launchReporter);
    }

    private static void launchKernel(final URL directory) {

        LaunchTask runKernel = new LaunchTask() {
            private Kernel kernel;

            @Override
            public void run() {
                log.info("Starting Kernel");


                ApplicationContext context = loadContext(directory, KERNEL_CONFIGURATION, environmentProperties);

                final CountDownLatch latch = new CountDownLatch(1);
                final Coordinator coordinator = (Coordinator) context.getBean("coordinator");

                kernel = (Kernel) context.getBean("kernel");

                toTerminate(kernel);

                Runnable kernelRunner = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        kernel.run();
                    }
                };

                getExecutor().execute(kernelRunner);

                coordinator.waitForReady();
                coordinator.waitForInitialization();

                latch.countDown();
            }
        };

        builder.addBackgroundTask(runKernel);
    }

    private static void launchRdbServer(final URL directory) {
        log.info("Starting RDB Server");


        LaunchTask rdbRunner = new LaunchTask() {
            @Override
            public void run() {
                ApplicationContext context = loadContext(directory, RDB_CONFIGURATION, environmentProperties);

                H2DatabaseServer dbServer = (H2DatabaseServer) context.getBean("databaseServer");

                dbServer.run();
            }
        };

        builder.addBackgroundTask(rdbRunner);
    }

    private static void launchCoordinationServer(final URL directory) {
        LaunchTask zookeeperInitializer = new LaunchTask() {

            //            private ZooKeeperServer zooKeeper;
            private AttendantServer server;

            public void run() {
                log.info("Starting Coordination Server");

                ApplicationContext context = loadContext(directory, COORDINATION_CONFIGURATION, environmentProperties);
                server = (AttendantServer) context.getBean("coordinatorServer");
                toTerminate(server);
                getExecutor().execute(server);
                server.initialize();
            }

        };

        builder.addMainTask(zookeeperInitializer);
    }

    private static void launchCometdCoordinationServer(final URL directory) {

        LaunchTask jettyRunner = new LaunchTask() {
            public void run() {
                log.info("Starting Cometd Coordination Server");

                ApplicationContext context = loadContext(directory, COORDINATION_HTTP_CONFIGURATION, environmentProperties);

                Server jettyServer = (Server) context.getBean("jettyServer");
                try {
                    jettyServer.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        builder.addMainTask(jettyRunner);
    }

    public static ApplicationContext loadContext(URL directory, String role, Properties environmentProperties) {
        String[] includePatterns = StringUtils.split(environmentProperties.getProperty(role + INCLUDE_SUFFIX), ", ");
        String[] excludePatterns = StringUtils.split(environmentProperties.getProperty(role + EXCLUDE_SUFFIX), ", ");

        List<String> descriptors = discoverResources(directory, includePatterns, excludePatterns);

        log.info("Discovered descriptors:");
        for (String descriptor : descriptors) {
            log.info("   " + descriptor);
        }

        return new JaggerXmlApplicationContext(directory, environmentProperties, descriptors.toArray(new String[descriptors.size()]));
    }

    private static List<String> discoverResources(URL directory, String[] includePatterns, String[] excludePatterns) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(new FileSystemResourceLoader());
        List<String> resourceNames = new ArrayList<String>();
        PathMatcher matcher = new AntPathMatcher();
        try {
            for (String pattern : includePatterns) {
                Resource[] includeResources = resolver.getResources(directory.toString() + pattern);
                for (Resource resource : includeResources) {
                    boolean isValid = true;
                    for (String excludePattern : excludePatterns) {
                        if (matcher.match(excludePattern, resource.getFilename())) {
                            isValid = false;
                            break;
                        }
                    }
                    if (isValid) {
                        resourceNames.add(resource.getURI().toString());
                    }
                }
            }
        } catch (IOException e) {
            throw new TechnicalException(e);
        }

        return resourceNames;
    }

    public static void loadBootProperties(URL directory, String environmentPropertiesLocation, Properties environmentProperties) throws IOException {
        URL bootPropertiesFile = new URL(directory, environmentPropertiesLocation);
        System.setProperty(ENVIRONMENT_PROPERTIES, environmentPropertiesLocation);
        environmentProperties.load(bootPropertiesFile.openStream());

        String defaultBootPropertiesLocation = environmentProperties.getProperty(DEFAULT_ENVIRONMENT_PROPERTIES);
        if (defaultBootPropertiesLocation != null) {
            URL defaultBootPropertiesFile = new URL(directory, defaultBootPropertiesLocation);
            Properties defaultBootProperties = new Properties();
            defaultBootProperties.load(defaultBootPropertiesFile.openStream());
            for (String name : defaultBootProperties.stringPropertyNames()) {
                if (!environmentProperties.containsKey(name)) {
                    environmentProperties.setProperty(name, defaultBootProperties.getProperty(name));
                }
            }
        }

        Properties properties = System.getProperties();
        for (Enumeration<String> enumeration = (Enumeration<String>) properties.propertyNames(); enumeration.hasMoreElements(); ) {
            String key = enumeration.nextElement();
            environmentProperties.put(key, properties.get(key));
        }

        System.setProperty(ENVIRONMENT_PROPERTIES, environmentPropertiesLocation);
        System.setProperty(DEFAULT_ENVIRONMENT_PROPERTIES, defaultBootPropertiesLocation);
    }
}
