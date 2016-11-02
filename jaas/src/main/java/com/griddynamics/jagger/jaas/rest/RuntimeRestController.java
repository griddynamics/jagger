package com.griddynamics.jagger.jaas.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * JaaS REST API controller based on Spring MVC which exposes jagger runtime info.
 */
@RequestMapping(value = "/runtime")
@RestController
public class RuntimeRestController {
    
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    
    @GetMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTestConfig(@RequestParam String host, @RequestParam int port) {
        return getResponseFor(host, port, "/config");
    }
    
    @GetMapping(path = "/session", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSessionInfo(@RequestParam String host, @RequestParam int port) {
        return getResponseFor(host, port, "/session");
    }
    
    @GetMapping(path = "/tests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTasks(@RequestParam String host, @RequestParam int port) {
        return getResponseFor(host, port, "/tests");
    }
    
    @GetMapping(path = "/tests/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTestInfo(@PathVariable String name, @RequestParam String host,
                                              @RequestParam int port
    ) {
        return getResponseFor(host, port, "/tests/" + name);
    }
    
    private ResponseEntity<String> getResponseFor(String host, int port, String path) {
        URI uri =
                UriComponentsBuilder.newInstance().scheme("http").host(host).port(port).path("/jaas/master").path(path)
                                    .build().toUri();
        ResponseEntity<String> responseEntity = REST_TEMPLATE.getForEntity(uri, String.class);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }
}
