package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;

/** An abstract class, that gives user an access to Jagger services
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details If you would like to have an access to jagger services - extend this class.
 * @n
 */
public abstract class ServicesAware implements ServicesInitializable {

    private MetricService metricService;
    private SessionInfoService sessionInfoService;
    private DataService dataService;

    /** Gives access to @ref MetricService
     *@return metric service */
    protected MetricService getMetricService(){
        return metricService;
    }

    /** Gives access to @ref SessionInfoService
     *@return sessionInfo service */
    protected SessionInfoService getSessionInfoService(){
        return sessionInfoService;
    }

    /** Gives access to @ref DataService
     *@return data service */
    public DataService getDataService() {
        return dataService;
    }

    @Override
    public final void initServices(String sessionId, String taskId, NodeContext context, JaggerPlace environment){

        /* begin: following section is used for docu generation - listeners to services relation */

        /* Services available for test listener */
        if (environment.equals(JaggerPlace.TEST_LISTENER)){
            metricService       = new DefaultMetricService(sessionId, taskId, context);         /* Available */
            sessionInfoService  = new DefaultSessionInfoService(context);                       /* Available */
            dataService         = new DefaultDataService(context);                              /* Available */
        }
        /* Services available for test group listener */
        if (environment.equals(JaggerPlace.TEST_GROUP_LISTENER)){
            metricService       = new DefaultMetricService(sessionId, taskId, context);         /* Available */
            sessionInfoService  = new DefaultSessionInfoService(context);                       /* Available */
            dataService         = new DefaultDataService(context);                              /* Available */
        }
        /* Services available for test suite listener */
        if (environment.equals(JaggerPlace.TEST_SUITE_LISTENER)){
            metricService       = new EmptyMetricService(JaggerPlace.TEST_SUITE_LISTENER);      /* NOT AVAILABLE */
            sessionInfoService  = new DefaultSessionInfoService(context);                       /* Available */
            dataService         = new DefaultDataService(context);                              /* Available */
        }
        /* Services available for decision maker listener */
        if (environment.equals(JaggerPlace.TEST_GROUP_DECISION_MAKER_LISTENER)){
            metricService       = new EmptyMetricService(JaggerPlace.TEST_GROUP_DECISION_MAKER_LISTENER);        /* NOT AVAILABLE */
            sessionInfoService  = new DefaultSessionInfoService(context);                                        /* Available */
            dataService         = new DefaultDataService(context);                                               /* Available */
        }

        /* Services available for invocation listener */
        if (environment.equals(JaggerPlace.INVOCATION_LISTENER)){
            metricService       = new DefaultMetricService(sessionId, taskId, context);         /* Available */
            sessionInfoService  = new EmptySessionInfoService(JaggerPlace.INVOCATION_LISTENER); /* NOT AVAILABLE */
            dataService         = new EmptyDataService(JaggerPlace.INVOCATION_LISTENER);        /* NOT AVAILABLE */
        }

        /* end: following section is used for docu generation - listeners to services relation */

        init();
    }

    /** User action, that will be executed before at least one object will be provided.
     * @author Gribov Kirill
     * @n
     * @par Details
     * @details If you would like to execute some actions, before objects will be provided, override this method */
    protected void init(){
    };
}
