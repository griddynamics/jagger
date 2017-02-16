package com.griddynamics.jagger.test.javabuilders.utils;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Read coma-separated list of sut endpoints and provide iterator of appropriate JHttpEndpoint
 */
public class EndpointsProvider implements Iterable<JHttpEndpoint> {

    private final List<String> endpointsList;

    public EndpointsProvider(JaggerPropertiesProvider provider) {
        String endpoints = provider.getPropertyValue("test.service.endpoints");
        if(endpoints==null){
            endpoints = "http://localhost:8080";
        }
        endpointsList = Arrays.asList(endpoints.split(","));
    }

    @Override
    public Iterator<JHttpEndpoint> iterator() {
        return endpointsList.stream().map(JHttpEndpoint::new).iterator();
    }
}
