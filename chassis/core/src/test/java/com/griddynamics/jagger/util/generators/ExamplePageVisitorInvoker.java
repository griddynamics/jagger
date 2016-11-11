package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

/**
 * Created by Andrey Badaev
 * Date: 10/11/16
 */
public class ExamplePageVisitorInvoker extends DefaultHttpInvoker {
    private static final long serialVersionUID = 1323093228502340420L;
    
    @Override
    public JHttpResponse invoke(JHttpQuery query, JHttpEndpoint endpoint) throws InvocationException {
        return super.invoke(query, endpoint);
    }
}
