package com.griddynamics.jagger.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This class is needed to provide Jagger environment properties.
 * It must be injected to class where properties are needed, or this class can extend JaggerPropertiesProvider.
 * In both cases that class must be a valid spring bean.
 */
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
