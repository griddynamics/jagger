package com.griddynamics.jagger.xml;

import static com.griddynamics.jagger.JaggerLauncher.RDB_CONFIGURATION;
import static org.junit.Assert.assertEquals;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.engine.e1.reporting.OverallSessionComparisonReporter;
import com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider;
import com.griddynamics.jagger.engine.e1.sessioncomparation.ConfigurableSessionComparator;
import com.griddynamics.jagger.engine.e1.sessioncomparation.WorstCaseDecisionMaker;
import com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring.MonitoringFeatureComparator;
import com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring.StdDevMonitoringParameterDecisionMaker;
import com.griddynamics.jagger.engine.e1.sessioncomparation.workload.ThroughputWorkloadDecisionMaker;
import com.griddynamics.jagger.engine.e1.sessioncomparation.workload.WorkloadFeatureComparator;
import com.griddynamics.jagger.extension.ExtensionExporter;
import com.griddynamics.jagger.master.SessionIdProvider;
import com.griddynamics.jagger.reporting.ReportProvider;
import com.griddynamics.jagger.reporting.ReportingService;
import com.griddynamics.jagger.storage.rdb.H2DatabaseServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.net.URL;
import java.util.Properties;


/**
 * User: nmusienko
 * Date: 29.11.12
 * Time: 19:30
 */

public class JaggerReportTest {
    private static ApplicationContext context=null;
    private static H2DatabaseServer dbServer;

    @BeforeClass
    public static void testInit() throws Exception{
        URL directory = new URL("file:" + "../configuration/");
        Properties environmentProperties = new Properties();
        JaggerLauncher.loadBootProperties(directory, "profiles/local/environment.properties", environmentProperties);
        environmentProperties.put("chassis.reporter.configuration.include",environmentProperties.get("chassis.reporter.configuration.include")+", ../spring.schema/src/test/resources/example-report.conf.xml");

        ApplicationContext rdbContext = JaggerLauncher.loadContext(directory, RDB_CONFIGURATION, environmentProperties);
        dbServer = (H2DatabaseServer) rdbContext.getBean("databaseServer");
        dbServer.run();

        context = JaggerLauncher.loadContext(directory,"chassis.reporter.configuration",environmentProperties);
    }

    //TODO
    @Test
    public void checkExtensionRef(){
        String s=((ExtensionExporter)context.getBean("ext_stringBean")).getExtension().toString();
        assertEquals(s,"stringValue");
    }

    @AfterClass
    public static void testShutdown() {
        dbServer.terminate();
    }

    @Test
    public void checkExtensionBeanRef(){
        Integer s=Integer.parseInt(((ExtensionExporter) context.getBean("ext_integerBean")).getExtension().toString());
        assertEquals(s,new Integer(1101));
    }

    @Test
    public void checkBaseline(){
        ReportingService service = (ReportingService) context.getBean("report1");
        BaselineSessionProvider baselineSessionProvider = service.getContext().getBaselineSessionProvider();
        String currentSession = ((SessionIdProvider)context.getBean("sessionIdProvider")).getSessionId();
        Assert.assertEquals(baselineSessionProvider.getBaselineSession(currentSession), "4444");
    }

    @Test
    public void checkComparators(){
        ReportingService service = (ReportingService) context.getBean("report1");
        ReportProvider provider = service.getContext().getProvider("sessionComparison");
        Assert.assertNotNull(provider);

        OverallSessionComparisonReporter comparators = (OverallSessionComparisonReporter) provider;
        ConfigurableSessionComparator comparatorChain = (ConfigurableSessionComparator)comparators.getSessionComparator();

        WorstCaseDecisionMaker worstMaker = (WorstCaseDecisionMaker)comparatorChain.getDecisionMaker();
        Assert.assertNotNull(worstMaker);

        MonitoringFeatureComparator monitoringComparator = (MonitoringFeatureComparator)comparatorChain.getComparatorChain().get(0);
        Assert.assertNotNull(monitoringComparator);

        StdDevMonitoringParameterDecisionMaker monitoringMaker = (StdDevMonitoringParameterDecisionMaker)monitoringComparator.getMonitoringParameterDecisionMaker();
        Assert.assertNotNull(monitoringMaker);
        Assert.assertEquals(monitoringMaker.getFatalDeviationThreshold(), 0.7, 0.0);
        Assert.assertEquals(monitoringMaker.getWarningDeviationThreshold(), 0.5, 0.0);

        WorkloadFeatureComparator workloadComparator = (WorkloadFeatureComparator)comparatorChain.getComparatorChain().get(1);
        Assert.assertNotNull(workloadComparator);

        ThroughputWorkloadDecisionMaker workloadMaker = (ThroughputWorkloadDecisionMaker) workloadComparator.getWorkloadDecisionMaker();
        Assert.assertNotNull(workloadMaker);
        Assert.assertEquals(workloadMaker.getFatalDeviationThreshold(), 0.7, 0.0);
        Assert.assertEquals(workloadMaker.getWarningDeviationThreshold(), 0.2, 0.0);

        MonitoringFeatureComparator monitoringComparatorRef = (MonitoringFeatureComparator)comparatorChain.getComparatorChain().get(2);
        Assert.assertNotNull(monitoringComparatorRef);

        StdDevMonitoringParameterDecisionMaker monitoringMakerRef = (StdDevMonitoringParameterDecisionMaker)monitoringComparatorRef.getMonitoringParameterDecisionMaker();
        Assert.assertNotNull(monitoringMakerRef);
        Assert.assertEquals(monitoringMakerRef.getFatalDeviationThreshold(), 0.5, 0.0);
        Assert.assertEquals(monitoringMakerRef.getWarningDeviationThreshold(), 0.9, 0.0);
    }
}
