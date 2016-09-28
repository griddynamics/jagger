package com.griddynamics.jagger.invoker.http.v2;

import java.net.URI;
import java.util.Map;

/**
 * An object that represents HTTP-endpoint. It consists of {@link JHttpEndpoint#protocol},
 * {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields. <p>
 *
 * @author Anton Antonenko
 * @since 1.2.7
 */
@SuppressWarnings("unused")
public class JHttpEndpoint {

    /**
     * Enum representing HTTP and HTTPS protocols
     */
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

    /**
     * @return {@link URI} based on {@link JHttpEndpoint#protocol},
     * {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields values.
     */
    public URI getURI() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param queryParams query parameters to be added to URI
     * @return {@link URI} based on {@link JHttpEndpoint#protocol},
     * {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields values with <b>queryParams</b> added.
     */
    public URI getURI(Map<String, String> queryParams) {
        throw new UnsupportedOperationException();
    }
}
