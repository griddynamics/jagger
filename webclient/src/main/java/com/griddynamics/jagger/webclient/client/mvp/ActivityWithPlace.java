package com.griddynamics.jagger.webclient.client.mvp;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public abstract class ActivityWithPlace<P extends Place> extends AbstractActivity {

    private P place;

    protected P getPlace() {
        return place;
    }

    public Activity withPlace(P place) {
        this.place = place;

        return this;
    }
}
