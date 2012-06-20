package com.griddynamics.jagger.webclient.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/1/12
 */
public interface JaggerResources extends ClientBundle {
    public static final JaggerResources INSTANCE = GWT.create(JaggerResources.class);

    @Source("ajax-loader.gif")
    ImageResource getLoadIndicator();

    @Source("JaggerWebClient.css")
    @CssResource.NotStrict
    JaggerStyle css();

    public interface JaggerStyle extends CssResource {
        String currentItem();

        String infoPanel();

        String centered();

        String taskDetailsTree();

        String xAxisLabel();

        String plotHeader();

        String plotLegend();

        String plotPanel();

        String zoomLabel();

        String zoomPanel();

        String mainPanel();

        String navPanel();

        String contentPanel();
    }
}
