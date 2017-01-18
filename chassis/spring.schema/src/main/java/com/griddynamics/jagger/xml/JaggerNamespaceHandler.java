package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.xml.beanParsers.FindParserByTypeDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.ListCustomDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.MapCustomDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.PrimitiveDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.configuration.TestPlanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.configuration.TestSuiteDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.limit.LimitDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.limit.LimitSetDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.monitoring.JmxMetricsDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.monitoring.MonitoringDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.monitoring.MonitoringSutDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.monitoring.jmxMetrixGroupDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.report.ExtensionDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.report.ExtensionsDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.report.ReportDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.report.SessionComparatorsDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.BackgroundTerminationStrategyDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.InvocationDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.IterationsOrDurationTerminationStrategyDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.RpsDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.TpsDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.UserDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.UserGroupDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.UserGroupsDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.VirtualUserDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.balancer.OneByOneBalancerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.balancer.RoundRobinBalancerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.ApacheHttpInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.ClassInvokerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.HttpInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.SoapInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.BasicTGDecisionMakerListenerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.CustomMetricDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.CustomValidatorDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.NotNullInvocationListenerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.NotNullResponseDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.SimpleMetricDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.SuccessRateCollectorDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.ThreadsTestListenerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.aggregator.AvgMetricAggregatorDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.aggregator.RefMetricAggregatorDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.aggregator.StdDevMetricAggregatorDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.aggregator.SumMetricAggregatorDefinitionParser;
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
        registerBeanDefinitionParser("test-suite", new TestSuiteDefinitionParser());
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

        //type of tasks
        registerBeanDefinitionParser("load",  findTypeParser);

        registerBeanDefinitionParser("load-user-group", new UserGroupDefinitionParser());
        registerBeanDefinitionParser("load-user-groups", new UserGroupsDefinitionParser());
        registerBeanDefinitionParser("load-invocation", new InvocationDefinitionParser());
        registerBeanDefinitionParser("user", new UserDefinitionParser());
        registerBeanDefinitionParser("load-tps", new TpsDefinitionParser());
        registerBeanDefinitionParser("load-rps", new RpsDefinitionParser());
        registerBeanDefinitionParser("load-threads", new VirtualUserDefinitionParser());


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
