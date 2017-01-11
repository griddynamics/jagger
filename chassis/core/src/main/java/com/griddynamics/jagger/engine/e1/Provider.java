package com.griddynamics.jagger.engine.e1;

import java.io.Serializable;

/** An object that provides new elements(test listeners, test-group listeners, etc) for jagger purposes
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 *
 * @param <T> - type of element, that will be provided
 * */
public interface Provider<T> extends Serializable {

    /** Provides new object
     *@return new T instance*/
    T provide();
}
