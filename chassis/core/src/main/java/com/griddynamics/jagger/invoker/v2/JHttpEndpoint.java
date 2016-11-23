package com.griddynamics.jagger.invoker.v2;

import static com.griddynamics.jagger.invoker.v2.JHttpEndpoint.Protocol.HTTP;
import static com.griddynamics.jagger.invoker.v2.JHttpEndpoint.Protocol.HTTPS;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * An object that represents HTTP-endpoint. It consists of {@link JHttpEndpoint#protocol},
 * {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields. <p>
 *
 * @author Anton Antonenko
 * @since 2.0
 */
public class JHttpEndpoint implements Serializable {

    public static final Protocol DEF_PROTOCOL = Protocol.HTTP;
    
    /**
     * Enum representing HTTP and HTTPS protocols
     */
    public enum Protocol {
        HTTP, HTTPS
    }

    private Protocol protocol = DEF_PROTOCOL;
    private String hostname;
    private int port = HttpURL.DEFAULT_PORT;

    /**
     * Parses given uri and sets {@link JHttpEndpoint#protocol}, {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields.
     *
     * @param uri URI to parse
     */
    @SuppressWarnings("unused")
    public JHttpEndpoint(URI uri) {
        try {
            URL url = uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        if (equalsIgnoreCase(uri.getScheme(), HTTP.name())) {
            this.protocol = HTTP;
            this.port = HttpURL.DEFAULT_PORT;
        } else if (equalsIgnoreCase(uri.getScheme(), HTTPS.name())) {
            this.protocol = HTTPS;
            this.port = HttpsURL.DEFAULT_PORT;
        } else {
            throw new IllegalArgumentException(format("Protocol of uri '%s' is unsupported!", uri));
        }

        this.hostname = uri.getHost();
        if (uri.getPort() > 0) {
            this.port = uri.getPort();
        }
    }

    /**
     * @param protocol protocol of endpoint
     * @param hostname hostname of endpoint
     * @param port     port of endpoint
     */
    public JHttpEndpoint(Protocol protocol, String hostname, int port) {
        Objects.nonNull(protocol);
        this.protocol = protocol;
        
        if (org.springframework.util.StringUtils.isEmpty(hostname)) {
            throw new IllegalArgumentException(format("hostname must non-empty. Provided value: %s", hostname));
        }
        this.hostname = hostname;
        
        if (port <= 0) {
            throw new IllegalArgumentException(format("port number must be > 0. Provided value: %s", port));
        }
        this.port = port;
    }

    /**
     * @param hostname hostname of endpoint
     * @param port     port of endpoint
     */
    public JHttpEndpoint(String hostname, int port) {
        this(DEF_PROTOCOL, hostname, port);
    }

    /**
     * @param endpointURL string with url to be passed to {@link JHttpEndpoint#JHttpEndpoint(URI)} constructor
     */
    public JHttpEndpoint(String endpointURL) {
        this(URI.create(endpointURL));
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    /**
     * @return {@link URI} based on {@link JHttpEndpoint#protocol},
     * {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields values.
     */
    public URI getURI() {
        Preconditions.checkNotNull(hostname, "Hostname is null!");

        if (protocol == null) {
            protocol = DEF_PROTOCOL;
        }
        return newInstance().scheme(protocol.name().toLowerCase()).host(hostname).port(port).build().toUri();
    }

    /**
     * @param path path to be added to URI
     * @return {@link URI} based on {@link JHttpEndpoint#protocol},
     * {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields values with <b>path</b> added.
     */
    public URI getURI(String path) {
        URI oldUri = getURI();
        if (StringUtils.isEmpty(path)) {
            return oldUri;
        }

        return fromUri(oldUri).path(path).build().toUri();
    }

    /**
     * @param queryParams query parameters to be added to URI
     * @return {@link URI} based on {@link JHttpEndpoint#protocol},
     * {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields values with <b>queryParams</b> added.
     */
    public URI getURI(Map<String, String> queryParams) {
        URI uri = getURI();
        if (MapUtils.isEmpty(queryParams)) {
            return uri;
        }

        return appendParameters(uri, queryParams);
    }

    /**
     * @param path        path to be added to URI
     * @param queryParams query parameters to be added to URI
     * @return {@link URI} based on {@link JHttpEndpoint#protocol},
     * {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields values with <b>path</b> and <b>queryParams</b> added.
     */
    public URI getURI(String path, Map<String, String> queryParams) {
        URI uri = getURI(path);
        if (MapUtils.isEmpty(queryParams)) {
            return uri;
        }

        return appendParameters(uri, queryParams);
    }

    /**
     * @param oldUri      base {@link URI}
     * @param queryParams query parameters to be added to {@link URI}
     * @return {@link URI} based on oldUri with <b>queryParams</b> added.
     */
    public static URI appendParameters(URI oldUri, Map<String, String> queryParams) {
        MultiValueMap<String, String> localQueryParams = new LinkedMultiValueMap<>();
        queryParams.entrySet().forEach(entry -> localQueryParams.add(entry.getKey(), entry.getValue()));

        return fromUri(oldUri).queryParams(localQueryParams).build().toUri();
    }

    @Override
    public String toString() {
        return "JHttpEndpoint{" +
                "protocol=" + protocol +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                '}';
    }
}
