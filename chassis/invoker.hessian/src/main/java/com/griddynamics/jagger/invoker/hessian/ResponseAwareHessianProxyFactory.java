package com.griddynamics.jagger.invoker.hessian;


import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.HessianRemoteObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: vshulga
 * Date: 10/8/12
 */
public class ResponseAwareHessianProxyFactory extends HessianProxyFactory {

    private static Logger logger = LoggerFactory.getLogger(ResponseAwareHessianProxyFactory.class);

    /**
     * Return our subclass of the Hessian Proxy.
     */
    @Override
    public Object create(Class api, String urlName, ClassLoader loader)
            throws MalformedURLException {
        if (api == null) {
            logger.error("api is null for HessianProxyFactory.create()");
            throw new NullPointerException(
                    "api must not be null for HessianProxyFactory.create()");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("urlName ", urlName);
        }

        URL url = new URL(urlName);
        InvocationHandler handler = new ResponseAwareHessianProxy(this, url);

        Object proxyInstance = Proxy.newProxyInstance(loader, new Class[]{
                api, HessianRemoteObject.class}, handler);

        if (logger.isDebugEnabled()) {
            logger.debug("Context Aware Hessian Proxy Instance ", proxyInstance);
        }
        return proxyInstance;
    }
}