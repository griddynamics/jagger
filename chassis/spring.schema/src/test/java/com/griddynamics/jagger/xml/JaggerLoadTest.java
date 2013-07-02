package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.engine.e1.scenario.*;
import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.user.TestConfiguration;
import com.griddynamics.jagger.user.TestGroupConfiguration;
import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/14/12
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class JaggerLoadTest {

    private ApplicationContext context;

    @BeforeClass
    public void testContext() throws Exception{
        URL directory = new URL("file:" + "../configuration/");
        Properties environmentProperties = new Properties();
        JaggerLauncher.loadBootProperties(directory, "profiles/local/environment.properties", environmentProperties);
        environmentProperties.put("chassis.master.configuration.include",environmentProperties.get("chassis.master.configuration.include")+", ../spring.schema/src/test/resources/example-load.conf.xml1");
        context = JaggerLauncher.loadContext(directory,"chassis.master.configuration",environmentProperties);
    }

    @Test
    public void testTpsTask(){
        TpsClockConfiguration tpsClock = (TpsClockConfiguration)context.getBean("tps1");
        Assert.assertEquals(tpsClock.getTickInterval(), 1000);
        Assert.assertEquals(tpsClock.getTps(), 200d);

        tpsClock = (TpsClockConfiguration)context.getBean("tps2");
        Assert.assertEquals(tpsClock.getTickInterval(), 500);
        Assert.assertEquals(tpsClock.getTps(), 100d);
    }

    @Test
    public void testInvocationTask(){
        ExactInvocationsClockConfiguration invClock = (ExactInvocationsClockConfiguration)context.getBean("inv1");
        Assert.assertEquals(invClock.getTickInterval(), 1000);
        Assert.assertEquals(invClock.getSamplesCount(), 100);
        Assert.assertEquals(invClock.getThreadCount(), 1);
        Assert.assertEquals(invClock.getDelay(), 100);

        invClock = (ExactInvocationsClockConfiguration)context.getBean("inv2");
        Assert.assertEquals(invClock.getTickInterval(), 500);
        Assert.assertEquals(invClock.getSamplesCount(), 50);
        Assert.assertEquals(invClock.getThreadCount(), 2);
        Assert.assertEquals(invClock.getDelay(), 0);

    }

    @Test
    public void testThreadsDelay(){
        VirtualUsersClockConfiguration invClock = (VirtualUsersClockConfiguration)context.getBean("load_threads_with_delay");
        Assert.assertEquals(1101, invClock.getDelay().getInvocationDelay().getValue());

    }

    @Test
    public void testTerminationStrategy(){
        IterationsOrDurationStrategyConfiguration conf = (IterationsOrDurationStrategyConfiguration) context.getBean("ts1");
        Assert.assertEquals(conf.getIterations(), -1);
        Assert.assertEquals(conf.getDuration(), "2h");

        conf = (IterationsOrDurationStrategyConfiguration) context.getBean("ts2");
        Assert.assertEquals(conf.getIterations(), 5);
        Assert.assertEquals(conf.getDuration(), null);

        InfiniteTerminationStrategyConfiguration  conf1 = (InfiniteTerminationStrategyConfiguration) context.getBean("ts3");

    }

    @Test
    public void testNewTest(){
        TestConfiguration testConfiguration = (TestConfiguration) context.getBean("test1");

        IterationsOrDurationStrategyConfiguration termination = (IterationsOrDurationStrategyConfiguration)testConfiguration.getTerminateStrategyConfiguration();
        Assert.assertEquals(termination.getIterations(), 255);
        Assert.assertEquals(termination.getDuration(), "2h");

        TpsClockConfiguration tps = (TpsClockConfiguration)testConfiguration.getClockConfiguration();
        Assert.assertEquals(tps.getTps(), 100d);
    }

    @Test
    public void testRpsLoad(){
        TestConfiguration testConfiguration = (TestConfiguration) context.getBean("test-rps");

        IterationsOrDurationStrategyConfiguration termination = (IterationsOrDurationStrategyConfiguration)testConfiguration.getTerminateStrategyConfiguration();
        Assert.assertEquals(255, termination.getIterations());
        Assert.assertEquals("2h", termination.getDuration());

        RpsClockConfiguration tps = (RpsClockConfiguration) testConfiguration.getClockConfiguration();
        Assert.assertEquals(100d, tps.getTps());
        Assert.assertEquals(RpsClock.class, tps.getClock().getClass());
    }

    @Test
    public void testRumpUpLoad(){
        TestConfiguration testConfiguration = (TestConfiguration) context.getBean("test-rump-up");

        IterationsOrDurationStrategyConfiguration termination = (IterationsOrDurationStrategyConfiguration)testConfiguration.getTerminateStrategyConfiguration();
        Assert.assertEquals(255, termination.getIterations());
        Assert.assertEquals("2h", termination.getDuration());

        TpsClockConfiguration tps = (TpsClockConfiguration) testConfiguration.getClockConfiguration();
        Assert.assertEquals(100d, tps.getTps());
        Assert.assertEquals(TpsClock.class, tps.getClock().getClass());
        Assert.assertEquals(10000L, tps.getWarmUpTime());
    }

    @Test
    public void testNewTestGroup(){
        TestGroupConfiguration testGroup = (TestGroupConfiguration)context.getBean("gr1");

        List<TestConfiguration> tests = testGroup.getTests();
        Assert.assertEquals(tests.size(), 1);

        TestConfiguration test = tests.get(0);
        InfiniteTerminationStrategyConfiguration termination = (InfiniteTerminationStrategyConfiguration)test.getTerminateStrategyConfiguration();
        Assert.assertNotNull(termination);

        UserGroupsClockConfiguration userGroup = (UserGroupsClockConfiguration)test.getClockConfiguration();
        Assert.assertEquals(userGroup.getUsers().size(), 1);

        ProcessingConfig.Test.Task.User user = userGroup.getUsers().get(0);
        Assert.assertEquals(user.getCount(), "5");
    }
}
