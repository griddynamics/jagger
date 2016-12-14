package com.griddynamics.jagger.webclient.client.resources;

import com.google.gwt.user.cellview.client.SimplePager;

/**
 * User: amikryukov
 * Date: 12/9/13
 */
public interface SessionPagerResources extends SimplePager.Resources {

    /**
     * override styles
     */
    @Override
    @Source("SessionPager.css")
    SimplePager.Style simplePagerStyle();


}
