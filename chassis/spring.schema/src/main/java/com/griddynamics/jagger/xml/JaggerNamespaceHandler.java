package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.xml.beanParsers.*;
import com.griddynamics.jagger.xml.beanParsers.configuration.*;
import com.griddynamics.jagger.xml.beanParsers.limit.LimitDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.limit.LimitSetDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.monitoring.JmxMetricsDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.monitoring.MonitoringDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.monitoring.MonitoringSutDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.monitoring.jmxMetrixGroupDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.report.*;
import com.griddynamics.jagger.xml.beanParsers.task.*;
import com.griddynamics.jagger.xml.beanParsers.workload.TestDescriptionDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.balancer.OneByOneBalancerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.balancer.RoundRobinBalancerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.ApacheHttpInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.ClassInvokerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.HttpInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.SoapInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.*;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.aggregator.*;
import com.griddynamics.jagger.xml.beanParsers.workload.queryProvider.CsvProviderDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.queryProvider.FileProviderDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.queryProvider.HttpQueryDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.scenario.QueryPoolScenarioDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class JaggerNamespaceHandler extends NamespaceHandlerSupport {

    private FindParserByTypeDefinitionParser findTypeParser = new FindParserByTypeDefinitionParser();
    private ListCustomDefinitionParser listCustomDefinitionParser = new ListCustomDefinitionParser();
    private MapCustomDefinitionParser mapCustomDefinitionParser = new MapCustomDefinitionParser();
    private PrimitiveDefinitionParser primitiveParser = new PrimitiveDefinitionParser();

    public void init() {

        //CONFIGURATION
        registerBeanDefinitionParser("configuration", new ConfigDefinitionParser());
        registerBeanDefinitionParser("test-suite", new TestSuiteDefinitionParser());
        registerBeanDefinitionParser("test-group", new TestDefinitionParser());
        registerBeanDefinitionParser("latency-percentiles", listCustomDefinitionParser);
        registerBeanDefinitionParser("percentile", primitiveParser);

        //REPORT
        registerBeanDefinitionParser("report", new ReportDefinitionParser());
        registerBeanDefinitionParser("processing", new TestPlanDefinitionParser());
        registerBeanDefinitionParser("extension", new ExtensionDefinitionParser());
        registerBeanDefinitionParser("extensions", new ExtensionsDefinitionParser());
        registerBeanDefinitionParser("session-comparators", new SessionComparatorsDefinitionParser());
        registerBeanDefinitionParser("comparator", findTypeParser);

        registerBeanDefinitionParser("decision-maker", findTypeParser);

        //TASKS
        registerBeanDefinitionParser("test", new TaskDefinitionParser());

        //type of tasks
        registerBeanDefinitionParser("load",  findTypeParser);

        registerBeanDefinitionParser("load-user-group", new UserGroupDefinitionParser());
        registerBeanDefinitionParser("load-user-groups", new UserGroupsDefinitionParser());
        registerBeanDefinitionParser("load-invocation", new InvocationDefinitionParser());
        registerBeanDefinitionParser("user", new UserDefinitionParser());
        registerBeanDefinitionParser("load-tps", new TpsDefinitionParser());
        registerBeanDefinitionParser("load-rps", new RpsDefinitionParser());
        registerBeanDefinitionParser("load-threads", new VirtualUserDefinitionParser());

        //Test-description
        registerBeanDefinitionParser("test-description" , new TestDescriptionDefinitionParser());

        //validator
        registerBeanDefinitionParser("validator", findTypeParser);

        //validators listeners
        registerBeanDefinitionParser("validator-not-null-response", new NotNullResponseDefinitionParser());
        registerBeanDefinitionParser("validator-custom", new CustomValidatorDefinitionParser());

        //metric
        registerBeanDefinitionParser("metric", findTypeParser);

        //metric calculators
        registerBeanDefinitionParser("metric-not-null-response", new SimpleMetricDefinitionParser());
        registerBeanDefinitionParser("metric-custom", new CustomMetricDefinitionParser());
        registerBeanDefinitionParser("metric-success-rate", new SuccessRateCollectorDefinitionParser());

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
        registerBeanDefinitionParser("invoker-apache-http", new ApacheHttpInvokerClassDefinitionParser());
        registerBeanDefinitionParser("invoker-soap", new SoapInvokerClassDefinitionParser());
        registerBeanDefinitionParser("invoker-class", new ClassInvokerDefinitionParser());

        //endpointProvider
        registerBeanDefinitionParser("endpoint-provider", findTypeParser);
        registerBeanDefinitionParser("endpoint", findTypeParser);

        //endpointProviders
        registerBeanDefinitionParser("endpoint-provider-list", listCustomDefinitionParser);
        registerBeanDefinitionParser("endpoint-provider-file", new FileProviderDefinitionParser());
        registerBeanDefinitionParser("endpoint-provider-csv", new CsvProviderDefinitionParser());

        //queryProvider
        registerBeanDefinitionParser("query-provider", findTypeParser);

        //queryProviders
        registerBeanDefinitionParser("query-provider-list", listCustomDefinitionParser);
        registerBeanDefinitionParser("query-provider-file", new FileProviderDefinitionParser());
        registerBeanDefinitionParser("query-provider-csv", new CsvProviderDefinitionParser());

        //objectCreator
        registerBeanDefinitionParser("object-creator", findTypeParser);

        //queries
        registerBeanDefinitionParser("query", findTypeParser);
        registerBeanDefinitionParser("query-http", new HttpQueryDefinitionParser());
        registerBeanDefinitionParser("client-params", mapCustomDefinitionParser);
        registerBeanDefinitionParser("method-params", mapCustomDefinitionParser);

        //termination strategy
        registerBeanDefinitionParser("termination",  findTypeParser);
        registerBeanDefinitionParser("termination-iterations", new IterationsOrDurationTerminationStrategyDefinitionParser());
        registerBeanDefinitionParser("termination-duration"  , new IterationsOrDurationTerminationStrategyDefinitionParser());
        registerBeanDefinitionParser("termination-background", new BackgroundTerminationStrategyDefinitionParser());

        //monitoring
        registerBeanDefinitionParser("monitoring", new MonitoringDefinitionParser());
        registerBeanDefinitionParser("monitoring-sut", new MonitoringSutDefinitionParser());
        registerBeanDefinitionParser("jmx-metrics"  , new JmxMetricsDefinitionParser());
        registerBeanDefinitionParser("jmx-metrics-group", new jmxMetrixGroupDefinitionParser());


        //metric aggregators
        registerBeanDefinitionParser("metric-aggregator", findTypeParser);
        registerBeanDefinitionParser("metric-aggregator-avg", new AvgMetricAggregatorDefinitionParser());
        registerBeanDefinitionParser("metric-aggregator-sum", new SumMetricAggregatorDefinitionParser());
        registerBeanDefinitionParser("metric-aggregator-std", new StdDevMetricAggregatorDefinitionParser());
        registerBeanDefinitionParser("metric-aggregator-ref", new RefMetricAggregatorDefinitionParser());


        //listeners
        registerBeanDefinitionParser("listener-invocation", findTypeParser);
        registerBeanDefinitionParser("listener-invocation-not-null-response", new NotNullInvocationListenerDefinitionParser());

        registerBeanDefinitionParser("listener-test", findTypeParser);
        registerBeanDefinitionParser("listeners-test", listCustomDefinitionParser);
        registerBeanDefinitionParser("listener-test-threads", new ThreadsTestListenerDefinitionParser());

        registerBeanDefinitionParser("listener-test-group", findTypeParser);
        registerBeanDefinitionParser("listeners-test-group", listCustomDefinitionParser);

        registerBeanDefinitionParser("listener-test-suite", findTypeParser);
        registerBeanDefinitionParser("listeners-test-suite", listCustomDefinitionParser);

        registerBeanDefinitionParser("listener-test-group-decision-maker", findTypeParser);
        registerBeanDefinitionParser("listeners-test-group-decision-maker", listCustomDefinitionParser);
        registerBeanDefinitionParser("listener-test-group-decision-maker-basic", new BasicTGDecisionMakerListenerDefinitionParser());

        //limits
        registerBeanDefinitionParser("limits", new LimitSetDefinitionParser());
        registerBeanDefinitionParser("limit", new LimitDefinitionParser());
    }
}
