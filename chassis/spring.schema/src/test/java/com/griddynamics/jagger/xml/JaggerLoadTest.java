package com.griddynamics.jagger.xml;

import static com.griddynamics.jagger.JaggerLauncher.RDB_CONFIGURATION;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.engine.e1.scenario.ExactInvocationsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.InfiniteTerminationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.IterationsOrDurationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.QpsClock;
import com.griddynamics.jagger.engine.e1.scenario.QpsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.TpsClock;
import com.griddynamics.jagger.engine.e1.scenario.TpsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.UserGroupsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.VirtualUsersClockConfiguration;
import com.griddynamics.jagger.storage.rdb.H2DatabaseServer;
import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.user.TestConfiguration;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.net.URL;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: kgribov
 * Date: 12/14/12
 */
public class JaggerLoadTest {

    private static ApplicationContext context;
    private static H2DatabaseServer dbServer;

    @BeforeClass
    public static void testContext() throws Exception{
        URL directory = new URL("file:" + "../configuration/");
        Properties environmentProperties = new Properties();
        JaggerLauncher.loadBootProperties(directory, "profiles/local/environment.properties", environmentProperties);
        environmentProperties.put("chassis.master.configuration.include",environmentProperties.get("chassis.master.configuration.include")+", ../spring.schema/src/test/resources/example-load.conf.xml1");

        ApplicationContext rdbContext = JaggerLauncher.loadContext(directory, RDB_CONFIGURATION, environmentProperties);
        dbServer = (H2DatabaseServer) rdbContext.getBean("databaseServer");
        dbServer.run();

        context = JaggerLauncher.loadContext(directory,"chassis.master.configuration",environmentProperties);
    }

    @AfterClass
    public static void testShutdown() {
        dbServer.terminate();
    }

    @Test
    public void testThreadsLoad(){
        TestConfiguration testConfiguration = (TestConfiguration) context.getBean("test-threads");

        InfiniteTerminationStrategyConfiguration termination = (InfiniteTerminationStrategyConfiguration)testConfiguration.getTerminateStrategyConfiguration();
        Assert.assertNotNull(termination);

        VirtualUsersClockConfiguration threads = (VirtualUsersClockConfiguration)testConfiguration.getClockConfiguration();
        Assert.assertEquals(threads.getClock().getValue(), 10);
    }

    @Test
    public void testInvocationTask(){
        TestConfiguration testConfiguration = (TestConfiguration) context.getBean("test-invocation-1");
        Assert.assertNotNull(testConfiguration.getTerminateStrategyConfiguration());
        ExactInvocationsClockConfiguration invClock = (ExactInvocationsClockConfiguration)testConfiguration.getClockConfiguration();
        Assert.assertEquals(invClock.getTickInterval(), 1000);
        Assert.assertEquals(invClock.getSamplesCount(), 100);
        Assert.assertEquals(invClock.getThreadCount(), 1);
        Assert.assertEquals(invClock.getDelay(), 100);
        Assert.assertEquals("15m", invClock.getPeriod());

        testConfiguration = (TestConfiguration) context.getBean("test-invocation-2");
        Assert.assertNotNull(testConfiguration.getTerminateStrategyConfiguration());
        invClock = (ExactInvocationsClockConfiguration)testConfiguration.getClockConfiguration();
        Assert.assertEquals(invClock.getTickInterval(), 500);
        Assert.assertEquals(invClock.getSamplesCount(), 50);
        Assert.assertEquals(invClock.getThreadCount(), 2);
        Assert.assertEquals(invClock.getDelay(), 0);
        Assert.assertEquals("-1" , invClock.getPeriod());

    }

    @Test
    public void testUserGroupLoad(){
        TestConfiguration testGroup = (TestConfiguration)context.getBean("test-user-group");

        IterationsOrDurationStrategyConfiguration termination = (IterationsOrDurationStrategyConfiguration)testGroup.getTerminateStrategyConfiguration();
        Assert.assertNotNull(termination);
        Assert.assertEquals(termination.getDuration(), "10m");   
        UserGroupsClockConfiguration userGroup = (UserGroupsClockConfiguration)testGroup.getClockConfiguration();
        Assert.assertEquals(userGroup.getUsers().size(), 1);

        ProcessingConfig.Test.Task.User user = userGroup.getUsers().get(0);
        Assert.assertEquals(user.getCount(), "10");
        Assert.assertEquals(user.getStartBy(), "1");
        Assert.assertEquals(user.getStartCount(), "2");
        Assert.assertEquals(user.getStartIn(), "3");
        Assert.assertEquals(user.getLife(), "2h");
    }

    @Test
    public void testUserGroupsLoad(){
        TestConfiguration testGroups = (TestConfiguration)context.getBean("test-user-groups");

        IterationsOrDurationStrategyConfiguration termination = (IterationsOrDurationStrategyConfiguration)testGroups.getTerminateStrategyConfiguration();
        Assert.assertNotNull(termination);
        Assert.assertEquals(termination.getIterations(), 10000);

        UserGroupsClockConfiguration userGroup = (UserGroupsClockConfiguration)testGroups.getClockConfiguration();
        Assert.assertEquals(userGroup.getUsers().size(), 2);

        ProcessingConfig.Test.Task.User user1 = userGroup.getUsers().get(0);
        Assert.assertEquals(user1.getCount(), "10");
        Assert.assertEquals(user1.getStartBy(), "7");
        Assert.assertEquals(user1.getStartCount(), "5");
        Assert.assertEquals(user1.getStartIn(), "6");
        Assert.assertEquals(user1.getLife(), "1h");

        ProcessingConfig.Test.Task.User user2 = userGroup.getUsers().get(1);
        Assert.assertEquals(user2.getCount(), "20");
        Assert.assertEquals(user2.getStartBy(), "14");
        Assert.assertEquals(user2.getStartCount(), "10");
        Assert.assertEquals(user2.getStartIn(), "12");
        Assert.assertEquals(user2.getLife(), "2h");
    }

    @Test
    public void testTpsLoad(){
        TestConfiguration testConfiguration = (TestConfiguration) context.getBean("test-tps");

        IterationsOrDurationStrategyConfiguration termination = (IterationsOrDurationStrategyConfiguration)testConfiguration.getTerminateStrategyConfiguration();
        Assert.assertEquals(termination.getIterations(), 255);
        Assert.assertEquals(termination.getDuration(), "2h");

        TpsClockConfiguration tps = (TpsClockConfiguration)testConfiguration.getClockConfiguration();
        Assert.assertEquals(tps.getTps(), 100d);
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
    public void testRpsLoad(){
        TestConfiguration testConfiguration = (TestConfiguration) context.getBean("test-rps");

        IterationsOrDurationStrategyConfiguration termination = (IterationsOrDurationStrategyConfiguration)testConfiguration.getTerminateStrategyConfiguration();
        Assert.assertEquals(255, termination.getIterations());
        Assert.assertEquals("2h", termination.getDuration());

        Assert.assertEquals(0, testConfiguration.generate(new AtomicBoolean(false)).getStartDelay());

        QpsClockConfiguration tps = (QpsClockConfiguration) testConfiguration.getClockConfiguration();
        Assert.assertEquals(100d, tps.getTps());
        Assert.assertEquals(QpsClock.class, tps.getClock().getClass());
    }
}
