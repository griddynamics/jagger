package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.engine.e1.aggregator.workload.DurationLogProcessor;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.invoker.QueryPoolScenario;
import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import com.griddynamics.jagger.invoker.ScenarioFactory;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.reporting.ReportingService;
import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/19/13
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class JaggerInheritanceTest {
    private ApplicationContext ctx;

    @BeforeClass
    public void testInit() throws Exception{
        URL directory = new URL("file:" + "../configuration/");
        Properties environmentProperties = new Properties();
        JaggerLauncher.loadBootProperties(directory, "profiles/local/environment.properties", environmentProperties);
        environmentProperties.put("chassis.master.configuration.include",environmentProperties.get("chassis.master.configuration.include")+", ../spring.schema/src/test/resources/example-inheritance.conf.xml1");
        ctx = JaggerLauncher.loadContext(directory,"chassis.master.configuration",environmentProperties);
    }

    @Test
    public void testReportInheritance(){
        Configuration config1 = (Configuration) ctx.getBean("config1");

        ReportingService report = config1.getReport();

        Assert.assertNotNull(report);
        Assert.assertEquals(report.getOutputReportLocation(), "config2-report.pdf");
        Assert.assertEquals(report.getReportType().name(), "PDF");
    }

    @Test
    public void testLatencyInheritance(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        List<DistributionListener> listeners = config1.getDistributionListeners();
        DurationLogProcessor latencyValues = (DurationLogProcessor) listeners.get(listeners.size()-1);
        Assert.assertEquals(latencyValues.getGlobalPercentilesKeys().size(), 2);
        Assert.assertEquals(latencyValues.getTimeWindowPercentilesKeys().size(), 2);
    }

//    @Test
//    public void testTestSuiteInheritance(){
//        Configuration config1 = (Configuration) ctx.getBean("config1");
//        Assert.assertNotNull(config1.getTasks());
//        Assert.assertEquals(config1.getTasks().size(), 2);
//    }

    @Test
    public void testScenarioInheritance(){
        QueryPoolScenarioFactory scenario = (QueryPoolScenarioFactory) ctx.getBean("sc1");
        Assert.assertNotNull(scenario);

        Iterable endpoints = scenario.getEndpointProvider();
        Iterable queries = scenario.getQueryProvider();

        Assert.assertEquals(getSize(endpoints), 3);
        Assert.assertEquals(getSize(queries), 1);
    }

    @Test
    public void testTestDescriptionInheritance(){
        WorkloadTask description = (WorkloadTask) ctx.getBean("desc1");

        List collectors = description.getCollectors();
        Assert.assertNotNull(collectors);
        Assert.assertEquals(collectors.size(), 3);

        QueryPoolScenarioFactory scenario = (QueryPoolScenarioFactory) description.getScenarioFactory();
        Iterable endpoints = scenario.getEndpointProvider();
        Iterable queries = scenario.getQueryProvider();

        Assert.assertEquals(getSize(endpoints), 3);
        Assert.assertEquals(getSize(queries), 2);
    }

    private int getSize(Iterable iterable){
        int size = 0;
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()){
            iterator.next();
            size++;
        }
        return size;
    }
}
