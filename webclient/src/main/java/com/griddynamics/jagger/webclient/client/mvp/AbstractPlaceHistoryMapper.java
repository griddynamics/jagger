package com.griddynamics.jagger.webclient.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;

import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public abstract class AbstractPlaceHistoryMapper implements PlaceHistoryMapper {

    protected static final String SEPARATOR_TOKEN_PARAMETERS = "?";

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
                ((PlaceWithParameters) place).setParameters(getParameters(token.substring(index+1)));
            }
        }

        return place;
    }

    public static Map<String, Set<String>> getParameters(String line){
        Map<String, Set<String>> mapParameters = new HashMap<String, Set<String>>();

        String[] parameters = complexSplit(line, '&');

        for (String parameter : parameters) {
            String[] paramIdValue = complexSplit(parameter, '=');
            if (!mapParameters.containsKey(paramIdValue[0])) {
                String[] values = complexSplit(paramIdValue[1], ',');
                if (values.length > 0){
                    HashSet<String> set = new HashSet<String>(Arrays.asList(values));
                    mapParameters.put(paramIdValue[0], set);
                }
            }
        }

        return mapParameters;
    }

    public static String[] complexSplit(String line, char separator){
        ArrayList<String> elements = new ArrayList<String>();
        int startPos = 0;
        int bracketCount = 0;
        for (int i=0; i<line.length(); i++){
            char current = line.charAt(i);
            if (current == '('){
                bracketCount++;
                i++;
                while (bracketCount!=0 && i<line.length()){
                    if (line.charAt(i)=='('){
                        bracketCount++;
                    }
                    if (line.charAt(i)==')'){
                        bracketCount--;
                    }
                    i++;
                }
            }
            if (i<line.length() && line.charAt(i) == separator){
                elements.add(line.substring(startPos, i));
                startPos = i+1;
            }
        }

        elements.add(line.substring(startPos, line.length()));

        return elements.toArray(new String[]{});
    }

    @Override
    public String getToken(Place place) {
        String token = getTokenFromPlace(place);

        if (place instanceof PlaceWithParameters) {
            Map<String, Set<String>> parameters = ((PlaceWithParameters) place).getParameters();
            if (null != parameters && !parameters.isEmpty()) {
                StringBuilder tokenBuilder = new StringBuilder(token);
                tokenBuilder.append(SEPARATOR_TOKEN_PARAMETERS);
                for (Map.Entry<String, Set<String>> parameter : parameters.entrySet()) {

                    tokenBuilder.append(parameter.getKey());
                    tokenBuilder.append(SEPARATOR_PARAMETER_ID_VALUE);

                    StringBuilder paramBuilder = new StringBuilder();
                    for (String paramValue : parameter.getValue()) {
                        paramBuilder.append(paramValue);
                        paramBuilder.append(',');
                    }
                    tokenBuilder.append(paramBuilder.toString().substring(0, paramBuilder.length()-1));
                    tokenBuilder.append(SEPARATOR_PARAMETERS);
                }
                token = tokenBuilder.toString().substring(0, tokenBuilder.length()-1);
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
