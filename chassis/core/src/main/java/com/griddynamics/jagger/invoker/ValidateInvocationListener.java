package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.engine.e1.collector.Validator;
import com.griddynamics.jagger.engine.e1.collector.ValidatorException;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 9/19/13
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidateInvocationListener<Q, R, E> extends InvocationListener<Q, R, E> {
    private Iterable<Validator> validators;
    private LoadInvocationListener<Q, R, E> metrics;
    private InvocationListener<Q, R, E> listener;

    public ValidateInvocationListener(Iterable<Validator> validators, Iterable<? extends LoadInvocationListener<Q, R, E>> metrics, List<InvocationListener<Q, R, E>> listeners) {
        this.validators = validators;
        this.metrics = Invokers.composeAndLogListeners(metrics);
        this.listener = InvocationListener.Composer.compose(listeners);
    }

    @Override
    public void onStart(InvocationInfo<Q, R, E> invocationInfo) {
        metrics.onStart(invocationInfo.getQuery(), invocationInfo.getEndpoint());
        listener.onStart(invocationInfo);
    }

    @Override
    public void onSuccess(InvocationInfo<Q, R, E> invocationInfo) {
        Validator failValidator = null;
        for (Validator validator : validators){
            if (!validator.validate(invocationInfo.getQuery(), invocationInfo.getEndpoint(), invocationInfo.getResult(), invocationInfo.getDuration())){
                failValidator = validator;
                break;
            }
        }

        if (failValidator != null){
            onFail(invocationInfo, new ValidatorException(failValidator.getValidator(), invocationInfo.getResult()));
        }else{
            metrics.onSuccess(invocationInfo.getQuery(), invocationInfo.getEndpoint(), invocationInfo.getResult(), invocationInfo.getDuration());
            listener.onSuccess(invocationInfo);
        }
    }

    @Override
    public void onFail(InvocationInfo<Q, R, E> invocationInfo, InvocationException e) {
        metrics.onFail(invocationInfo.getQuery(), invocationInfo.getEndpoint(), e);
        listener.onFail(invocationInfo, e);
    }

    @Override
    public void onError(InvocationInfo<Q, R, E> invocationInfo, Throwable error) {
        metrics.onError(invocationInfo.getQuery(), invocationInfo.getEndpoint(), error);
        listener.onError(invocationInfo, error);
    }
}
