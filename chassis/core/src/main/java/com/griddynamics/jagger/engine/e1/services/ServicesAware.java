package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.Provider;

/** An abstract class, that gives user an access to Jagger services
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details If you would like to have an access to jagger services - extend this class.
 * @n
 * */
public abstract class ServicesAware implements ServicesInitializable {

    private MetricService metricService;
    private SessionInfoService sessionInfoService;

    /** Returns MetricService
     * @author Gribov Kirill
     * @n
     * @par Details:
     * @details Returns metric service for current test
     *@return metric service */
    protected MetricService getMetricService(){
        return metricService;
    }

    /** Returns SessionInfoService
     * @author Gribov Kirill
     * @n
     * @par Details:
     * @details Returns sessionInfo service for current test
     *@return sessionInfo service */
    protected SessionInfoService getSessionInfoService(){
        return sessionInfoService;
    }

    @Override
    public final void initServices(String sessionId, String taskId, NodeContext context, JaggerEnvironment environment){
        if (environment.equals(JaggerEnvironment.TEST)){
            initTestServices(sessionId, taskId, context);
        }

        if (environment.equals(JaggerEnvironment.TEST_GROUP)){
            initTestGroupServices(sessionId, taskId, context);
        }

        if (environment.equals(JaggerEnvironment.TEST_SUITE)){
            initTestSuiteServices(sessionId, taskId, context);
        }

        init();
    }

    /** User action, that will be executed before at least one object will be provided.
     * @author Gribov Kirill
     * @n
     * @par Details: If you would like to execute some actions, before objects will be provided, override this method
     * @details */
    protected void init(){
    };

    private void initTestServices(String sessionId, String taskId, NodeContext context){
        metricService = new DefaultMetricService(sessionId, taskId, context);
        sessionInfoService = new DefaultSessionInfoService(context);
    }

    private void initTestGroupServices(String sessionId, String taskId, NodeContext context){
        metricService = new EmptyMetricService(JaggerEnvironment.TEST_GROUP);
        sessionInfoService = new DefaultSessionInfoService(context);
    }

    private void initTestSuiteServices(String sessionId, String taskId, NodeContext context){
        metricService = new EmptyMetricService(JaggerEnvironment.TEST_SUITE);
        sessionInfoService = new DefaultSessionInfoService(context);
    }
}
