package com.griddynamics.jagger.engine.e1.collector;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/7/13
 * Time: 9:21 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AbstractMetricCalculator<R, C extends Number> extends Serializable{
    C calculate(R response);
}
