package com.griddynamics.jagger.webclient.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.griddynamics.jagger.webclient.client.trends.Trends;
import com.griddynamics.jagger.webclient.client.trends.TrendsPlace;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class JaggerActivityMapper implements ActivityMapper {
    JaggerResources resources;

    Trends trendsActivity;
    boolean was = false;

    public JaggerActivityMapper(JaggerResources resources) {
        this.resources = resources;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof TrendsPlace) {
            if (trendsActivity == null) {
                trendsActivity = new Trends(resources);
            }
            if (!was){
                trendsActivity.getPropertiesUpdatePlace((TrendsPlace)place);
                was = true;
            }
            return trendsActivity;
        }

        return null;
    }
}
