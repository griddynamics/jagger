package com.griddynamics.jagger.test.jaas.util;

import com.griddynamics.jagger.util.JaggerXmlApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class JaggerPropertiesProvider implements ApplicationContextAware {

    private ApplicationContext context;

    public String getPropertyValue(String key) {
        return ((JaggerXmlApplicationContext) context).getEnvironmentProperties().getProperty(key);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}