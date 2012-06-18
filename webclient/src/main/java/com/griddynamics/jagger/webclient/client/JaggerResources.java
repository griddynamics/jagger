package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/1/12
 */
public interface JaggerResources extends ClientBundle {
    public static final JaggerResources INSTANCE =  GWT.create(JaggerResources.class);

    @Source("ajax-loader.gif")
    ImageResource getLoadIndicator();
}
