package com.griddynamics.jagger.engine.e1.services;

/** Internal Jagger service that can be used in custom components
 * @author Kirill Gribov
 * @n
 * @par Details:
 * @details Main goal of Jagger service implementations - give user easy access to Jagger internal components from custom code @n
 * @n
 * To view all Jagger services implementations click here @ref Main_Services_group
 *
 * @ingroup Main_Services_group */
public interface JaggerService {

    /** Reports if service is available
     * @author Kirill Gribov
     * @n
     * @par Details:
     * @details
     * @n
     *
     * @return true if service is available */
    boolean isAvailable();
}
