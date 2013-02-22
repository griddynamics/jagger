package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.xml.beanParsers.*;
import com.griddynamics.jagger.xml.beanParsers.configuration.*;
import com.griddynamics.jagger.xml.beanParsers.report.*;
import com.griddynamics.jagger.xml.beanParsers.task.*;
import com.griddynamics.jagger.xml.beanParsers.workload.WorkloadDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.balancer.OneByOneBalancerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.balancer.RoundRobinBalancerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.calibration.DefaultCalibratorDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.ClassInvokerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.HttpInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.SoapInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.ConsistencyDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.CustomMetricDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.NotNullResponseDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.SimpleMetricDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.queryProvider.HttpQueryDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.scenario.QueryPoolScenarioDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class JaggerNamespaceHandler extends NamespaceHandlerSupport {

    private FindParserByTypeDefinitionParser findTypeParser = new FindParserByTypeDefinitionParser();
    private ListCustomDefinitionParser listCustomDefinitionParser = new ListCustomDefinitionParser();
    private MapCustomDefinitionParser mapCustomDefinitionParser = new MapCustomDefinitionParser();
    private PrimitiveDefinitionParser primitiveParser = new PrimitiveDefinitionParser();

    public void init() {

        //CONFIGURATION
        registerBeanDefinitionParser("configuration", new ConfigDefinitionParser());
        registerBeanDefinitionParser("test-suite", listCustomDefinitionParser);
        registerBeanDefinitionParser("test-group", new TestDefinitionParser());
        registerBeanDefinitionParser("latency-percentiles", listCustomDefinitionParser);
        registerBeanDefinitionParser("percentile", primitiveParser);

        //REPORT
        registerBeanDefinitionParser("report", new ReportDefinitionParser());
        registerBeanDefinitionParser("processing", new TestPlanDefinitionParser());
        registerBeanDefinitionParser("extension", new ExtensionDefinitionParser());
        registerBeanDefinitionParser("extensions", new ExtensionsDefinitionParser());
        registerBeanDefinitionParser("comparator", new ComparatorDefinitionParser());
        registerBeanDefinitionParser("sessionComparators", new SessionComparatorsDefinitionParser());

        //TASKS
        registerBeanDefinitionParser("test", new TaskDefinitionParser());

        //type of tasks
        registerBeanDefinitionParser("load",  findTypeParser);

        registerBeanDefinitionParser("load-user-group", new UserGroupDefinitionParser());
        registerBeanDefinitionParser("load-user-groups", new UserGroupsDefinitionParser());
        registerBeanDefinitionParser("load-invocation", new InvocationDefinitionParser());
        registerBeanDefinitionParser("user", new UserDefinitionParser());
        registerBeanDefinitionParser("load-tps", new TpsDefinitionParser());
        registerBeanDefinitionParser("load-threads", new VirtualUserDefinitionParser());

        //Test-description
        registerBeanDefinitionParser("test-description" , new WorkloadDefinitionParser());

        //listeners

        //validator
        registerBeanDefinitionParser("validator", findTypeParser);

        //validators listeners
        registerBeanDefinitionParser("validator-not-null-response", new NotNullResponseDefinitionParser());
        registerBeanDefinitionParser("validator-consistency", new ConsistencyDefinitionParser());

        //metric
        registerBeanDefinitionParser("metric", findTypeParser);

        //metric calculators
        registerBeanDefinitionParser("metric-not-null-response", new SimpleMetricDefinitionParser());
        registerBeanDefinitionParser("metric-custom", new CustomMetricDefinitionParser());

        //scenario
        registerBeanDefinitionParser("scenario",  findTypeParser);

        //scenarios
        registerBeanDefinitionParser("scenario-query-pool", new QueryPoolScenarioDefinitionParser());

        //balancer
        registerBeanDefinitionParser("query-distributor", findTypeParser);

        //balancers
        registerBeanDefinitionParser("query-distributor-round-robin", new RoundRobinBalancerDefinitionParser());
        registerBeanDefinitionParser("query-distributor-one-by-one", new OneByOneBalancerDefinitionParser());

        //invoker
        registerBeanDefinitionParser("invoker", findTypeParser);

        //invokers
        registerBeanDefinitionParser("invoker-http", new HttpInvokerClassDefinitionParser());
        registerBeanDefinitionParser("invoker-soap", new SoapInvokerClassDefinitionParser());
        registerBeanDefinitionParser("invoker-class", new ClassInvokerDefinitionParser());

        //endpointProvider
        registerBeanDefinitionParser("endpoint-provider", findTypeParser);
        registerBeanDefinitionParser("endpoint", findTypeParser);

        //endpointProviders
        registerBeanDefinitionParser("endpoint-provider-list", listCustomDefinitionParser);

        //queryProvider
        registerBeanDefinitionParser("query-provider", findTypeParser);

        //queryProviders
        registerBeanDefinitionParser("query-provider-list", listCustomDefinitionParser);

        //queries
        registerBeanDefinitionParser("query", findTypeParser);
        registerBeanDefinitionParser("query-http", new HttpQueryDefinitionParser());
        registerBeanDefinitionParser("client-params", mapCustomDefinitionParser);
        registerBeanDefinitionParser("method-params", mapCustomDefinitionParser);

        //calibrators
        registerBeanDefinitionParser("calibrator", findTypeParser);
        registerBeanDefinitionParser("defaultCalibrator", new DefaultCalibratorDefinitionParser());



        //termination strategy
        registerBeanDefinitionParser("termination",  findTypeParser);
        registerBeanDefinitionParser("termination-iterations", new IterationsOrDurationTerminationStrategyDefinitionParser());
        registerBeanDefinitionParser("termination-duration"  , new IterationsOrDurationTerminationStrategyDefinitionParser());
        registerBeanDefinitionParser("termination-background", new BackgroundTerminationStrategyDefinitionParser());
    }
}
