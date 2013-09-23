package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.engine.e1.collector.Validator;
import com.griddynamics.jagger.engine.e1.collector.ValidatorException;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 9/19/13
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidateLoadInvocationListener<Q, R, E> implements LoadInvocationListener<Q, R, E>{
    private Iterable<Validator> validators;
    private LoadInvocationListener<Q, R, E> metrics;

    public ValidateLoadInvocationListener(Iterable<Validator> validators, Iterable<? extends LoadInvocationListener<Q, R, E>> metrics) {
        this.validators = validators;
        this.metrics = Invokers.composeListeners(metrics);
    }

    @Override
    public void onStart(Q query, E endpoint) {
        metrics.onStart(query, endpoint);
    }

    @Override
    public void onSuccess(Q query, E endpoint, R result, long duration) {
        Validator failValidator = null;
        for (Validator validator : validators){
            if (!validator.validate(query, endpoint, result, duration)){
                failValidator = validator;
                break;
            }
        }

        if (failValidator != null){
            onFail(query, endpoint, new ValidatorException(failValidator.getValidator(), result));
        }else{
            metrics.onSuccess(query, endpoint, result, duration);
        }
    }

    @Override
    public void onFail(Q query, E endpoint, InvocationException e) {
        metrics.onFail(query, endpoint, e);
    }

    @Override
    public void onError(Q query, E endpoint, Throwable error) {
        metrics.onError(query, endpoint, error);
    }
}
