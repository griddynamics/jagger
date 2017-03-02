package com.griddynamics.jagger.engine.e1.reporting;

import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.extractDisplayNameFromGenerated;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.extractIdsFromGeneratedIdForScenarioComponents;

import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Andrey Badaev
 * Date: 16/02/17
 */
public class ScenarioSummaryExtractor {
    
    private final static String STEP_PREFIX = "  Step: ";
    private final static Comparator<SummaryDto> STEP_METRICS_COMPARATOR = (o1, o2) -> {
        if (o2.getKey().startsWith(STEP_PREFIX)) {
            return 1;
        }
        return o1.getKey().compareTo(o2.getKey());
    };
    
    private final static String SCENARIO_PREFIX = "Scenario: ";
    private final static Comparator<SummaryDto> SCENARIO_METRICS_COMPARATOR = (o1, o2) -> {
        if (o2.getKey().startsWith(SCENARIO_PREFIX)) {
            return 1;
        }
        return o1.getKey().compareTo(o2.getKey());
    };
    
    private static class StepData {
        String displayName;
        final List<SummaryDto> metrics = new ArrayList<>();
    }
    private final static class ScenarioData extends StepData {
        final Map<String, StepData> stepMetrics = new HashMap<>();
    }
    
    
    public static List<SummaryDto> extractScenarioSummary(Map<String, SummaryDto> summaries) {
        final List<SummaryDto> result = new ArrayList<>();
        final Map<String, ScenarioData> scenarioDataMap = new HashMap<>();
    
        for (Map.Entry<String, SummaryDto> entry: summaries.entrySet()) {
            final SummaryDto metric = entry.getValue();
            if (!StandardMetricsNamesUtil.isBelongingToScenario(entry.getKey())) {
                result.add(metric);
                continue;
            }
        
            StandardMetricsNamesUtil.IdContainer ids = extractIdsFromGeneratedIdForScenarioComponents(entry.getKey());
            if (ids == null) {
                result.add(metric);
                continue;
            }
        
            ScenarioData scenarioData = scenarioDataMap.computeIfAbsent(ids.getScenarioId(), k -> new ScenarioData());
            if (!ids.getScenarioId().equals(ids.getStepId())) { // if it is a step metric
                StepData stepData =
                        scenarioData.stepMetrics.computeIfAbsent(ids.getStepId(), s -> new StepData());
                addDisplayNameEntry(metric, stepData, STEP_PREFIX);
                metric.setKey("    " + metric.getKey());
                stepData.metrics.add(metric);
            } else { // then it is a scenario metric
                addDisplayNameEntry(metric, scenarioData, SCENARIO_PREFIX);
                metric.setKey("  " + metric.getKey());
                scenarioData.metrics.add(metric);
            }
        }
    
        result.sort(Comparator.comparing(SummaryDto::getKey));
    
        result.addAll(scenarioDataMap.entrySet()
                                     .stream()
                                     .sorted(Comparator.comparing(se -> se.getValue().displayName))
                                     .flatMap(e -> {
                                         List<SummaryDto> allScenarioMetrics = e.getValue().metrics.stream()
                                                                                                   .sorted(SCENARIO_METRICS_COMPARATOR)
                                                                                                   .collect(Collectors.toList());
                                         List<SummaryDto> allStepsMetrics = e.getValue().stepMetrics.entrySet()
                                                                                                    .stream()
                                                                                                    .sorted(Comparator.comparing(
                                                                                                            ste -> ste.getValue().displayName))
                                                                                                    .flatMap(e2 -> e2.getValue().metrics
                                                                                                            .stream()
                                                                                                            .sorted(STEP_METRICS_COMPARATOR))
                                                                                                    .collect(Collectors.toList());
                                         allScenarioMetrics.addAll(allStepsMetrics);
                                         return allScenarioMetrics.stream();
                                     })
                                     .collect(Collectors.toList()));
    
        return result;
    }
    
    private static void addDisplayNameEntry(SummaryDto metric, StepData stepData, String prefix) {
        if (stepData.displayName == null) { // add an entry with step display name
            String displayName = extractDisplayNameFromGenerated(metric.getKey());
            stepData.displayName = displayName;
            SummaryDto summaryDto = new SummaryDto();
            summaryDto.setKey(prefix + displayName);
            summaryDto.setValue("");
            stepData.metrics.add(summaryDto);
        }
    }
}
