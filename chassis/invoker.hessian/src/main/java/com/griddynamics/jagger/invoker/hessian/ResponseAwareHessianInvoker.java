package com.griddynamics.jagger.invoker.hessian;

import com.caucho.hessian.client.HessianProxyFactory;
import com.google.common.base.Throwables;

import java.net.MalformedURLException;

/**
 * User: vshulga
 * Date: 10/17/12
 */
public abstract class ResponseAwareHessianInvoker<S, Q, R> extends HessianInvoker<S, Q, R> {

    @SuppressWarnings("unchecked")
    protected S initService(String url) {
        HessianProxyFactory factory = new ResponseAwareHessianProxyFactory();
        factory.setOverloadEnabled(true);
        try {
            return (S) factory.create(getClazz(), url);
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        }
    }

}
