package com.griddynamics.jagger.xml.beanParsers;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nmusienko
 * Date: 05.12.12
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */
public class XMLConstants {

    public static final String FATAL_DEVIATION_THRESHOLD = "fatalDeviationThreshold";
    public static final String WARNING_DEVIATION_THRESHOLD = "warningDeviationThreshold";
    public static final String WORKLOAD_DECISION_MAKER = "workloadDecisionMaker";
    public static final String MONITORING_PARAMETER_DECISION_MAKER = "monitoringParameterDecisionMaker";
    public static final String WORKLOAD_FEATURE_COMPARATOR = "workloadFeatureComparator";
    public static final String MONITORING_FEATURE_COMPARATOR = "monitoringFeatureComparator";
    public static final String DECISION_MAKER_TYPE = "decisionMakerType";
    public static final String DECISION_MAKER_REF = "decisionMakerRef";
    public static final String WORKLOAD = "workload";
    public static final String MONITORING = "monitoring";
    public static final String COMPARATOR_TYPE = "comparatorType";
    public static final String ID = "id";
    public static final String DEFAULT_REPORTING_SERVICE = "defaultReportingService";
    public static final String CUSTOM_REPORTING_SERVICE = "customReportingService";
    public static final String REPORTING_SERVICE = "reportingService";
    public static final String REPORT_TYPE = "reportType";
    public static final String ROOT_TEMPLATE_LOCATION = "rootTemplateLocation";
    public static final String OUTPUT_REPORT_LOCATION = "outputReportLocation";
    public static final String EXTENSION_PREFIX = "ext_";
    public static final String EXTENSION = "extension";
    public static final String ATTRIBUTE_REF = "ref";
    public static final String BEAN = "bean";
    public static final String COMPARATOR = "comparator";
    public static final String COMPARATOR_CHAIN = "comparatorChain";
    public static final String DECISION_MAKER = "decisionMaker";
    public static final String STRATEGY = "strategy";
    public static final String BASELINE_ID = "baselineId";
    public static final String CUSTOM_SESSION_COMPARATOR = "customSessionComparator";
    public static final String SESSION_COMPARATOR = "sessionComparator";
    public static final String SESSION_COMPARATORS_ELEMENT = "session-comparators";
    public static final String SESSION_COMPARISON = "sessionComparison";
    public static final String WORST_CASE_DECISION_MAKER = "worstCaseDecisionMaker";
    public static final String WORST_CASE = "worstCase";
    public static final String BASELINE_SESSION_ID = "baselineSessionId";
    public static final String SESSION_ID_PROVIDER = "sessionIdProvider";
    public static final String CUSTOM_BASELINE_SESSION_PROVIDER = "customBaselineSessionProvider";
    public static final String BASELINE_SESSION_PROVIDER = "baselineSessionProvider";
    public static final String TEST_SUITE = "test-suite";
    public static final String TEST_GROUP = "test-group";
    public static final String CONFIG = "config";
    public static final String TASKS = "tasks";
    public static final String TASK = "task";
    public static final String TESTS = "tests";
    public static final String TEST_GROUPS = "testGroups";
    public static final String TEST = "test";
    public static final String USERS = "users";
    public static final String USER = "user";
    public static final String TPS = "tps";
    public static final String INVOCATION = "invocation";
    public static final String VIRTUAL_USER = "virtual-user";
    public static final String VIRTUAL_USER_CLASS_FIELD = "virtualUser";
    public static final String SESSION_EXECUTION_LISTENERS = "session-execution-listeners";
    public static final String SESSION_EXECUTION_LISTENERS_CLASS_FIELD = "sessionExecutionListeners";
    public static final String TASK_EXECUTION_LISTENERS = "task-execution-listeners";
    public static final String TASK_EXECUTION_LISTENERS_CLASS_FIELD = "taskExecutionListeners";
    public static final String GENERATOR = "generator";
    public static final String GENERATOR_GENERATE = "#{generator.generate()}";
    public static final String LOCAL = "local";
    public static final String MONITORING_ENABLE = "monitoringEnable";
    //listeners beans. must be in scope(locations - default-collectors.conf.xml , default-aggregators.conf.xml)
    public static final String BASIC_COLLECTOR = "basicSessionCollector";
    public static final String WORKLOAD_COLLECTOR = "e1MasterCollector";
    public static final String BASIC_AGGREGATOR = "basicAggregator";
    public static final String WORKLOAD_AGGREGATOR = "e1ScenarioAggregator";
    public static final String DURATION_LOG_PROCESSOR = "durationLogProcessor";
    public static final String METRIC_LOG_PROCESSOR = "metricLogProcessor";
    public static final String PROFILER_LOG_PROCESSOR = "profilerLogProcessor";
    //don't change the order!!! will not works
    public static final List<String> STANDARD_SESSION_EXEC_LISTENERS = Arrays.asList(BASIC_COLLECTOR, BASIC_AGGREGATOR);
    public static final List<String> STANDARD_TASK_EXEC_LISTENERS = Arrays.asList(BASIC_COLLECTOR, WORKLOAD_COLLECTOR, BASIC_AGGREGATOR, WORKLOAD_AGGREGATOR, METRIC_LOG_PROCESSOR, PROFILER_LOG_PROCESSOR);

