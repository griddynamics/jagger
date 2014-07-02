/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.engine.e1.reporting;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.util.FormatCalculator;
import com.griddynamics.jagger.util.MetricNamesRankingProvider;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.springframework.beans.factory.annotation.Required;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SummaryReporter {
    private DatabaseService databaseService;
    private String sessionId;
    private Map<TestEntity, Set<MetricEntity>> metricsPerTest;
    private Map<String, List<SummaryDto>> summaryMap = new HashMap<String, List<SummaryDto>>();
    private Map<String, List<SummaryDto>> latencyPercentilesMap = new HashMap<String, List<SummaryDto>>();
    private Map<String, List<SummaryDto>> validatorsMap = new HashMap<String, List<SummaryDto>>();
    private Map<TestEntity, Map<MetricEntity, MetricSummaryValueEntity>> standardMetricsMap = new HashMap<TestEntity, Map<MetricEntity, MetricSummaryValueEntity>>();
    private DateFormat dateFormatter = new SimpleDateFormat(FormatCalculator.DATE_FORMAT);
    private boolean isMetricHighlighting;

    @Required
    public void setMetricHighlighting(boolean isMetricHighlighting) {
        this.isMetricHighlighting = isMetricHighlighting;
    }

    public List<SummaryDto> getSummary(String sessionId, String taskId) {

        getData(sessionId);

        if (summaryMap.containsKey(taskId)) {
            return summaryMap.get(taskId);
        } else {
            return null;
        }
    }

    public List<SummaryDto> getValidators(String sessionId, String taskId) {

        getData(sessionId);

        if (validatorsMap.containsKey(taskId)) {
            return validatorsMap.get(taskId);
        } else {
            return null;
        }
    }

    public List<SummaryDto> getLatencyPercentile(String sessionId, String taskId) {

        getData(sessionId);

        if (latencyPercentilesMap.containsKey(taskId)) {
            return latencyPercentilesMap.get(taskId);
        } else {
            return null;
        }
    }

    public Map<TestEntity, Map<MetricEntity, MetricSummaryValueEntity>> getStandardMetricsPerTest(String sessionId) {

        getData(sessionId);

        return standardMetricsMap;
    }

    private void getData(String sessionId) {

        // Remember what session id was set for cashed data
        if (this.sessionId == null) {
            this.sessionId = sessionId;
        }

        // Reset data if new session id arrived
        if (!sessionId.equals(this.sessionId)) {
            metricsPerTest = null;
            this.sessionId = sessionId;
        }

        if (metricsPerTest == null) {

            Set<String> standardMetricsIds = new HashSet<String>();
            standardMetricsIds.add(StandardMetricsNamesUtil.THROUGHPUT_ID);
            standardMetricsIds.add(StandardMetricsNamesUtil.FAIL_COUNT_ID);
            standardMetricsIds.add(StandardMetricsNamesUtil.SUCCESS_RATE_ID);
            standardMetricsIds.add(StandardMetricsNamesUtil.LATENCY_ID);
            standardMetricsIds.add(StandardMetricsNamesUtil.LATENCY_STD_DEV_ID);

            LocalRankingProvider localRankingProvider = new LocalRankingProvider();
            DataService dataService = new DefaultDataService(databaseService);
            Set<TestEntity> testEntities = dataService.getTests(sessionId);
            metricsPerTest = dataService.getMetricsByTests(testEntities);

            for (Map.Entry<TestEntity, Set<MetricEntity>> entry : metricsPerTest.entrySet()) {
                List<SummaryDto> summaryList = new ArrayList<SummaryDto>();
                List<SummaryDto> latencyPercentilesList = new ArrayList<SummaryDto>();
                List<SummaryDto> validatorsList = new ArrayList<SummaryDto>();
                Map<MetricEntity, MetricSummaryValueEntity> standardMetricsPerTest = new HashMap<MetricEntity, MetricSummaryValueEntity>();

                // Metrics
                Map<MetricEntity, MetricSummaryValueEntity> summary = dataService.getMetricSummary(entry.getValue());

                for (MetricEntity metricEntity : summary.keySet()) {
                    SummaryDto value = new SummaryDto();

                    // All summary
                    value.setKey(metricEntity.getDisplayName());

                    MetricSummaryValueEntity metricSummaryValueEntity = summary.get(metricEntity);

                    Double summaryValue = metricSummaryValueEntity.getValue();
                    value.setValue(new DecimalFormat(FormatCalculator.getNumberFormat(summaryValue)).format(summaryValue));
                    if (isMetricHighlighting && metricSummaryValueEntity.getDecision() != null) {
                        value.setDecision(metricSummaryValueEntity.getDecision().toString());
                    }

                    // Validators
                    if (metricEntity.getMetricNameDto().getOrigin().equals(MetricNameDto.Origin.VALIDATOR)) {
                        validatorsList.add(value);
                    }

                    // Latency percentiles
                    if (metricEntity.getMetricId().matches("^" + StandardMetricsNamesUtil.LATENCY_PERCENTILE_REGEX)) {
                        // change key (name) for back compatibility
                        value.setKey(metricEntity.getDisplayName().replace("Latency ", "").concat("  -  "));
                        latencyPercentilesList.add(value);
                    } else {
                        summaryList.add(value);
                    }

                    // Standard metrics
                    if (standardMetricsIds.contains(metricEntity.getMetricId())) {
                        standardMetricsPerTest.put(metricEntity, summary.get(metricEntity));
                    }
                }

                localRankingProvider.sortSummaryDto(summaryList);
                localRankingProvider.sortSummaryDto(validatorsList);
                localRankingProvider.sortSummaryDto(latencyPercentilesList);

                // Test info
                SummaryDto description = new SummaryDto();
                description.setKey("Test description");
                description.setValue(entry.getKey().getDescription());
                summaryList.add(0, description);

                SummaryDto startTime = new SummaryDto();
                startTime.setKey("Start time");
                startTime.setValue(dateFormatter.format(entry.getKey().getStartDate()));
                summaryList.add(0, startTime);

                SummaryDto termination = new SummaryDto();
                termination.setKey("Termination");
                termination.setValue(entry.getKey().getTerminationStrategy());
                summaryList.add(0, termination);

                SummaryDto load = new SummaryDto();
                load.setKey("Load");
                load.setValue(entry.getKey().getLoad());
                summaryList.add(0, load);

                summaryMap.put(entry.getKey().getId().toString(), summaryList);
                latencyPercentilesMap.put(entry.getKey().getId().toString(), latencyPercentilesList);
                validatorsMap.put(entry.getKey().getId().toString(), validatorsList);
                standardMetricsMap.put(entry.getKey(), standardMetricsPerTest);
            }
        }
    }

    private class LocalRankingProvider extends MetricNamesRankingProvider {
        public void sortSummaryDto(List<SummaryDto> list) {
            Collections.sort(list, new Comparator<SummaryDto>() {
                @Override
                public int compare(SummaryDto o, SummaryDto o2) {
                    return LocalRankingProvider.compare(o.getKey(), o2.getKey());
                }
            });
        }
    }

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

}
