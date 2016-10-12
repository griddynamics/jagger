package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.agent.model.JmxMetricGroup;
import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorSettings;
import com.griddynamics.jagger.engine.e1.collector.MetricCollectorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.ValidatorProvider;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import com.griddynamics.jagger.invoker.ScenarioFactory;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.reporting.ReportingService;
import com.griddynamics.jagger.user.TestGroupConfiguration;
import com.griddynamics.jagger.util.TimeUnits;
import com.griddynamics.jagger.xml.stubs.xml.ExampleDecisionMakerListener;
import com.griddynamics.jagger.xml.stubs.xml.ExampleDistributionListener;
import com.griddynamics.jagger.xml.stubs.xml.ExampleTestGroupListener;
import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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
    private String testDir;

    @BeforeClass
    public void testInit() throws Exception {
        String projectDir = getProjectDir();
        URL configurationDirectory = new URL("file:" + projectDir + "/../configuration/");
        Properties environmentProperties = new Properties();
        JaggerLauncher.loadBootProperties(configurationDirectory, "profiles/local/environment.properties", environmentProperties);
        environmentProperties.put("chassis.master.configuration.include",environmentProperties.get("chassis.master.configuration.include")+", ../spring.schema/src/test/resources/example-configuration.conf.xml1");
        ctx = JaggerLauncher.loadContext(configurationDirectory,"chassis.master.configuration",environmentProperties);
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

        Assert.assertEquals(config1.getSessionExecutionListeners().size(), 3);
        checkListOnNull(config1.getSessionExecutionListeners());

        Assert.assertEquals(config1.getDistributionListeners().size(), 8);
        checkListOnNull(config1.getDistributionListeners());
    }


    @Test
    public void conf2ListTest(){
        Configuration config2 = (Configuration) ctx.getBean("config2");

        Assert.assertEquals(config2.getSessionExecutionListeners().size(), 2);
        checkListOnNull(config2.getSessionExecutionListeners());

        Assert.assertEquals(7, config2.getDistributionListeners().size());
        checkListOnNull(config2.getDistributionListeners());
    }

    @Test
    public void conf1LatencyTest(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        ExampleDistributionListener exampleDistributionListener = (ExampleDistributionListener)config1.getDistributionListeners().get(config1.getDistributionListeners().size()-1);
        Assert.assertNotNull(exampleDistributionListener);
    }

    @Test
    public void conf1CalibrationSamplesCountTest(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        // DANGER! CLASS CAST MAGIC!!!
        ScenarioFactory scenarioFactory =
                ((WorkloadTask)((CompositeTask) config1.getTasks().get(0)).getAttendant().get(0)).getScenarioFactory();
        int calibrationSamplesCount = scenarioFactory.getCalibrationSamplesCount();
        Assert.assertEquals(1101, calibrationSamplesCount);
    }

    @Test
    public void conf1ReportTest(){
        Configuration config1 = (Configuration) ctx.getBean("config1");

        ReportingService reportingService = config1.getReport();
        Assert.assertNotNull(reportingService);
    }

    @Test
    public void conf1MetricAggregatorTest(){
        Configuration config1 = (Configuration) ctx.getBean("config1");
        //NEED TO UPDATE!!
    }

    @Test
    public void conf1ProviderTest() throws Exception {
        Configuration config1 = (Configuration) ctx.getBean("config1");
        // DANGER! CLASS CAST MAGIC!!!
        Iterator it=((QueryPoolScenarioFactory)((WorkloadTask)((CompositeTask) config1.getTasks().get(0)).getAttendant().get(0)).getScenarioFactory()).getEndpointProvider().iterator();
        Assert.assertEquals(RequestPath.class, it.next().getClass());
    }

    @Test
    public void jmxMetrics() throws MalformedObjectNameException {
        Configuration config1 = (Configuration) ctx.getBean("config1");
        ArrayList<JmxMetricGroup> groupArrayList = config1.getMonitoringConfiguration().getMonitoringSutConfiguration().getJmxMetricGroups();
        Assert.assertEquals(1, groupArrayList.size());
        JmxMetricGroup metric = groupArrayList.get(0);
        Assert.assertEquals(new ObjectName("java.lang:type=OperatingSystem"), metric.getObjectName());
        Assert.assertEquals("OperatingSystem", metric.getGroupName());
        Assert.assertEquals(1, metric.getAttributes().length);
        Assert.assertEquals("MaxFileDescriptorCount", metric.getAttributes()[0]);
    }

    @Test
    public void metricDisplayNameTest() throws Exception {
        MetricCollectorProvider mcp = (MetricCollectorProvider) ctx.getBean("metric-with-display-name");
        Assert.assertEquals("NOT_NULL_METRIC", mcp.getMetricDescriptions().getDisplayName());

        mcp = (MetricCollectorProvider) ctx.getBean("metric1");
        Assert.assertNull(mcp.getMetricDescriptions().getDisplayName());
    }

    @Test
    public void validatorDisplayNameTest() throws Exception {
        ValidatorProvider vp = (ValidatorProvider) ctx.getBean("validator-with-display-name");
        Assert.assertEquals("NOT_NULL_VALIDATOR", vp.getDisplayName());
    }

    @Test
    public void testGroupListenerTest() {
        ArrayList<TestGroupConfiguration> suitConfiguration = (ArrayList)ctx.getBean("test-plan-1");
       Assert.assertEquals(1, suitConfiguration.get(1).getListeners().size());
        Assert.assertEquals(11,((ExampleTestGroupListener)suitConfiguration.
                get(1).
                getListeners().
                get(0)).
                getTestValue());

    }

    @Test
    public void decisionMakerListenerTest() {
        ArrayList<TestGroupConfiguration> suitConfiguration = (ArrayList)ctx.getBean("test-plan-1");
        Assert.assertEquals(2, suitConfiguration.get(0).getTestGroupDecisionMakerListeners().size());
        Assert.assertEquals(15,((ExampleDecisionMakerListener)suitConfiguration.
                get(0).
                getTestGroupDecisionMakerListeners().
                get(0)).
                getTestValue());

    }
    private void checkListOnNull(List list){
        for (Object o : list){
            Assert.assertNotNull(o);
        }
    }

    @Test
    public void aggregatorWithNoSettingsTest() {

        MetricCollectorProvider mcp = (MetricCollectorProvider) ctx.getBean("metric-aggregator-with-no-settings");
        MetricDescription description = mcp.getMetricDescriptions();

        for (MetricAggregatorSettings settings : description.getAggregatorsWithSettings().values()) {
            Assert.assertEquals(TimeUnits.NONE, settings.getNormalizationBy());
            Assert.assertEquals(0, settings.getPointCount());
            Assert.assertEquals(0, settings.getPointInterval());
        }
    }

    @Test
    public void aggregatorWithSettingsTest() {

        MetricCollectorProvider mcp = (MetricCollectorProvider) ctx.getBean("metric-aggregator-with-settings");
        MetricDescription description = mcp.getMetricDescriptions();

        for (MetricAggregatorSettings settings : description.getAggregatorsWithSettings().values()) {
            Assert.assertEquals(TimeUnits.MINUTE, settings.getNormalizationBy());
            Assert.assertEquals(10, settings.getPointCount());
            Assert.assertEquals(1000, settings.getPointInterval());
        }
    }

    public static String getProjectDir() {
        String someTestResource = JaggerConfigurationTest.class.getResource("/example-configuration-import.conf.xml1").getFile();
        File someTestFile = new File(someTestResource);
        return someTestFile.getParentFile().getParentFile().getParent();
    }
}
