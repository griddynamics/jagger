package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceChangeEvent.Handler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.griddynamics.jagger.webclient.client.trends.TrendsPlace;
import com.griddynamics.jagger.webclient.client.viewresults.ViewResultsPlace;

import java.util.ArrayList;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class MainView extends ResizeComposite implements IsWidget, Handler {

    interface MainViewUiBinder extends UiBinder<Widget, MainView> {
    }

    private static MainViewUiBinder uiBinder = GWT.create(MainViewUiBinder.class);

    private static interface Predicate {
        boolean apply(Place place);
    }

    private static class Link {
        private Hyperlink link;

        private Predicate predicate;

        public Link(Hyperlink link, Predicate predicate) {
            this.link = link;
            this.predicate = predicate;
        }

        public boolean isPlaceMatchLink(Place place) {
            return predicate.apply(place);
        }

        public Hyperlink getLink() {
            return link;
        }
    }

    @UiField
    DeckLayoutPanel contentContainer;

    public MainView(EventBus eventBus, JaggerResources resources) {

        initWidget(uiBinder.createAndBindUi(this));

        eventBus.addHandler(PlaceChangeEvent.TYPE, this);
    }

    public AcceptsOneWidget getContentContainer() {
        return contentContainer;
    }

    @Override
    public void onPlaceChange(PlaceChangeEvent event) {
    }
}
