package com.griddynamics.jagger.webclient.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public abstract class AbstractPlaceHistoryMapper implements PlaceHistoryMapper {

    protected static final String SEPARATOR_TOKEN_PARAMETERS = ":";

    protected static final String SEPARATOR_PARAMETERS = "&";

    protected static final String SEPARATOR_PARAMETER_ID_VALUE = "=";

    @Override
    public Place getPlace(String token) {
        if (null == token || "".equals(token)) {
            // it will show the default place
            return null;
        }

        Place place = getPlaceFromToken(token);

        if (null == place) {
            GWT.log("No corresponding place found for the token " + token);
            return null;
        }

        // if the place has parameters, retrieving them from token
        if (place instanceof PlaceWithParameters) {
            int index = token.indexOf(SEPARATOR_TOKEN_PARAMETERS);
            if (index != -1) {
                String[] parameters = token.substring(index + 1).split(SEPARATOR_PARAMETERS);
                Map<String, Set<String>> mapParameters = new HashMap<String, Set<String>>();
                for (String parameter : parameters) {
                    String[] paramIdValue = parameter.split(SEPARATOR_PARAMETER_ID_VALUE);
                    if (!mapParameters.containsKey(paramIdValue[0])) {
                        mapParameters.put(paramIdValue[0], new HashSet<String>());
                    }
                    mapParameters.get(paramIdValue[0]).add(paramIdValue[1]);
                }
                ((PlaceWithParameters) place).setParameters(mapParameters);
            }
        }

        return place;
    }

    @Override
    public String getToken(Place place) {
        String token = getTokenFromPlace(place);

        if (place instanceof PlaceWithParameters) {
            Map<String, Set<String>> parameters = ((PlaceWithParameters) place).getParameters();
            if (null != parameters && !parameters.isEmpty()) {
                StringBuilder tokenBuilder = new StringBuilder(token);
                tokenBuilder.append(SEPARATOR_TOKEN_PARAMETERS);
                boolean first = true;
                for (Map.Entry<String, Set<String>> parameter : parameters.entrySet()) {

                    for (String paramValue : parameter.getValue()) {
                        if (!first) {
                            tokenBuilder.append(SEPARATOR_PARAMETERS);
                        }
                        tokenBuilder.append(parameter.getKey());
                        tokenBuilder.append(SEPARATOR_PARAMETER_ID_VALUE);
                        tokenBuilder.append(paramValue);
                        first = false;
                    }
                }
                token = tokenBuilder.toString();
            }
        }
        return token;
    }

    /**
     * Find a place corresponding to the token given in parameters.
     *
     * @param token a String token
     * @return the place corresponding to the token, throw UnsupportedOperationException if none found
     */
    protected abstract Place getPlaceFromToken(String token);

    /**
     * Find a string token corresponding to the place given in parameters.
     *
     * @param place a place
     * @return the token corresponding to the place, throw UnsupportedOperationException if none found
     */
    protected abstract String getTokenFromPlace(Place place);
}
