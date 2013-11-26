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

package com.griddynamics.jagger.facade;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.SessionData;
import com.griddynamics.jagger.extension.ExtensionRegistry;
import com.griddynamics.jagger.monitoring.reporting.AbstractMonitoringReportProvider;
import com.griddynamics.jagger.monitoring.reporting.MonitoringReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * User: dkotlyarov
 */
public final class Context {
    private static final Logger log = LoggerFactory.getLogger(Context.class);

    private final User user;
    private final Config.Location location;
    private final ApplicationContext applicationContext;
    private final HashMap<String, Report> reports = new HashMap<String, Report>();
    private Process process = null;
    private String processId = null;
    private LinkedList<String> processFullLog = null;
    private LinkedList<String> processLog = null;

    Context(User user, Config.Location location) throws IOException {
        log.info("BEGIN: Spring context initialization: {}", location);

        this.user = user;
        this.location = location;

        Properties environmentProperties = new Properties();
        URL directory = new URL(location.getDirectory());
        JaggerLauncher.loadBootProperties(directory, "profiles/ci-reporter/environment-reporter.properties", environmentProperties);
        this.applicationContext = JaggerLauncher.loadContext(directory, JaggerLauncher.REPORTER_CONFIGURATION, environmentProperties);

        if (user.contexts.put(location.getName(), this) != null) {
            throw new RuntimeException(String.format("Location with name %s already exists", location.getName()));
        }

        log.info("END: Spring context initialization: {}", location);
    }

    public User getUser() {
        return user;
    }

    public Config.Location getLocation() {
        return location;
    }

    ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public synchronized int getSessionCount() {
        ExtensionRegistry reporterExtensionRegistry = applicationContext.getBean("reporterExtensionRegistry", ExtensionRegistry.class);
        MonitoringReporter monitoringReporter = (MonitoringReporter) reporterExtensionRegistry.getExtension("monitoringReporter");
        long count = (Long) monitoringReporter.getHibernateTemplate().find("select count(sd.id) from SessionData sd").get(0);
        return (int) count;
    }

    public synchronized List<SessionData> getSessions() {
        ExtensionRegistry reporterExtensionRegistry = applicationContext.getBean("reporterExtensionRegistry", ExtensionRegistry.class);
        MonitoringReporter monitoringReporter = (MonitoringReporter) reporterExtensionRegistry.getExtension("monitoringReporter");
        return monitoringReporter.getHibernateTemplate().find("from SessionData sd order by sd.id desc");
    }

    public synchronized Report getReport(String sessionId) {
        Report report = reports.get(sessionId);
        if (report == null) {
            ExtensionRegistry reporterExtensionRegistry = applicationContext.getBean("reporterExtensionRegistry", ExtensionRegistry.class);
            MonitoringReporter monitoringReporter = (MonitoringReporter) reporterExtensionRegistry.getExtension("monitoringReporter");

            ExtensionRegistry mappedReporterExtensionRegistry = applicationContext.getBean("mappedReporterExtensionRegistry", ExtensionRegistry.class);
            ((AbstractMonitoringReportProvider) mappedReporterExtensionRegistry.getExtension("sysUTPlots")).clearCache();
            ((AbstractMonitoringReportProvider) mappedReporterExtensionRegistry.getExtension("profiler")).clearCache();

            report = new Report(this, (SessionData) monitoringReporter.getHibernateTemplate().find("from SessionData sd where sd.sessionId = ?", sessionId).get(0));
            reports.put(sessionId, report);
        }
        return report;
    }

    public synchronized void startSession() {
        try {
            String dir = location.getDirectory();
            dir = dir.substring(dir.indexOf("/"));
            File directoryFile = new File(dir);
            String cmd = location.getStartSession().getCmd();
            process = Runtime.getRuntime().exec(cmd, null, directoryFile);
            processFullLog = new LinkedList<String>();
            processLog = new LinkedList<String>();
            new Thread(String.format("Log processing for %s", location.getName())) {
                @Override
                public void run() {
                    try {
                        BufferedInputStream input = new BufferedInputStream(process.getInputStream());
                        Scanner scanner = new Scanner(input);

                        boolean f;
                        LinkedList<String> processFullLog;
                        LinkedList<String> processLog;
                        synchronized (Context.this) {
                            f = process != null;
                            processFullLog = Context.this.processFullLog;
                            processLog = Context.this.processLog;
                        }
                        while (f) {
                            synchronized (Context.this) {
                                if (processFullLog.size() >= 10000) {
                                    processFullLog.removeFirst();
                                }
                                if (processLog.size() >= 10000) {
                                    processLog.removeFirst();
                                }
                            }

                            String line = scanner.nextLine();
                            if (line.startsWith("PID:")) {
                                String[] items = line.split(":");
                                synchronized (Context.this) {
                                    processId = items[1].split("@")[0];
                                }
                            }

                            synchronized (Context.this) {
                                processFullLog.addLast(line);
                                processLog.addLast(line);
                                f = process != null;
                            }
                        }
                    } catch (Exception e) {
                        log.error("Log processing for {} is terminated with exception", location.getName(), e);
                    } finally {
                        log.info("Log processing for {} is terminated", location.getName());
                    }
                }
            }.start();
            new Thread(String.format("Error processing for {}", location.getName())) {
                @Override
                public void run() {
                    try {
                        BufferedInputStream error = new BufferedInputStream(process.getErrorStream());

                        boolean f;
                        synchronized (Context.this) {
                            f = process != null;
                        }
                        while (f) {
                            error.read();
                            synchronized (Context.this) {
                                f = process != null;
                            }
                        }
                    } catch (Exception e) {
                        log.error("Log processing for {} is terminated with exception", location.getName(), e);
                    } finally {
                        log.info("Log processing for {} is terminated", location.getName());
                    }
                }
            }.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopSession(boolean kill) {
        Process process;
        String processId;
        synchronized(this) {
            process = this.process;
            processId = this.processId;
            if ((process == null) || (processId == null)) {
                return;
            }
        }
        try {
            if (!kill) {
                Runtime.getRuntime().exec(String.format("kill %s", processId));
            } else {
                Runtime.getRuntime().exec(String.format("kill -9 %s", processId));
            }
            process.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        synchronized(this) {
            this.process = null;
            this.processId = null;
            this.processFullLog = null;
            this.processLog = null;
        }
    }

    public synchronized boolean isSessionStarted() {
        return process != null;
    }

    public synchronized ArrayList<String> getFullSessionLog() {
        if (processFullLog != null) {
            ArrayList<String> log = new ArrayList<String>(processFullLog);
            processLog.clear();
            return log;
        } else {
            return new ArrayList<String>();
        }
    }

    public synchronized ArrayList<String> getSessionLog() {
        if (processLog != null) {
            ArrayList<String> log = new ArrayList<String>(processLog);
            processLog.clear();
            return log;
        } else {
            return new ArrayList<String>();
        }
    }
}
