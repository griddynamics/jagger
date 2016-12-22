/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
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

import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;
import com.griddynamics.jagger.kernel.Kernel;
import com.griddynamics.jagger.launch.LaunchManager;
import com.griddynamics.jagger.launch.LaunchTask;
import com.griddynamics.jagger.launch.Launches;
import com.griddynamics.jagger.master.JaasExecApiClient;
import com.griddynamics.jagger.master.Master;
import com.griddynamics.jagger.master.JaasEnvApiClient;
import com.griddynamics.jagger.master.TerminateException;
import com.griddynamics.jagger.reporting.ReportingService;
import com.griddynamics.jagger.storage.StorageServerLauncher;
import com.griddynamics.jagger.storage.rdb.H2DatabaseServer;
import com.griddynamics.jagger.util.JaggerXmlApplicationContext;
import com.griddynamics.jagger.util.generators.ConfigurationGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.google.common.collect.Sets;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public final class JaggerLauncher {
    public static final String ROLES = "chassis.roles";
    public static final String MASTER_CONFIGURATION = "chassis.master.configuration";
    public static final String REPORTER_CONFIGURATION = "chassis.reporter.configuration";
    public static final String KERNEL_CONFIGURATION = "chassis.kernel.configuration";
    public static final String COORDINATION_CONFIGURATION = "chassis.coordination.configuration";
    public static final String COORDINATION_HTTP_CONFIGURATION = "chassis.coordination.http.configuration";
    public static final String RDB_CONFIGURATION = "chassis.rdb.configuration";
    public static final String INCLUDE_SUFFIX = ".include";
    public static final String EXCLUDE_SUFFIX = ".exclude";
    public static final String LOAD_SCENARIO_ID_PROP = "jagger.load.scenario.id.to.execute";
    public static final String DEFAULT_ENVIRONMENT_PROPERTIES = "jagger.default.environment.properties";
    public static final String USER_ENVIRONMENT_PROPERTIES = "jagger.user.environment.properties";
    public static final String ENVIRONMENT_PROPERTIES = "jagger.environment.properties";
    public static final String USER_CONFIGS_PACKAGE = "chassis.master.package.to.scan";
    private static final Logger log = LoggerFactory.getLogger(JaggerLauncher.class);
    private static final String DEFAULT_ENVIRONMENT_PROPERTIES_LOCATION =
            "./configuration/basic/default.environment.properties";
    private static final String DEFAULT_USER_ENVIRONMENT_PROPERTIES_LOCATION =
            "./configuration/basic/default.user.properties";
    
    private static final Properties environmentProperties = new Properties();
    
    private static final Launches.LaunchManagerBuilder builder = Launches.builder();
    
    public static void main(String[] args) throws Exception {
        Thread memoryMonitorThread = new Thread("memory-monitor") {
            @Override
            public void run() {
                for (; ; ) {
                    try {
                        log.info(
                                "Memory info: totalMemory={}, freeMemory={}", Runtime.getRuntime().totalMemory(),
                                Runtime.getRuntime().freeMemory()
                        );
                        
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
    
    private static void initCoordinator(ApplicationContext applicationContext) {
        final Coordinator coordinator = (Coordinator) applicationContext.getBean("coordinator");
        coordinator.waitForReady();
        coordinator.initialize();
    }
    
    private static void launchMaster(final URL directory) {
    
        // pass this property value for Spring's context:component-scan
        System.setProperty(USER_CONFIGS_PACKAGE, environmentProperties.getProperty(USER_CONFIGS_PACKAGE));
        
        LaunchTask masterTask = new LaunchTask() {
            @Override
            public void run() {
                boolean isStandByMode = Boolean.parseBoolean(
                        environmentProperties.getProperty("realtime.enable.standby.mode", "false"));
    
                if (isStandByMode) {
                    log.info("Starting Master in stand by mode...");
        
                    try (JaasEnvApiClient masterToJaasCoordinator = new JaasEnvApiClient(
                            environmentProperties.getProperty("realtime.environment.id"),
                            environmentProperties.getProperty("realtime.jaas.endpoint"), Integer.parseInt(
                            environmentProperties.getProperty("realtime.status.report.interval.seconds")),
                            getAvailableConfigurations(directory)
                    )) {
                        masterToJaasCoordinator.register();
                        while (masterToJaasCoordinator.isStandBy()) {
                            doLoadScenarioExecution(masterToJaasCoordinator.awaitNextExecution(), directory);
                        }
                    } catch (TerminateException | InterruptedException e) {
                        log.error("Master has been terminated.");
                    }
                } else {
                    log.info("Starting Master in right away mode...");
                    doLaunchMaster(directory);
                }
            }
        };
        builder.addMainTask(masterTask);
    }
    
    private static void doLoadScenarioExecution(final JaasEnvApiClient.JaasResponse jaasResponse, final URL directory) {
        JaasExecApiClient execApiClient = null;
        try {
            execApiClient = new JaasExecApiClient(jaasResponse.getExecutionId(),
                                                  environmentProperties.getProperty("realtime.jaas.endpoint")
            );
        
            Optional<TestExecutionEntity> optionalExecution = execApiClient.getExecution();
            if (!optionalExecution.isPresent()) { // didn't manage to acquire an entity
                return;
            }
            TestExecutionEntity execution = optionalExecution.get();
            if (execution.getStatus() != TestExecutionEntity.TestExecutionStatus.PENDING) {
                log.warn("Received execution with id '{}' is not in PENDING state. Going to proceed with next...",
                         execution.getId());
                return;
            }
            execApiClient.startExecution();
            
            String sessionId = null;
            if (execution.getTestProjectURL() == null) { // then execute existing load scenario
                environmentProperties.setProperty(LOAD_SCENARIO_ID_PROP, execution.getLoadScenarioId());
                System.setProperty(USER_CONFIGS_PACKAGE, environmentProperties.getProperty(USER_CONFIGS_PACKAGE));
                sessionId = doLaunchMaster(directory);
            } else { // then execute a load scenario from provided artifact
                sessionId = executeDynamicLoadScenario(execution.getLoadScenarioId(),
                                           execution.getTestProjectURL(),
                                           directory);
            }
            
            execApiClient.completeExecution(sessionId);
        } catch (Exception e) {
            log.error("Error during a load scenario execution", e);
            if (execApiClient != null) {
                execApiClient.failExecution(ExceptionUtils.getMessage(e) + " With root cause message: " + ExceptionUtils
                        .getRootCauseMessage(e));
            }
        }
    }
    
    private static String executeDynamicLoadScenario(String loadScenarioId, String customClassesUrl, URL directory)
            throws IOException {
        
        environmentProperties.setProperty(LOAD_SCENARIO_ID_PROP, loadScenarioId);
    
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            final URL customClasses = new URL(customClassesUrl);
            try (URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{customClasses})) {
                Thread.currentThread().setContextClassLoader(urlClassLoader);
            
                Properties userProps = new Properties();
                String userPropsPath = "profiles/basic/environment.properties";
                URL userPropsUrl = urlClassLoader.findResource(userPropsPath);
                if (userPropsUrl == null) {
                    throw new IOException(String.format(
                            "Provided artifact %s is not properly formatted. There is no %s file inside.",
                            customClassesUrl, userPropsPath
                    ));
                }
                userProps.load(userPropsUrl.openStream());
                // pass this property value for Spring's context:component-scan
                System.setProperty(USER_CONFIGS_PACKAGE, userProps.getProperty(USER_CONFIGS_PACKAGE));
            
                if (StringUtils.isEmpty(loadScenarioId)) {
                    environmentProperties.setProperty(LOAD_SCENARIO_ID_PROP, userProps.getProperty(LOAD_SCENARIO_ID_PROP));
                }
                
                return doLaunchMaster(directory, urlClassLoader);
            } finally {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        } catch (IOException e) {
            log.error("I/O Error during load scenario execution launching.", e);
            throw e;
        }
    }
    
    private static Set<String> getAvailableConfigurations(final URL directory) {
        AbstractXmlApplicationContext context = loadContext(directory, MASTER_CONFIGURATION, environmentProperties);
        ConfigurationGenerator configurationGenerator = context.getBean(ConfigurationGenerator.class);
        Set<String> availableConfigs = new HashSet<>(configurationGenerator.getJaggerLoadScenarioNames());
        context.destroy();
        return availableConfigs;
    }
    
    private static String doLaunchMaster(final URL directory, final ClassLoader classLoader) {
        AbstractXmlApplicationContext context = loadContext(directory, MASTER_CONFIGURATION, environmentProperties, classLoader);
        initCoordinator(context);
        context.getBean(StorageServerLauncher.class); // to trigger lazy initialization
        ConfigurationGenerator configurationGenerator = context.getBean(ConfigurationGenerator.class);
        configurationGenerator.setJLoadScenarioIdToExecute(environmentProperties.getProperty(LOAD_SCENARIO_ID_PROP));
        Master master = context.getBean(Master.class);
        master.run();
        
        String sessionId = master.getSessionIdProvider().getSessionId();
        context.destroy();
        return sessionId;
    }
    
    private static String doLaunchMaster(final URL directory) {
        return doLaunchMaster(directory, JaggerLauncher.class.getClassLoader());
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
                
                Runnable kernelRunner = () -> {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    kernel.run();
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
                
                ApplicationContext context =
                        loadContext(directory, COORDINATION_HTTP_CONFIGURATION, environmentProperties);
                
                initCoordinator(context);
                
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
    
    public static AbstractXmlApplicationContext
    loadContext(URL directory, String role, Properties environmentProperties, ClassLoader classLoader) {
        String[] includePatterns = StringUtils.split(environmentProperties.getProperty(role + INCLUDE_SUFFIX), ", ");
        String[] excludePatterns = StringUtils.split(environmentProperties.getProperty(role + EXCLUDE_SUFFIX), ", ");
        
        List<String> descriptors = discoverResources(directory, includePatterns, excludePatterns);
        log.info("Discovered descriptors:");
        for (String descriptor : descriptors) {
            log.info("   " + descriptor);
        }
        
        return new JaggerXmlApplicationContext(
                directory, environmentProperties, descriptors.toArray(new String[descriptors.size()]), classLoader);
    }
    
    public static AbstractXmlApplicationContext loadContext(URL directory, String role, Properties environmentProperties) {
        return loadContext(directory, role, environmentProperties, JaggerLauncher.class.getClassLoader());
    }
    
    private static List<String> discoverResources(URL directory, String[] includePatterns, String[] excludePatterns) {
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver(new FileSystemResourceLoader());
        List<String> resourceNames = new ArrayList<>();
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
    
    public static void loadBootProperties(URL directory, String environmentPropertiesLocation,
                                          Properties environmentProperties
    ) throws IOException {
        
        // priorities
        // low priority (default properties - environment props - user props - system properties) high priority
        
        // properties from command line - environment properties
        URL bootPropertiesFile = new URL(directory, environmentPropertiesLocation);
        System.setProperty(ENVIRONMENT_PROPERTIES, environmentPropertiesLocation);
        environmentProperties.load(bootPropertiesFile.openStream());
        
        // user properties
        String userBootPropertiesLocationsString = System.getProperty(USER_ENVIRONMENT_PROPERTIES);
        if (userBootPropertiesLocationsString == null) {
            userBootPropertiesLocationsString = environmentProperties.getProperty(USER_ENVIRONMENT_PROPERTIES);
        }
        if (userBootPropertiesLocationsString == null) {
            userBootPropertiesLocationsString = DEFAULT_USER_ENVIRONMENT_PROPERTIES_LOCATION;
        }
        String[] userBootPropertiesSingleLocations = userBootPropertiesLocationsString.split(",");
        
        for (String location : userBootPropertiesSingleLocations) {
            URL userBootPropertiesFile = new URL(directory, location);
            Properties userBootProperties = new Properties();
            userBootProperties.load(userBootPropertiesFile.openStream());
            
            for (String name : userBootProperties.stringPropertyNames()) {
                // overwrite due to higher priority
                environmentProperties.setProperty(name, userBootProperties.getProperty(name));
            }
        }
        
        // default properties
        String defaultBootPropertiesLocation = System.getProperty(DEFAULT_ENVIRONMENT_PROPERTIES);
        if (defaultBootPropertiesLocation == null) {
            defaultBootPropertiesLocation = environmentProperties.getProperty(DEFAULT_ENVIRONMENT_PROPERTIES);
        }
        if (defaultBootPropertiesLocation == null) {
            defaultBootPropertiesLocation = DEFAULT_ENVIRONMENT_PROPERTIES_LOCATION;
        }
        URL defaultBootPropertiesFile = new URL(directory, defaultBootPropertiesLocation);
        Properties defaultBootProperties = new Properties();
        defaultBootProperties.load(defaultBootPropertiesFile.openStream());
        for (String name : defaultBootProperties.stringPropertyNames()) {
            // only append due to low priority
            if (!environmentProperties.containsKey(name)) {
                environmentProperties.setProperty(name, defaultBootProperties.getProperty(name));
            }
        }
        
        Properties properties = System.getProperties();
        for (Enumeration<String> enumeration = (Enumeration<String>) properties.propertyNames();
             enumeration.hasMoreElements(); ) {
            String key = enumeration.nextElement();
            // overwrite due to higher priority
            environmentProperties.put(key, properties.get(key));
        }
        
        System.setProperty(ENVIRONMENT_PROPERTIES, environmentPropertiesLocation);
        System.setProperty(USER_ENVIRONMENT_PROPERTIES, userBootPropertiesLocationsString);
        System.setProperty(DEFAULT_ENVIRONMENT_PROPERTIES, defaultBootPropertiesLocation);
    }
}
