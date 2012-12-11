package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider;
import com.griddynamics.jagger.engine.e1.sessioncomparation.ConfigurableSessionComparator;
import com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring.MonitoringFeatureComparator;
import com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring.StdDevMonitoringParameterDecisionMaker;
import com.griddynamics.jagger.engine.e1.sessioncomparation.workload.ThroughputWorkloadDecisionMaker;
import com.griddynamics.jagger.engine.e1.sessioncomparation.workload.WorkloadFeatureComparator;
import com.griddynamics.jagger.extension.ExtensionExporter;
import com.griddynamics.jagger.reporting.ReportingContext;
import com.griddynamics.jagger.reporting.ReportingService;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Properties;

import static org.testng.Assert.assertEquals;


/**
 * Created by IntelliJ IDEA.
 * User: nmusienko
 * Date: 29.11.12
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */
public class JaggerReportTest {
    ApplicationContext context=null;

    @BeforeClass
    public void testInit() throws Exception{
        URL directory = new URL("file:" + "../configuration/");
        Properties environmentProperties = new Properties();
        JaggerLauncher.loadBootProperties(directory, "profiles/local/environment.properties", environmentProperties);
        environmentProperties.put("chassis.reporter.configuration.include",environmentProperties.get("chassis.reporter.configuration.include")+", ../spring.schema/src/test/resources/example-report.xml");
        context = JaggerLauncher.loadContext(directory,"chassis.reporter.configuration",environmentProperties);
    }

    @Test
    public void checkExtensionRef(){
        String s=((ExtensionExporter)context.getBean("ext_stringBean")).getExtension().toString();
        assertEquals(s,"stringValue");
    }

    @Test
    public void checkExtensionBeanRef(){
        Integer s=Integer.parseInt(((ExtensionExporter) context.getBean("ext_integerBean")).getExtension().toString());
        assertEquals(s,new Integer(1101));
    }

    @Test
    public void checkBaseline(){
        BaselineSessionProvider provider=(BaselineSessionProvider) context.getBean("baselineSessionProvider");
        assertEquals(provider.getBaselineSession(),"4444");
    }

    @Test
    public void checkStrategy(){
        ConfigurableSessionComparator comparator=(ConfigurableSessionComparator) context.getBean("sessionComparator");
        assertEquals(comparator.getDecisionMaker(), context.getBean("worstCaseDecisionMaker"));
    }

    @Test
    public void checkMonitoringComparator(){
        ConfigurableSessionComparator comparator=(ConfigurableSessionComparator) context.getBean("sessionComparator");
        checkMonitoringComparator((MonitoringFeatureComparator) comparator.getComparatorChain().get(0),0.5,0.7);
    }

    @Test
    public void checkWorkloadComparator(){
        ConfigurableSessionComparator comparator=(ConfigurableSessionComparator) context.getBean("sessionComparator");
        checkWorkloadComparator((WorkloadFeatureComparator) comparator.getComparatorChain().get(1), 0.2, 0.7);
    }

    @Test
    public void checkMonitoringComparatorRef(){
        ConfigurableSessionComparator comparator=(ConfigurableSessionComparator) context.getBean("sessionComparator");
        checkMonitoringComparator((MonitoringFeatureComparator) comparator.getComparatorChain().get(2),0.9, 0.5);
    }

    @Test
    public void checkReportingService(){
        ReportingService service=(ReportingService) context.getBean("reportingService");
        assertEquals(service.getReportType().toString(),"PDF");
        assertEquals(service.getRootTemplateLocation(), "custom-root-template.jrxml");
        assertEquals(service.getOutputReportLocation(),"custom-report.pdf");
        ReportingContext defaultContext=(ReportingContext) context.getBean("reportingContext");
        assertEquals(service.getContext(),defaultContext);
    }

    @Test
    private void checkWorkloadComparator(WorkloadFeatureComparator comparator, double warning, double fatal){
        assertEquals(comparator.getSessionFactory(), context.getBean("sessionFactory"));
        ThroughputWorkloadDecisionMaker decisionMaker=(ThroughputWorkloadDecisionMaker)comparator.getWorkloadDecisionMaker();
        assertEquals(decisionMaker.getFatalDeviationThreshold(),fatal);
        assertEquals(decisionMaker.getWarningDeviationThreshold(),warning);
    }

    @Test
    private void checkMonitoringComparator(MonitoringFeatureComparator comparator, double warning, double fatal){
        assertEquals(comparator.getSessionFactory(), context.getBean("sessionFactory"));
        assertEquals(comparator.getMonitoringSummaryRetriever(), context.getBean("monitoringSummaryRetriever"));
        StdDevMonitoringParameterDecisionMaker decisionMaker=(StdDevMonitoringParameterDecisionMaker)comparator.getMonitoringParameterDecisionMaker();
        assertEquals(decisionMaker.getFatalDeviationThreshold(),fatal);
        assertEquals(decisionMaker.getWarningDeviationThreshold(),warning);
    }

}
