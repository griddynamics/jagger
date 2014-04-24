package com.griddynamics.jagger.invoker;

/** An object, which can process query and endpoint before and after invocation
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details QueryProcessor decides what query and endpoint will be executed next @n
 * @n */
public interface QueryProcessor {


    /** Method is executed before invocation
    * @param stepInfo - contains query and endpoint, which were passed from previous step or query-distributor
    * @return query and endpoint for current invocation */
    StepInfo preProcess(StepInfo stepInfo);

    /** Method is executed after invocation
     * @param currentResult - contains invocation query, endpoint and result
     * @return query and endpoint for next invocation step. If it equals null, next step will be not executed */
    StepInfo postProcess(StepResult currentResult);
}