    public static final String WORKLOAD_LISTENERS_ELEMENT = "info-collectors";
    public static final String DURATION_COLLECTOR = "durationCollector";
    public static final String INFORMATION_COLLECTOR = "informationCollector";
    public static final String DIAGNOSTIC_COLLECTOR = "diagnosticCollector";
    public static final String THREADS_AVG_COLLECTOR = "threadsAvg";
    public static final String DISPLAY_NAME = "displayName";

    public static final String LISTENERS = "listeners";
    public static final String TEST_LISTENERS = "listeners-test";
    public static final String TEST_LISTENER = "listener-test";
    public static final String TEST_GROUP_LISTENERS = "listeners-test-group";
    public static final String TEST_GROUP_LISTENER = "listener-test-group";
    public static final String TEST_SUITE_LISTENERS = "listeners-test-suite";
    public static final String INVOCATION_LISTENER = "listener-invocation";
    public static final String TEST_GROUP_DECISION_MAKER_LISTENERS = "listeners-test-group-decision-maker";

    //don't change the order!!! will not works
    public static final String STANDARD_COLLECTORS = "standardCollectors";
    public static final String STANDARD_WORKLOAD_STATUS_COLLECTORS = "workloadStatusCollectors";

    public static final List<String> STANDARD_WORKLOAD_LISTENERS = Arrays.asList(INFORMATION_COLLECTOR, DURATION_COLLECTOR);
    public static final List<String> STANDARD_WORKLOAD_STATUS_LISTENERS = Arrays.asList(THREADS_AVG_COLLECTOR);


    public static final String WORKLOAD_LISTENERS_CLASS   = "collectors";
    public static final String VALIDATOR = "validator";
    public static final String VALIDATORS = "validators";
    public static final String METRIC = "metric";
    public static final String METRICS = "metrics";
    public static final String QUERY_EQ = "queryEq";
    public static final String ENDPOINT_EQ = "endpointEq";
    public static final String RESULT_EQ = "resultEq";
    public static final String METRIC_CALCULATOR = "metricCalculator";
    public static final String CALCULATOR = "calculator";
    public static final String LIST = "list";
    public static final String CLIENT_PARAMS = "clientParams";
    public static final String METHOD_PARAMS = "methodParams";
    public static final String CLIENT_PARAMS_ELEMENT = "client-params";
    public static final String METHOD_PARAMS_ELEMENT = "method-params";
    public static final String METHOD = "method";
    public static final String INVOKER = "invoker";
    public static final String INVOKER_CLAZZ = "invokerClazz";
    public static final String WARM_UP_TIME = "warmUpTime";
    public static final String ENDPOINT_PROVIDER = "endpointProvider";
    public static final String ENDPOINT_PROVIDER_ELEMENT = "endpoint-provider";
    public static final String QUERY_PROVIDER = "queryProvider";
    public static final String QUERY_PROVIDER_ELEMENT = "query-provider";
    public static final String LOAD_BALANCER = "loadBalancer";
    public static final String QUERY_DISTRIBUTOR = "query-distributor";
    public static final String SCENARIO = "scenario";
    public static final String SCENARIO_FACTORY = "scenarioFactory";
    public static final String PARENT = "parent";
    public static final String XSI_TYPE = "xsi:type";
    public static final String LATENCY = "latency-percentiles";
    public static final String PERCENTILES_TIME = "percentiles-time";
    public static final String PERCENTILES_GLOBAL = "percentiles-global";
    public static final String TIME_WINDOW_PERCENTILES_KEYS = "timeWindowPercentilesKeys";
    public static final String GLOBAL_PERCENTILES_KEYS = "globalPercentilesKeys";
    public static final String REPORT = "report";
    public static final String CALIBRATOR = "calibrator";
    public static final String CALIBRATION = "calibration";
    public static final String NAME = "name";
    public static final String CLASS = "class";
    public static final String TEST_DESCRIPTION = "testDescription";
    public static final String TEST_DESCRIPTION_CLASS_FIELD = "testDescription";
    public static final String LOAD = "load";
    public static final String TERMINATION = "termination";
    public static final String TERMINATION_STRATEGY = "terminateStrategy";
    public static final String TICK_INTERVAL = "tickInterval";
    public static final String DURATION = "duration";
    public static final String MAX_DURATION = "maxDuration";
    public static final String MAX_THREAD_NUMBER = "maxThreadNumber";
    public static final String ITERATIONS = "iterations";
    public static final String DELAY = "delay";
    public static final String COUNT = "count";
    public static final String DESCRIPTION = "description";
    public static final String PAIR_SUPPLIER_FACTORY = "pairSupplierFactory";
    public static final String RANDOM_SEED = "randomSeed";
    public static final String PERIOD = "period";

