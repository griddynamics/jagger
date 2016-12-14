package com.griddynamics.jagger.test.jaas.provider;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides main endpoint of JaaS.
 */
public class EndpointsProvider_JaaS implements Iterable {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointsProvider_JaaS.class);
    private List<JHttpEndpoint> endpoints = new LinkedList<>();

    @Value( "${jaas.endpoint}" )
    protected String endpoint;

    public EndpointsProvider_JaaS() {}

    @Override
    public Iterator iterator() {
        if (endpoints.isEmpty()) {
            try {
                endpoints.add(new JHttpEndpoint(new URI(endpoint))); //TODO: use another constructor when PR-626 is merged.
            } catch (URISyntaxException e) {
                LOGGER.warn("Could not create an endpoint entity from {} due to: ", endpoint, e);
            }
        }

        return endpoints.iterator();
    }
}