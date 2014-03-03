package com.griddynamics.jagger.webclient.client.trends;

import com.griddynamics.jagger.webclient.client.dto.TestsMetrics;
import com.griddynamics.jagger.webclient.client.mvp.AbstractPlaceHistoryMapper;

import java.util.*;

/**
 * User: amikryukov
 * Date: 2/14/14
 */
public class LinkFragment {

    private static final String PARAM_TESTS = "tests";
    private static final String PARAM_SESSIONS = "sessions";

    private Set<String> selectedSessionIds = Collections.EMPTY_SET;
    private Set<TestsMetrics> selectedTestsMetrics = Collections.EMPTY_SET;
    private Set<String> sessionTrends = Collections.EMPTY_SET;

    public Set<String> getSelectedSessionIds() {
        return selectedSessionIds;
    }

    public void setSelectedSessionIds(Set<String> selectedSessionIds) {
        this.selectedSessionIds = selectedSessionIds;
    }

    public Set<TestsMetrics> getSelectedTestsMetrics() {
        return selectedTestsMetrics;
    }

    public void setSelectedTestsMetrics(Set<TestsMetrics> selectedTestsMetrics) {
        this.selectedTestsMetrics = selectedTestsMetrics;
    }

    public Set<String> getSessionTrends() {
        return sessionTrends;
    }

    public void setSessionTrends(Set<String> sessionTrends) {
        this.sessionTrends = sessionTrends;
    }


    public Map<String, Set<String>> getParameters() {

        HashMap<String, Set<String>> parameters = new HashMap<String, Set<String>>();

        if (!selectedSessionIds.isEmpty()){

            StringBuilder sessionsBuilder = new StringBuilder("ids=");
            for (String session : selectedSessionIds){
                sessionsBuilder.append(session+",");
            }
            String  sessions = sessionsBuilder.toString().substring(0, sessionsBuilder.length()-1);

            String trends = "";
            if (!sessionTrends.isEmpty()){
                StringBuilder trendsBuilder = new StringBuilder("&trends=");
                for (String trend : sessionTrends){
                    trendsBuilder.append(trend+",");
                }
                trends = trendsBuilder.toString().substring(0, trendsBuilder.length()-1);
            }

            parameters.put(PARAM_SESSIONS, new HashSet<String>(Arrays.asList("("+sessions+trends+")")));
        }else{
            return Collections.EMPTY_MAP;
        }

        HashSet<String> testMetrics = new HashSet<String>();
        for (TestsMetrics testsMetric : selectedTestsMetrics){

            StringBuilder builder = new StringBuilder();
            String metricsString = "";
            String trendsString = "";

            if (!testsMetric.getMetrics().isEmpty()){
                builder = new StringBuilder();
                for (String metricString : testsMetric.getMetrics()){
                    builder.append(metricString);
                    builder.append(',');
                }
                metricsString = builder.toString().substring(0, builder.length()-1);
                metricsString = "&metrics=" + metricsString;
            }

            if (!testsMetric.getTrends().isEmpty()){
                builder = new StringBuilder();
                for (String trendString : testsMetric.getTrends()){
                    builder.append(trendString);
                    builder.append(',');
                }
                trendsString = builder.toString().substring(0, builder.length()-1);
                trendsString = "&trends=" + trendsString;
            }

            testMetrics.add("("+"name="+testsMetric.getTestName()+metricsString+trendsString+")");
        }
        if (!testMetrics.isEmpty()){
            parameters.put(PARAM_TESTS, testMetrics);
        }

        return parameters;
    }

    public void setParameters(Map<String, Set<String>> parameters) {
        selectedTestsMetrics = new HashSet<TestsMetrics>();
        selectedSessionIds = new HashSet<String>();
        if (parameters != null && !parameters.isEmpty()) {

            Set<String> sessionGroups =  parameters.get(PARAM_SESSIONS);
            if (sessionGroups!=null && !sessionGroups.isEmpty()){
                String group = sessionGroups.iterator().next();

                String temp = group.substring(1, group.length()-1);

                Map<String, Set<String>> idsAndTrends = AbstractPlaceHistoryMapper.getParameters(temp);

                Set<String> sessions = idsAndTrends.get("ids");
                sessions = sessions!=null ? sessions : Collections.EMPTY_SET;

                Set<String> trends = idsAndTrends.get("trends");
                trends = trends!=null ? trends : Collections.EMPTY_SET;

                selectedSessionIds = sessions;
                sessionTrends = trends;
            }

            Set<String> groups = parameters.get(PARAM_TESTS);
            if (groups!=null && !groups.isEmpty()){
                for (String group : groups){
                    String temp = group.substring(1, group.length()-1);
                    Map<String, Set<String>> metricsAndTests = AbstractPlaceHistoryMapper.getParameters(temp);

                    Set<String> testsNames = metricsAndTests.get("name");
                    testsNames = testsNames!=null ? testsNames : Collections.EMPTY_SET;

                    Set<String> metrics = metricsAndTests.get("metrics");
                    metrics = metrics!=null ? metrics : Collections.EMPTY_SET;

                    Set<String> trends = metricsAndTests.get("trends");
                    trends = trends!=null ? trends : Collections.EMPTY_SET;

                    selectedTestsMetrics.add(new TestsMetrics(testsNames.iterator().next(), metrics, trends));
                }
            }
        }

    }

    public String getParametersAsString() {
        Map<String, Set<String>> parameters = getParameters();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : parameters.entrySet()) {
            sb.append(entry.getKey()).append('=');
            for (String string : entry.getValue()) {
                sb.append(string).append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append('&');
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
