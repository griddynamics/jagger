package com.griddynamics.jagger.util.generators;

import org.junit.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Andrey Badaev (andrey.badaev@kohls.com)
 * Date: 10/11/16
 */
public class ConfigurationGeneratorTest {
    
    
    @org.junit.Test
    public void setUserJTestSuites() throws Exception {
    
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/example.test.suite.conf.xml");
        ConfigurationGenerator configurationGenerator = context.getBean(ConfigurationGenerator.class);
    
        Assert.assertTrue(configurationGenerator.getUserJTestSuiteNames().size() == 1);
    }
}