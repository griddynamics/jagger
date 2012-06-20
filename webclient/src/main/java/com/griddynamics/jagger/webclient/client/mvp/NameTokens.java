package com.griddynamics.jagger.webclient.client.mvp;

/**
 * Class containing all the tokens of the application. Useful to reference them in one place and use them in uibinder
 *
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class NameTokens {

    protected NameTokens() {
    }

    public static final String TRENDS = "trends";

    public static final String VIEW_RESULTS = "view-results";

    public static String trends() {
        return TRENDS;
    }

    public static String viewResults() {
        return VIEW_RESULTS;
    }
}
