package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.engine.e1.scenario.Flushable;
import com.griddynamics.jagger.engine.e1.scenario.NodeSideInitializable;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/30/13
 * Time: 8:35 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Metric extends NodeSideInitializable, Flushable{
    String getId();
    void save(long timeStamp, Number value);
    void save(Number value);
}
