package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Andrey Badaev
 * Date: 10/11/16
 */
public class ExampleEndpointsProvider implements Iterable {
    private List<JHttpEndpoint> endpoints = new ArrayList<>();
    
    // Simple example of endpoint provider
    // Constructor will be triggered during spring bean creation at Jagger startup
    // Later distributor will invoke iterator method to get endpoints
    public ExampleEndpointsProvider() {
        // Put custom code here to get endpoints
        // In our case they will be hardcoded
        try {
            JHttpEndpoint httpEndpoint = new JHttpEndpoint(new URI("http://www.scala-lang.org"));
            endpoints.add(httpEndpoint);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Iterator<JHttpEndpoint> iterator() {
        return endpoints.iterator();
    }
}
