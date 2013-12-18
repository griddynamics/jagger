package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.NodeSideInitializable;
import com.griddynamics.jagger.engine.e1.Provider;

/** Abstract type of Provider, that gives user an access to Jagger services
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details If you would like to provide new objects and have an access to jagger services - extend this class.
 * @n
 *
 * @param <T> - type of element, that will be provided
 * */
public abstract class AbstractServicesAwareProvider<T> implements NodeSideInitializable, Provider<T> {

    private MetricService metricService;

    /** Returns MetricService
     * @author Gribov Kirill
     * @n
     * @par Details:
     * @details Returns metric service for current test
     *@return metric service */
    protected MetricService getMetricService(){
        return metricService;
    }

    public final void init(String sessionId, String taskId, NodeContext context){
        metricService = new DefaultMetricService(sessionId, taskId, context);

        init();
    }

    /** User action, that will be executed before at least one object will be provided.
     * @author Gribov Kirill
     * @n
     * @par Details: If you would like to execute some actions, before objects will be provided, override this method
     * @details */
    protected void init(){
    };
}
