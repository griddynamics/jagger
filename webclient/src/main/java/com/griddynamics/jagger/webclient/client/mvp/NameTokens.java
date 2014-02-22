package com.griddynamics.jagger.webclient.client.mvp;

/**
 * Class containing all the tokens of the application. Useful to reference them in one place and use them in uibinder
 *
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class NameTokens {

    protected NameTokens() {
    }

    public static final String SUMMARY = "summary";
    public static final String TRENDS = "trends";
    public static final String METRICS = "metrics";
    public static final String NODES = "nodes";

    public static final String CONTROL_SUMMARY_TRENDS = "Summary & Trends";
    public static final String CONTROL_METRICS = "Metrics";
    public static final String SESSION_INFO = "Session Info";
    public static final String TEST_INFO = "Test Info";
    public static final String SESSION_SCOPE_PLOTS = "Session Scope Plots";

    // used to understand if test from summary subtree or from metrics subtree
    public static final String SUMMARY_PREFIX = "sum-";
    public static final String METRICS_PREFIX = "met-";

    public static final String MONITORING_PREFIX = "mon-";

    /**
     * used to separate monitoring plot name and agent id in PolNameDto.plotName/SessionNameDto.plotName
     * note: '|' == '%7C' in while link processing
     */
    public static final String AGENT_NAME_SEPARATOR = "|";

    public static String EMPTY = "";

    public static String summary() {
        return SUMMARY;
    }

    public static String trends() {
        return TRENDS;
    }
}
