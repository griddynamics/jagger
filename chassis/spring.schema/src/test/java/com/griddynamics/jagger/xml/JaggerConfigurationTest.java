package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.engine.e1.aggregator.workload.DurationLogProcessor;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.reporting.CurrentConfigurationReportProvider;
import com.griddynamics.jagger.reporting.ReportingService;
import com.griddynamics.jagger.reporting.ReportingServiceProvider;
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
        environmentProperties.put("chassis.master.configuration.include",environmentProperties.get("chassis.master.configuration.include")+", ../spring.schema/src/test/resources/example-configuration.conf.xml1");
        //environmentProperties.put("chassis.master.configuration.include",environmentProperties.get("chassis.master.configuration.include")+", ../spring.schema/src/test/resources/example-configuration-import.conf.xml1");
        ctx = JaggerLauncher.loadContext(directory,"chassis.master.configuration",environmentProperties);
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
    public void outerTestPlanTest(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        Assert.assertEquals(config1.getTasks().size(), 2);
        checkListOnNull(config1.getTasks());
    }


    @Test
    public void conf1ListTest(){
        Configuration config1 = (Configuration) ctx.getBean("config1");

        Assert.assertEquals(config1.getSessionExecutionListeners().size(), 2);
        checkListOnNull(config1.getSessionExecutionListeners());

        Assert.assertEquals(config1.getDistributionListeners().size(), 6);
        checkListOnNull(config1.getDistributionListeners());
    }


    @Test
    public void conf2ListTest(){
        Configuration config2 = (Configuration) ctx.getBean("config2");

        Assert.assertEquals(config2.getSessionExecutionListeners().size(), 2);
        checkListOnNull(config2.getSessionExecutionListeners());

        Assert.assertEquals(config2.getDistributionListeners().size(), 6);
        checkListOnNull(config2.getDistributionListeners());
    }

    @Test
    public void conf1LatencyTest(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        DurationLogProcessor logProcessor = (DurationLogProcessor)config1.getDistributionListeners().get(config1.getDistributionListeners().size()-1);
        Assert.assertNotNull(logProcessor);
    }

    @Test
    public void conf1ReportTest(){
        Configuration config1 = (Configuration) ctx.getBean("config1");

        CurrentConfigurationReportProvider provider = new CurrentConfigurationReportProvider();
        provider.setConfigurationName("config1");

        ReportingService reportingService = provider.getReportingService(config1, ctx);
        Assert.assertNotNull(reportingService);
    }

    private void checkListOnNull(List list){
        for (Object o : list){
            Assert.assertNotNull(o);
        }
    }
}
