package com.griddynamics.jagger.test.jagger2;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class EndpointsProvider {

    private static List<URI> getUrls() {
        String[] urls = "http://localhost".split(",");  // get list of endpoints from properties
        List<URI> result = new ArrayList<>();
        for (String url : urls) {
            try {
                result.add(new URI(url));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Iterable getEndpoints() {
        return getUrls().stream().map(JHttpEndpoint::new).collect(Collectors.toList());
    }

}
