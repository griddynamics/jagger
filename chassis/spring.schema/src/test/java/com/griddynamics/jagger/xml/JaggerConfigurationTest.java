package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.master.configuration.Configuration;
import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/14/12
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class JaggerConfigurationTest {

    private ApplicationContext ctx;

    @BeforeTest
    public void configTest() {
        ctx = new ClassPathXmlApplicationContext("/example-test-configuration.xml1");
    }

    @Test
    public void conf1Test(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        Assert.assertNotNull(config1);
    }

    @Test
    public void conf2Test(){
        Configuration config2 = (Configuration) ctx.getBean("config2");
        Assert.assertNotNull(config2);
    }

    @Test
    public void  innerTestPlanTest(){
        Configuration config2 = (Configuration) ctx.getBean("config2");
        Assert.assertEquals(config2.getTasks().size(), 2);
        checkListOnNull(config2.getTasks());
    }

    @Test
    public void outerTestPlanTest(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        Assert.assertEquals(config1.getTasks().size(), 2);
        checkListOnNull(config1.getTasks());
    }


    @Test
    public void sesListTest1(){
        Configuration config2 = (Configuration) ctx.getBean("config1");
        Assert.assertEquals(config2.getSessionExecutionListeners().size(), 2);
        checkListOnNull(config2.getSessionExecutionListeners());
    }

    @Test
    public void sesListTest2(){
        Configuration config2 = (Configuration) ctx.getBean("config2");
        Assert.assertEquals(config2.getSessionExecutionListeners().size(), 2);
        checkListOnNull(config2.getSessionExecutionListeners());
    }


    @Test
    public void taskListTest1(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        Assert.assertEquals(config1.getDistributionListeners().size(), 2);
        checkListOnNull(config1.getDistributionListeners());
    }

    @Test
    public void taskListTest2(){
        Configuration config1 = (Configuration) ctx.getBean("config2");
        Assert.assertEquals(config1.getDistributionListeners().size(), 1);
        checkListOnNull(config1.getDistributionListeners());
    }

    private void checkListOnNull(List list){
        for (Object o : list){
            Assert.assertNotNull(o);
        }
    }
}
