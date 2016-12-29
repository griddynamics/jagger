package com.griddynamics.jagger.test.javabuilders.smoke_components;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

/**
 * Invoker to check creation of definition with custom invoker.
 */
public class DummyCustomInvoker extends DefaultHttpInvoker {
    @Override
    public JHttpResponse invoke(JHttpQuery query, JHttpEndpoint endpoint) throws InvocationException {
        return super.invoke(query, endpoint);
    }
}
