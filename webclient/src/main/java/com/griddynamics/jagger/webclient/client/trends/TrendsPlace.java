package com.griddynamics.jagger.webclient.client.trends;

import com.griddynamics.jagger.webclient.client.mvp.PlaceWithParameters;

import java.util.Collections;
import java.util.Map;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class TrendsPlace extends PlaceWithParameters {

    @Override
    public Map<String, String> getParameters() {
        return Collections.emptyMap();
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
    }
}
