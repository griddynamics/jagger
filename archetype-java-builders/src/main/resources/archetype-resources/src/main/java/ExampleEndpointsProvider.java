package ${package};

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Simple example of endpoint provider
 *
 */
// begin: following section is used for docu generation - Endpoint provider
public class ExampleEndpointsProvider implements Iterable {
    private List<JHttpEndpoint> endpoints = new ArrayList<>();
    
    // Simple example of endpoint provider
    // Constructor will be triggered during spring bean creation at Jagger startup
    // Later distributor will invoke iterator method to get endpoints
    public ExampleEndpointsProvider() {
        // Put custom code here to get endpoints
        // In our case they will be hardcoded
        JHttpEndpoint httpEndpoint = new JHttpEndpoint(URI.create("https://jagger.griddynamics.net:443"));
        endpoints.add(httpEndpoint);
    }
    
    @Override
    public Iterator<JHttpEndpoint> iterator() {
        return endpoints.iterator();
    }
}
// end: following section is used for docu generation - Endpoint provider
