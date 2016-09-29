package com.griddynamics.jagger.invoker.http.v2;

import com.google.common.base.Preconditions;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.griddynamics.jagger.invoker.http.v2.JHttpEndpoint.Protocol.HTTP;
import static com.griddynamics.jagger.invoker.http.v2.JHttpEndpoint.Protocol.HTTPS;
import static java.lang.String.format;

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
        HTTP("http"), HTTPS("https");

        private String value;

        Protocol(String value) {
            this.value = value;
        }
    }

    private Protocol protocol = HTTP;
    private String hostname;
    private int port = 80;

    public JHttpEndpoint(URI uri) {
        if (StringUtils.startsWith(uri.getPath().toLowerCase(), HTTP.value))
            this.protocol = HTTP;
        else if (StringUtils.startsWith(uri.getPath().toLowerCase(), HTTPS.value))
            this.protocol = HTTPS;
        else
            throw new IllegalArgumentException(format("Protocol of uri '%s' is unsupported!", uri));

        this.hostname = uri.getHost();
        this.port = uri.getPort();
    }

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
        Preconditions.checkNotNull(hostname, "Hostname is null!");

        if (protocol.value == null){
            protocol = HTTP;
        }
        if (port == 80) {
            return URI.create(format("%s://%s", protocol.value, hostname));
        }
        return URI.create(format("%s://%s:%s", protocol.value, hostname, port));
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
     * @param oldUri base {@link URI}
     * @param queryParams query parameters to be added to {@link URI}
     * @return {@link URI} based on oldUri with <b>queryParams</b> added.
     */
    public static URI appendParameters(URI oldUri, Map<String, String> queryParams) {
        String newQuery = oldUri.getQuery();
        String appendQuery = queryParams.entrySet().stream()
                .map(param -> param.getKey() + "=" + param.getValue())
                .reduce("", (a, b) -> a + "&" + b);

        if (newQuery == null) {
            newQuery = StringUtils.removeStart(appendQuery, "&");
        } else {
            newQuery += appendQuery;
        }

        try {
            return new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(), newQuery, oldUri.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
