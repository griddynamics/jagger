package com.griddynamics.jagger.test.jaas.listener;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.test.TestInfo;
import com.griddynamics.jagger.engine.e1.collector.test.TestListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.http.v2.DefaultHttpInvoker;
import com.griddynamics.jagger.invoker.http.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.net.URISyntaxException;

/**
 * Sends requests to delete DB configs created during corresponding tests.
 * <p>
 * Created by ELozovan on 2016-09-27.
 */
@SuppressWarnings("unused")
public class AfterCreateDbTestsCleanUpListener extends ServicesAware implements Provider<TestListener> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AfterCreateDbTestsCleanUpListener.class);

    @Value( "${jaas.endpoint}" )
    protected String endpoint;

    @Value( "${jaas.rest.base.dbs}" )
    private String uri;

    @Override
    public TestListener provide() {
        return new TestListener() {
            @Override
            public void onStop(TestInfo testInfo) {
                super.onStop(testInfo);

                DefaultHttpInvoker invoker = new DefaultHttpInvoker();
                try {
                    JHttpEndpoint jaasEndpoint = new JHttpEndpoint(endpoint);

                    // Request to delete DB configs created during test run.
                    for (String createdDbId : TestContext.getCreatedDbConfigIds()) {
                        invoker.invoke(new JHttpQuery<String>().delete().path(uri + "/" + createdDbId),
                                        jaasEndpoint);

                    }
                } catch (URISyntaxException e) {
                    LOGGER.warn("Could not create a JaaS endpoint due to ", e);
                }
            }
        };
    }
}