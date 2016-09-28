package com.griddynamics.jagger.invoker.http.v2;

import java.net.URI;
import java.util.Map;

/**
 * Created by aantonenko on 9/27/16.
 */
public class JHttpEndpoint  {
    public enum Protocol {
        HTTP, HTTPS
    }

    private Protocol protocol = Protocol.HTTP;
    private String hostname;
    private int port;

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public URI getURI() {
        throw new UnsupportedOperationException();
    }

    public URI getURI(Map<String, String> queryParams) {
        throw new UnsupportedOperationException();
    }
}
