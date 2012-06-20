package com.griddynamics.jagger.webclient.client.mvp;

import com.google.gwt.place.shared.Place;
import com.griddynamics.jagger.webclient.client.trends.TrendsPlace;
import com.griddynamics.jagger.webclient.client.viewresults.ViewResultsPlace;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class JaggerPlaceHistoryMapper extends AbstractPlaceHistoryMapper {

    @Override
    protected Place getPlaceFromToken(String token) {
        // Add any new place here

        if (token.startsWith(NameTokens.TRENDS)) {
            return new TrendsPlace();
        } else if (token.startsWith(NameTokens.VIEW_RESULTS)) {
            return new ViewResultsPlace();
        }

        throw new UnsupportedOperationException("Token " + token + " is unsupported now");
    }

    @Override
    protected String getTokenFromPlace(Place place) {
        // Add any new place here

        if (place instanceof TrendsPlace) {
            return NameTokens.TRENDS;
        } else if (place instanceof ViewResultsPlace) {
            return NameTokens.VIEW_RESULTS;
        }

        throw new UnsupportedOperationException("Place " + place + " is unsupported now");
    }
}
