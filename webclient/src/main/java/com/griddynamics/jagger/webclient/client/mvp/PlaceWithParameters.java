package com.griddynamics.jagger.webclient.client.mvp;

import com.google.gwt.place.shared.Place;

import java.util.Map;

/**
 * Places with parameters should extend this class
 *
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public abstract class PlaceWithParameters extends Place {

    /**
     * Return the place's parameters in their string representation
     *
     * @return the place's parameters in their string representation
     */
    public abstract Map<String, String> getParameters();

    /**
     * Give to the place their parameters in their string representation
     *
     * @param parameters parameters in their string representation
     */
    public abstract void setParameters( Map<String, String> parameters );
}
