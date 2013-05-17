/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
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

import com.google.common.collect.Lists;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * {@link Invoker} that invokes services of SuT via http protocol.
 *
 * @author Alexey Kiselyov
 */
public class HttpInvoker extends ApacheAbstractHttpInvoker<HttpQuery> {
    private static final Logger log = LoggerFactory.getLogger(ApacheAbstractHttpInvoker.class);

    @Override
    protected HttpMethod getHttpMethod(HttpQuery query, String endpoint) {
        HttpMethod method = createMethod(query, endpoint);

        if (!HttpQuery.Method.POST.equals(query.getMethod()) && !HttpQuery.Method.CONNECT.equals(query.getMethod())) {
            List<NameValuePair> params = Lists.newLinkedList();
            for (Map.Entry<String, String> methodParam : query.getMethodParams().entrySet()) {
                params.add(new NameValuePair(methodParam.getKey(), methodParam.getValue()));
            }
            method.setQueryString(params.toArray(new NameValuePair[params.size()]));
        }

        return method;
    }

    @Override
    protected HttpClientParams getHttpClientParams(HttpQuery query) {
        HttpClientParams clientParams = new HttpClientParams();
        for (Map.Entry<String, Object> clientParam : query.getClientParams().entrySet()) {
            clientParams.setParameter(clientParam.getKey(), clientParam.getValue());
        }
        return clientParams;
    }

    private HttpMethod createMethod(HttpQuery query, String endpoint) {
        HttpMethod method;
        switch (query.getMethod()) {
            case POST:
                method = new PostMethod(endpoint);
                for (Map.Entry<String, String> methodParam : query.getMethodParams().entrySet()) {
                    ((PostMethod) method).addParameter(methodParam.getKey(), methodParam.getValue());
                }
                break;
            case PUT:
                method = new PutMethod(endpoint);
                break;
            case GET:
                method = new GetMethod(endpoint);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> stringStringEntry : query.getMethodParams().entrySet()) {
                    params.add(new NameValuePair(stringStringEntry.getKey(), stringStringEntry.getValue()));
                }
                method.setQueryString(params.toArray(new NameValuePair[params.size()]));
                break;
            case DELETE:
                method = new DeleteMethod(endpoint);
                break;
            case TRACE:
                method = new TraceMethod(endpoint);
                break;
            case HEAD:
                method = new HeadMethod(endpoint);
                break;
            case OPTIONS:
                method = new OptionsMethod(endpoint);
                break;
            case CONNECT:
                HostConfiguration hostConfiguration = new HostConfiguration();
                hostConfiguration.setHost(endpoint);
                method = new ConnectMethod();
                try {
                    method.setURI(new URI(endpoint, true));
                } catch (URIException e) {
                    throw new InvocationException("InvocationException : ", e);
                }
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
