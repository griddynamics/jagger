package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.master.configuration.SessionExecutionListener;

import java.util.*;

public class MetricsProvider {

    private final static String BASICS = "basics";

    // TODO make configurable
    private Map<String, Collection<SessionExecutionListener>> sessionListeners;

    public Iterable<SessionExecutionListener> getSessionListeners(Collection<String> metrics) {
        List<SessionExecutionListener> result = new ArrayList<SessionExecutionListener>();
        for (String metric: metrics) {
            if (sessionListeners.containsKey(metric)) {
                result.addAll(sessionListeners.get(metric));
            }
        }
        return result;
    }
}
