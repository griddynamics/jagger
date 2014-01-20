package com.griddynamics.jagger.engine.e1.services;

/** Internal Jagger service that can be used in custom components
 * @author Kirill Gribov
 * @n
 * @par Details:
 * @details Main goal of Jagger service implementations - give user easy access to Jagger internal components from custom code @n
 * @n
 * To view all Jagger services implementations click here @ref Main_Services_group
 *
 * @ingroup Main_Services_Base_group */
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

/* **************** Jagger services page ************************* */
/// @defgroup Main_Services_General_group Jagger services main page
///
/// @li General information about interfaces: @ref Main_Services_Base_group
/// @li Available implementations: @ref Main_Services_group
/// @li Where services can be used: @ref Main_ListenersAndServices_group
/// @n
/// @n
/// @details
/// @par General info
/// Main goal of Jagger services - give user easy access to Jagger internal components from custom code @n
/// You can find example of services usage in custom listeners under link: @ref Main_CustomListenersExamples_group
///