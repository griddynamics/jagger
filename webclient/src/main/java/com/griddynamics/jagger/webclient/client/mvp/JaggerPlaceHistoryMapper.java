package com.griddynamics.jagger.webclient.client.mvp;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.griddynamics.jagger.dbapi.model.NameTokens;
import com.griddynamics.jagger.webclient.client.trends.TrendsPlace;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class JaggerPlaceHistoryMapper extends AbstractPlaceHistoryMapper {

    @Override
    protected Place getPlaceFromToken(String token) {
        // Add any new place here

        TrendsPlace place = null;

        if (token.startsWith(NameTokens.SUMMARY)) {
            place = new TrendsPlace(NameTokens.SUMMARY);
        } else if (token.startsWith(NameTokens.TRENDS)) {
            place = new TrendsPlace(NameTokens.TRENDS);
        } else if (token.startsWith(NameTokens.METRICS)) {
            place = new TrendsPlace(NameTokens.METRICS);
        }

        if (place != null) {
            place.setUrl(Window.Location.getHost() + Window.Location.getPath() + "#" + token);
            return place;
        }

        throw new UnsupportedOperationException("Token " + token + " is unsupported now");
    }

    @Override
    protected String getTokenFromPlace(Place place) {
        // Add any new place here

        if (place instanceof TrendsPlace) {
            TrendsPlace trendsPlace = (TrendsPlace)place;
            return trendsPlace.getToken();
        }

        throw new UnsupportedOperationException("Place " + place + " is unsupported now");
    }
}
