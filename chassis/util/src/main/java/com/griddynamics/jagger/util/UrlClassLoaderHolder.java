package com.griddynamics.jagger.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * Holds and handles a custom url class loader based on provided classes url.
 * @n
 * Created by Andrey Badaev
 * Date: 26/12/16
 */
public class UrlClassLoaderHolder {
    
    private static final Logger log = LoggerFactory.getLogger(UrlClassLoaderHolder.class);
    
    private volatile URLClassLoader urlClassLoader;
    
    public URLClassLoader get() {
        return urlClassLoader;
    }
    
    public boolean createFor(final String classesUrl) {
        try {
            log.info("Creating a classloader for classes url {} ...", classesUrl);
            final URL customClasses = new URL(classesUrl);
            this.urlClassLoader = new URLClassLoader(new URL[]{customClasses});
            log.info("Classloader for classes url {} successfully created", classesUrl);
            return true;
        } catch (MalformedURLException e) {
            this.urlClassLoader = null;
            log.error("Error during creating a custom class loader for classes url {}", classesUrl, e);
        }
        return false;
    }
    
    public boolean clear() {
        log.info("Closing a classloader with custom url {} ...", Arrays.toString(urlClassLoader.getURLs()));
        if (urlClassLoader != null) {
            try {
                urlClassLoader.close();
                log.info("Classloader with custom url {} has been successfully closed.",
                         Arrays.toString(urlClassLoader.getURLs()));
            } catch (IOException e) {
                log.error("Error during closing a classloader with custom url {}.",
                          Arrays.toString(urlClassLoader.getURLs()),
                          e);
                return false;
            } finally {
                urlClassLoader = null;
            }
        }
    
        return true;
    }
}
