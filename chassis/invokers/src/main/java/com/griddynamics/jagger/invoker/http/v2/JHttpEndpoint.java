package com.griddynamics.jagger.invoker.http.v2;

import com.google.common.base.Preconditions;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import static com.griddynamics.jagger.invoker.http.v2.JHttpEndpoint.Protocol.HTTP;
import static com.griddynamics.jagger.invoker.http.v2.JHttpEndpoint.Protocol.HTTPS;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

/**
 * An object that represents HTTP-endpoint. It consists of {@link JHttpEndpoint#protocol},
 * {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields. <p>
 *
 * @author Anton Antonenko
 * @since 1.3
 */
@SuppressWarnings("unused")
public class JHttpEndpoint {

    /**
     * Enum representing HTTP and HTTPS protocols
     */
    public enum Protocol {
        HTTP, HTTPS
    }

    private Protocol protocol = HTTP;
    private String hostname;
    private int port = 80;

    /**
     * Parses given uri and sets {@link JHttpEndpoint#protocol}, {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields.
     *
     * @param uri URI to parse
     */
    public JHttpEndpoint(URI uri) {
        try {
            URL url = uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        if (equalsIgnoreCase(uri.getScheme(), HTTP.name()))
            this.protocol = HTTP;
        else if (equalsIgnoreCase(uri.getScheme(), HTTPS.name()))
            this.protocol = HTTPS;
        else
            throw new IllegalArgumentException(format("Protocol of uri '%s' is unsupported!", uri));

        this.hostname = uri.getHost();
        this.port = uri.getPort() < 0 ? 80 : uri.getPort();
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
            protocol = HTTP;
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
        if (StringUtils.isEmpty(path))
            return oldUri;

        return fromUri(oldUri).path(path).build().toUri();
    }

    /**
     * @param queryParams query parameters to be added to URI
     * @return {@link URI} based on {@link JHttpEndpoint#protocol},
     * {@link JHttpEndpoint#hostname} and {@link JHttpEndpoint#port} fields values with <b>queryParams</b> added.
     */
    public URI getURI(Map<String, String> queryParams) {
        URI uri = getURI();
        if (MapUtils.isEmpty(queryParams))
            return uri;

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
        if (MapUtils.isEmpty(queryParams))
            return uri;

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
