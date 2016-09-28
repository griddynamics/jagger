package com.griddynamics.jagger.test.jaas.invoker;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;

/**
 * Honestly and boldly stolen from D. L., many thanks.
 * Extremely simplified.
 * //TODO: To be replaced by built-in Invoker implementation once Jggr2.0 is in the wild.
 * //TODO: Do we really need it at the moment?
 */
public class Invoker_Http implements Invoker<String, HttpResponse, String> {

    private static Logger log = LoggerFactory.getLogger(Invoker_Http.class);

    private HttpClient client = new org.apache.http.impl.client.DefaultHttpClient();
    private boolean trustAllSslCertificates = false;
    private boolean logHeadersAndCookies = false;
    private boolean logBody = false;
    private boolean logCodeAndUri = false;

    @Override
    public HttpResponse invoke(String query, String endpoint) throws InvocationException {
        HttpResponse response = null;
        // Create a local instance of cookie store in local HTTP context
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();

        // Bind custom cookie store to the local context
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        if ((endpoint != null) && (!endpoint.isEmpty())) {
            HttpEntity entity = null;

            // transactions in endpoints list
            long startTime;
            long transactionDuration = 0;
            //TODO: Refactor to support POST.
            HttpRequestBase request = new HttpGet();
            try {
                request.setURI(new URI(endpoint));
            } catch (URISyntaxException e) {
                log.warn(String.format("Could not use the endpoint provided(%s).", endpoint), e);
            }

            try {
                if (request == null) {
                    throw new InvocationException("\nProvider returned null instead of http request");
                } else {
                    // http request
                    startTime = System.currentTimeMillis();
                    response = client.execute(request, localContext);
                    transactionDuration += System.currentTimeMillis() - startTime;
                    entity = response.getEntity();

                    StringBuilder sb = new StringBuilder();
                    try {
                        BufferedReader reader =
                                new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                        String line;

                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    } catch (Exception e) {
                        log.warn("Error upon reading the response's entity.", e);
                    }

                    if (response.getStatusLine().getStatusCode() != 200) {
                        log.warn("Url:" + request.getURI());
                        log.warn("Status Code:" + response.getStatusLine().getStatusCode());
                        for (Header header : response.getAllHeaders()) {
                            log.info("Header: " + header.getName() + " :" + header.getValue());
                        }
                        for (Cookie cookie : cookieStore.getCookies()) {
                            log.info("Cookie: " + cookie.getName() + " :" + cookie.getValue());
                        }
                        log.info("Body: " + sb);
                        log.error(String.format("Endpoint: %s", request.getURI()));
                    }
                }

            } catch (Exception e) {
                throw new InvocationException(String.format("Exception during invocation. Endpoint: %s",
                        (request == null) ? "n/a" : request.getURI()), e);
            } finally {
                EntityUtils.consumeQuietly(entity);
            }

                }
        return response;
    }


    public void init() {
        // Change strategy to trust all SSL certificates (self-signed or even absent)
        if (trustAllSslCertificates) {
            TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] certificate, String authType) {
                    return true;
                }
            };
            try {
                SSLSocketFactory sf = new SSLSocketFactory(acceptingTrustStrategy,
                        SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, sf));
            } catch (Exception e) {
                log.error("Error during changing of SSL certificate trust strategy for httpClient",e);
            }
        }
    }

    @Required
    public void setClient(HttpClient client) {
        this.client = client;
        // Enable circular redirects and set allowed depth for redirects
        client.getParams().setParameter(ClientPNames.MAX_REDIRECTS, 20);
        client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        // Enable compatible mode for cookies
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
    }

    public boolean isTrustAllSslCertificates() {
        return trustAllSslCertificates;
    }

    @Required
    public void setTrustAllSslCertificates(boolean trustAllSslCertificates) {
        this.trustAllSslCertificates = trustAllSslCertificates;
    }

    public boolean isLogHeadersAndCookies() {
        return logHeadersAndCookies;
    }

    public void setLogHeadersAndCookies(boolean logHeadersAndCookies) {
        this.logHeadersAndCookies = logHeadersAndCookies;
    }

    public boolean isLogBody() {
        return logBody;
    }

    public void setLogBody(boolean logBody) {
        this.logBody = logBody;
    }

    public boolean isLogCodeAndUri() {
        return logCodeAndUri;
    }

    public void setLogCodeAndUri(boolean logCodeAndUri) {
        this.logCodeAndUri = logCodeAndUri;
    }
}
