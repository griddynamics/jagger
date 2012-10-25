package com.griddynamics.jagger.util;

import java.util.HashMap;
import java.util.Map;

/**
 * User: vshulga
 * Date: 10/20/12
 */
public class ThreadLocalMetrics {

    public static final String METRIC_DURATION_MARKER = "metric_duration_";
    public static final String METRIC_MARKER = "metric_";

    private static ThreadLocal<Map<String, Integer>> metrics = new ThreadLocal<Map<String, Integer>>() {
        @Override
        protected Map<String, Integer> initialValue() {
            return new HashMap<String, Integer>();
        }
    };

    public static Map<String, Integer> listLike(String startPattern){
        Map<String, Integer> found = new HashMap<String, Integer>();
        Map<String, Integer> metricsMap = metrics.get();
        for(Map.Entry<String, Integer> entry : metricsMap.entrySet()) {
            if (entry.getKey().startsWith(startPattern)) {
                found.put(entry.getKey(), entry.getValue());
            }
        }

        return found;
    }

    public static Integer getMetric(String key) {
        return metrics.get().get(key);
    }

    public static void addMetric(String key, Integer value) {
        metrics.get().put(key, value);
    }

}
