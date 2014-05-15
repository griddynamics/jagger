package com.griddynamics.jagger.webclient.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.griddynamics.jagger.dbapi.model.NameTokens;
import com.griddynamics.jagger.webclient.client.mvp.JaggerActivityMapper;
import com.griddynamics.jagger.webclient.client.mvp.JaggerPlaceHistoryMapper;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.griddynamics.jagger.webclient.client.trends.TrendsPlace;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/28/12
 */
public class JaggerWebClient implements EntryPoint {

    public void onModuleLoad() {
        JaggerResources.INSTANCE.css().ensureInjected();

        // Initialize the history handler and activity manager
        EventBus eventBus = new SimpleEventBus();

        ActivityMapper activityMapper = new JaggerActivityMapper(JaggerResources.INSTANCE);
        PlaceHistoryMapper placeHistoryMapper = new JaggerPlaceHistoryMapper();

        PlaceController placeController = new PlaceController(eventBus);
        PlaceHistoryHandler placeHistoryHandler = new PlaceHistoryHandler(placeHistoryMapper);
        placeHistoryHandler.register(placeController, eventBus, new TrendsPlace(NameTokens.SUMMARY));

        MainView mainView = new MainView(eventBus, JaggerResources.INSTANCE);
        RootLayoutPanel.get().add(mainView);

        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(mainView.getContentContainer());

        placeHistoryHandler.handleCurrentHistory();
    }
}
