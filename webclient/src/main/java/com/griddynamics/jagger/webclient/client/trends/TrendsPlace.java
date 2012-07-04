package com.griddynamics.jagger.webclient.client.trends;

import com.griddynamics.jagger.webclient.client.mvp.PlaceWithParameters;

import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class TrendsPlace extends PlaceWithParameters {

    private static final String PARAM_SESSION_ID = "sid";

    private Set<String> sessionIds;

    @Override
    public Map<String, Set<String>> getParameters() {
        if (sessionIds == null) {
            return null;
        }

        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        map.put(PARAM_SESSION_ID, sessionIds);

        return map;
    }

    @Override
    public void setParameters(Map<String, Set<String>> parameters) {
        if (parameters != null && !parameters.isEmpty()) {
            sessionIds = parameters.get(PARAM_SESSION_ID);
        }
    }

    public Set<String> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(Set<String> sessionIds) {
        this.sessionIds = sessionIds;
    }
}
