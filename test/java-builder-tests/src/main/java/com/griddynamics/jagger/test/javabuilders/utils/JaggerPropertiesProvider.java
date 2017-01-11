package com.griddynamics.jagger.test.javabuilders.utils;

import com.griddynamics.jagger.util.JaggerXmlApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:test.properties")
public class JaggerPropertiesProvider{

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Environment testEnv;

    public Integer getIntPropertyValue(String key) {
        return Integer.parseInt(getPropertyValue(key));
    }

    public String getPropertyValue(String key) {
        String prop = testEnv.getProperty(key);
        if(prop==null){
            prop = ((JaggerXmlApplicationContext)context).getEnvironmentProperties().getProperty(key);
        }
        return prop;
    }

}