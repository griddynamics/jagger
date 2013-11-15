package com.griddynamics.jagger.engine.e1;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 11/7/13
 * Time: 9:43 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Provider<T> {
    T provide();
}
