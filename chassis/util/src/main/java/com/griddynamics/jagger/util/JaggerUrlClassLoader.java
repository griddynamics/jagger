package com.griddynamics.jagger.util;

import java.net.URL;
import java.net.URLClassLoader;

public class JaggerUrlClassLoader extends URLClassLoader {

    public JaggerUrlClassLoader(URL[] urls) {
        super(urls);
    }

    @Override
    public URL getResource(String name) {
        URL resource = findResource(name);
        return resource != null ? resource : super.getResource(name);
    }
}
