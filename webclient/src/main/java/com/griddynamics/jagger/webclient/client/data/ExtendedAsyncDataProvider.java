package com.griddynamics.jagger.webclient.client.data;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/22/12
 */
public abstract class ExtendedAsyncDataProvider<T> extends AsyncDataProvider<T> {

    protected ExtendedAsyncDataProvider() {
    }

    protected ExtendedAsyncDataProvider(ProvidesKey<T> keyProvider) {
        super(keyProvider);
    }

    protected void update() {
        for (HasData<T> display : getDataDisplays()) {
            onRangeChanged(display);
        }
    }

    public void addDataDisplayIfNotExists(final HasData<T> display) {
        if (!getDataDisplays().contains(display)) {
            addDataDisplay(display);
        }
    }

    public void removeDataDisplayIfNotExists(final HasData<T> display) {
        if (getDataDisplays().contains(display)) {
            removeDataDisplay(display);
        }
    }
}
