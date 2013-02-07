package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.configuration.Configuration;
import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/14/12
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */

/*
*   Launch only as maven test
*/
public class JaggerConfigurationTest {

    private ApplicationContext ctx;

    @BeforeClass
    public void testInit() throws Exception{
        URL directory = new URL("file:" + "../configuration/");
        Properties environmentProperties = new Properties();
        JaggerLauncher.loadBootProperties(directory, "profiles/local/environment.properties", environmentProperties);
        environmentProperties.put("chassis.master.configuration.include",environmentProperties.get("chassis.master.configuration.include")+", ../spring.schema/src/test/resources/example-test-configuration.xml1");
        ctx = JaggerLauncher.loadContext(directory,"chassis.master.configuration",environmentProperties);
    }

    @Test
    public void conf1Test(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        Assert.assertNotNull(config1);
    }


    @Test
    public void outerTestPlanTest(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        Assert.assertEquals(config1.getTasks().size(), 1);
        checkListOnNull(config1.getTasks());
    }


    @Test
    public void sesListTest1(){
        Configuration config2 = (Configuration) ctx.getBean("config1");
        Assert.assertEquals(config2.getSessionExecutionListeners().size(), 2);
        checkListOnNull(config2.getSessionExecutionListeners());
    }


    @Test
    public void taskListTest1(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        Assert.assertEquals(config1.getDistributionListeners().size(), 6);
        checkListOnNull(config1.getDistributionListeners());
    }


    @Test
    public void workLoadTest1(){
        WorkloadTask task1 = (WorkloadTask) ctx.getBean("w1");
        Assert.assertNotNull(task1);
    }

    private void checkListOnNull(List list){
        for (Object o : list){
            Assert.assertNotNull(o);
        }
    }
}
