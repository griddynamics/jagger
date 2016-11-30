/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.griddynamics.jagger.invoker.http;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

/** Invoker that invokes services of SuT via http protocol
 * @author Alexey Kiselyov
 * @n
 * @par Details:
 * @details Invoker uses HttpQuery as query and String as endpoint. Can do POST, GET and etc. request. @n
 *
 * See @xlink{invoker-http} for details on how-to use this element in xml-configuration
 *
 * @ingroup Main_Invokers_group */
@Deprecated
public class HttpInvoker extends ApacheAbstractHttpInvoker<HttpQuery> {
    private static final Logger log = LoggerFactory.getLogger(ApacheAbstractHttpInvoker.class);

    /** Creates http request with method params from query and with url equals endpoint
     * @author Alexey Kiselyov
     * @n
     * @param query    - contains method params
     * @param endpoint - url of target service
     *
     * @return http request for http-client */
    @Override
    protected HttpRequestBase getHttpMethod(HttpQuery query, String endpoint) {

        try {
            URIBuilder uriBuilder = new URIBuilder(endpoint);

            if (!HttpQuery.Method.POST.equals(query.getMethod())) {
                for (Map.Entry<String, String> methodParam : query.getMethodParams().entrySet()) {
                    uriBuilder.setParameter(methodParam.getKey(), methodParam.getValue());
                }
            }

            return createMethod(query, uriBuilder.build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates http request with method params from query and with url equals endpoint
     * @author Alexey Kiselyov
     * @n
     * @param query - contains client params
     *
     * @return http params for http-client */
    @Override
    protected HttpParams getHttpClientParams(HttpQuery query) {
        HttpParams clientParams = new BasicHttpParams();
        for (Map.Entry<String, Object> clientParam : query.getClientParams().entrySet()) {
            clientParams.setParameter(clientParam.getKey(), clientParam.getValue());
        }
        return clientParams;
    }

    private HttpRequestBase createMethod(HttpQuery query, URI uri) throws UnsupportedEncodingException {
        HttpRequestBase method;
        switch (query.getMethod()) {
            case POST:
                method = new HttpPost(uri);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> methodParam : query.getMethodParams().entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(methodParam.getKey(), methodParam.getValue()));
                }
                ((HttpPost) method).setEntity(new UrlEncodedFormEntity(nameValuePairs));
                break;
            case PUT:
                method = new HttpPut(uri);
                break;
            case GET:
                method = new HttpGet(uri);
                break;
            case DELETE:
                method = new HttpDelete(uri);
                break;
            case TRACE:
                method = new HttpTrace(uri);
                break;
            case HEAD:
                method = new HttpHead(uri);
                break;
            case OPTIONS:
                method = new HttpOptions(uri);
                break;
            default:
                throw new UnsupportedOperationException("Invoker does not support \"" + query.getMethod() + "\" HTTP request.");
        }
        return method;
    }

    @Override
    public String toString() {
        return "Apache Commons Http Invoker";
    }
}
