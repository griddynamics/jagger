package com.griddynamics.jagger.webclient.client.trends;

import com.griddynamics.jagger.webclient.client.dto.TestsMetrics;
import com.griddynamics.jagger.webclient.client.mvp.AbstractPlaceHistoryMapper;
import com.griddynamics.jagger.webclient.client.mvp.PlaceWithParameters;

import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class TrendsPlace extends PlaceWithParameters {

    private static final String PARAM_TESTS = "tests";
    private static final String PARAM_SESSIONS = "sessions";

    private String token;
    private Set<String> selectedSessionIds = Collections.EMPTY_SET;
    private Set<TestsMetrics> selectedTestsMetrics = Collections.EMPTY_SET;

    public TrendsPlace(String token){
        this.token = token;
    }

    public void setSelectedSessionIds(Set<String> selectedSessionIds){
        this.selectedSessionIds = selectedSessionIds;
    }

    public void setSelectedTestsMetrics(Set<TestsMetrics> selectedTestsMetrics){
        this.selectedTestsMetrics = selectedTestsMetrics;
    }

    public Set<String> getSelectedSessionIds() {
        return selectedSessionIds;
    }

    public Set<TestsMetrics> getSelectedTestsMetrics() {
        return selectedTestsMetrics;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public Map<String, Set<String>> getParameters() {

        HashMap<String, Set<String>> parameters = new HashMap<String, Set<String>>();

        if (!selectedSessionIds.isEmpty()){
            parameters.put(PARAM_SESSIONS, selectedSessionIds);
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

    @Override
    public void setParameters(Map<String, Set<String>> parameters) {
        selectedTestsMetrics = new HashSet<TestsMetrics>();
        selectedSessionIds = new HashSet<String>();
        if (parameters != null && !parameters.isEmpty()) {

            Set<String> sessions =  parameters.get(PARAM_SESSIONS);
            if (sessions!=null && !sessions.isEmpty()){
                selectedSessionIds = sessions;
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
}