    public static final String PLOT_DATA = "plotData";
    public static final String SAVE_SUMMARY = "saveSummary";
    public static final String SIMPLE_COLLECTOR = "simpleCollector";

    //default reporting names
    public static final String REPORTER_REGISTRY = "reporterExtensionRegistry";
    public static final String REPORTER_COMPARISON = "defaultOverallSessionComparisonReporter";
    public static final String REPORTER_BASELINE_PROVIDER = "DefaultBaselineSessionProvider";
    public static final String REPORTER_SESSION_COMPARATOR = "defaultSessionComparator";
    public static final String REPORTING_CONTEXT = "reportingContext";

    public static final String PROVIDER_REGISTRY = "providerRegistry";
    public static final String CONTEXT = "context";
    public static final String EXTENSIONS = "extensions";

    public static final String DEFAULT_TICK_INTERVAL = "${workload.tickinterval.default}";
    public static final String DEFAULT_MAX_THREAD_COUNT = "${workload.threads.maxcount}";

    public static final String DEFAULT_NAMESPACE = "http://www.griddynamics.com/schema/jagger";

    public static final String  DEFAULT_METRIC_NAME = "No name metric";
    public static final String  DEFAULT_METRIC_SUCCESS_RATE_NAME = "SR";

    public static final String MONITORING_SUT_CONFIGURATION = "monitoringSutConfiguration";
    public static final String JMX_METRIC_ATTRIBUTE = "jmx-metric-attribute";
    public static final String ATTRIBUTES = "attributes";
    public static final String JMX_METRIC_GROUPS = "jmxMetricGroups";
    public static final String JMX_METRICS = "jmx-metrics";
    public static final String START_DELAY_ATTRIBUTE = "startDelay";

    public static final String AGGREGATORS = "aggregators";
    public static final String NEED_PLOT_DATA = "plotData";
    public static final String NEED_SAVE_SUMMARY = "showSummary";
    public static final String METRIC_AGGREGATOR_PROVIDER = "metricAggregatorProvider";
    public static final String METRIC_DESCRIPTION = "metricDescription";
    public static final String AGGREGATORS_WITH_SETTINGS = "aggregatorsWithSettings";
    public static final String NORMALIZE_BY = "normalizeBy";
    public static final String POINT_COUNT = "pointCount";
    public static final String POINT_INTERVAL = "pointInterval";

    public static final String LIMITS = "limits";
    public static final String LIMIT = "limit";
    public static final String LIMIT_METRIC_NAME = "metricName";
    public static final String LIMIT_DESCRIPTION = "limitDescription";
    public static final String LIMIT_REFVALUE = "refValue";
    public static final String LIMIT_LWT_TAG = "LWT";
    public static final String LIMIT_UWT_TAG = "UWT";
    public static final String LIMIT_LET_TAG = "LET";
    public static final String LIMIT_UET_TAG = "UET";
    public static final String LIMIT_LWT_PROP = "lowerWarningThreshold";
    public static final String LIMIT_UWT_PROP = "upperWarningThreshold";
    public static final String LIMIT_LET_PROP = "lowerErrorThreshold";
    public static final String LIMIT_UET_PROP = "upperErrorThreshold";
}
