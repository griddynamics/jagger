package com.griddynamics.jagger.test.jaas.util;

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


    public String getPropertyValue(String key) {
        String prop = testEnv.getProperty(key);
        if(prop==null){
            prop = ((JaggerXmlApplicationContext)context).getEnvironmentProperties().getProperty(key);
        }
        return prop;
    }

}