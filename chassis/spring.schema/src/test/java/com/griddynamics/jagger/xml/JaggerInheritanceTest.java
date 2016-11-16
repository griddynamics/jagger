package com.griddynamics.jagger.xml;

import static com.griddynamics.jagger.JaggerLauncher.RDB_CONFIGURATION;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.engine.e1.aggregator.workload.DurationLogProcessor;
import com.griddynamics.jagger.engine.e1.collector.ConsistencyValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.Validator;
import com.griddynamics.jagger.engine.e1.collector.ValidatorProvider;
import com.griddynamics.jagger.engine.e1.scenario.IterationsOrDurationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.engine.e1.scenario.ReflectionProvider;
import com.griddynamics.jagger.engine.e1.scenario.TpsClockConfiguration;
import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.reporting.ReportingService;
import com.griddynamics.jagger.storage.rdb.H2DatabaseServer;
import com.griddynamics.jagger.user.TestConfiguration;
import com.griddynamics.jagger.user.TestDescription;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * User: kgribov
 * Date: 2/19/13
 * Time: 1:30 PM
 */
public class JaggerInheritanceTest {
    private static AbstractXmlApplicationContext ctx;
    private static AbstractXmlApplicationContext dbCtx;

    @BeforeClass
    public static  void testInit() throws Exception{
        URL directory = new URL("file:" + "../configuration/");
        Properties environmentProperties = new Properties();
        JaggerLauncher.loadBootProperties(directory, "profiles/local/environment.properties", environmentProperties);
        environmentProperties.put("chassis.master.configuration.include", environmentProperties.get("chassis.master.configuration.include")+", ../spring.schema/src/test/resources/example-inheritance.conf.xml1");
    
        dbCtx = JaggerLauncher.loadContext(directory, RDB_CONFIGURATION, environmentProperties);
        H2DatabaseServer dbServer = (H2DatabaseServer) dbCtx.getBean("databaseServer");
        dbServer.run();

        ctx = JaggerLauncher.loadContext(directory,"chassis.master.configuration",environmentProperties);
    }

    @AfterClass
    public static void testShutdown() {
        dbCtx.destroy();
        ctx.destroy();
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
        TestDescription description = (TestDescription) ctx.getBean("desc1");

        //check collectors
        Assert.assertEquals(description.getValidators().size(), 2);
        Assert.assertEquals(description.getStandardCollectors().size(), XMLConstants.STANDARD_WORKLOAD_LISTENERS.size());
        Assert.assertEquals(description.getMetrics().size(), 2);
        Assert.assertEquals(description.getListeners().size(), 2);

        //check validators queue
        List< KernelSideObjectProvider<Validator>> validators = description.getValidators();
        KernelSideObjectProvider validatorProvider0 = ((ValidatorProvider)validators.get(0)).getValidatorProvider();
        KernelSideObjectProvider validatorProvider1 = ((ValidatorProvider)validators.get(1)).getValidatorProvider();

        Assert.assertEquals(validatorProvider0 instanceof ReflectionProvider, true);
        Assert.assertEquals(validatorProvider1 instanceof ConsistencyValidatorProvider, true);

        QueryPoolScenarioFactory scenario = (QueryPoolScenarioFactory) description.getScenarioFactory();
        Iterable endpoints = scenario.getEndpointProvider();
        Iterable queries = scenario.getQueryProvider();

        Assert.assertEquals(getSize(endpoints), 3);
        Assert.assertEquals(getSize(queries), 2);
    }

    @Test
    public void testTestInheritance(){
        TestConfiguration test1 = (TestConfiguration)ctx.getBean("testChild1");
        IterationsOrDurationStrategyConfiguration termination;
        TpsClockConfiguration tps;
        TestDescription testDescription;

        termination = (IterationsOrDurationStrategyConfiguration)test1.getTerminateStrategyConfiguration();
        Assert.assertEquals(termination.getIterations(), 255);
        Assert.assertEquals(termination.getDuration(), "1h");

        tps = (TpsClockConfiguration)test1.getClockConfiguration();
        Assert.assertEquals(tps.getTps(), 50d);

        testDescription = test1.getTestDescription();
        Assert.assertEquals(testDescription.getDescription(),"testIntDescrParent");


        TestConfiguration test2 = (TestConfiguration)ctx.getBean("testChild2");

        termination = (IterationsOrDurationStrategyConfiguration)test2.getTerminateStrategyConfiguration();
        Assert.assertEquals(termination.getIterations(), 25);
        Assert.assertEquals(termination.getDuration(), "2h");

        tps = (TpsClockConfiguration)test2.getClockConfiguration();
        Assert.assertEquals(tps.getTps(), 20d);

        Assert.assertEquals(test2.getListeners().size(), 2);

        testDescription = test1.getTestDescription();
        Assert.assertEquals(testDescription.getDescription(),"testIntDescrParent");

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
