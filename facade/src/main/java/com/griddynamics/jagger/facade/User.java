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

import org.apache.log4j.xml.DOMConfigurator;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * User: dkotlyarov
 */
public class User {
    private static final Logger log;
    private static final Config config;
    private static final LinkedHashMap<String, User> users = new LinkedHashMap<String, User>();

    private final String id;
    final LinkedHashMap<String, Context> contexts = new LinkedHashMap<String, Context>();

    public User(String id) {
        log.info("BEGIN: User initialization: {}", id);

        this.id = id;

        for (Config.Location location : config.getLocations()) {
            try {
                new Context(this, location);
            } catch(Exception e) {
                log.error("EXCEPTION: Spring context initialization: {}", location, e);
            }
        }

        log.info("END: User initialization: {}", id);
    }

    public String getId() {
        return id;
    }

    public Context getContext(String name) {
        return contexts.get(name);
    }

    public Collection<Context> getContexts() {
        return contexts.values();
    }

    static {
        try {
            // init log4j
            URL url = Context.class.getResource("log4j.xml");
            DOMConfigurator.configure(url);
            log = LoggerFactory.getLogger(Context.class);

            // init spring contexts for jaggers described in jvm parameter "chassis.facade.config" or
            // in file config.xml
            URL configURL = new URL(System.getProperty("chassis.facade.config", Context.class.getResource("config.xml").toString()));
            config = new Persister().read(Config.class, configURL.openStream());

            String directory = config.getContentDirectory() + "report/";
            deleteDirectory(new File(directory));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Config getConfig() {
        return config;
    }

    public static synchronized User getUser(String id) {
        User user = users.get(id);
        if (user == null) {
            user = new User(id);
            users.put(id, user);
        }
        return user;
    }

    public static void deleteDirectory(File directoryFile) {
        if (directoryFile.exists()) {
            File[] files = directoryFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
            directoryFile.delete();
        }
    }

    public static final class Activator implements ServletContextListener {
        public Activator() {
            User.log.info("Facade is initialized");
        }

        @Override
        public void contextInitialized(ServletContextEvent servletContextEvent) {
        }

        @Override
        public void contextDestroyed(ServletContextEvent servletContextEvent) {
        }
    }
}
