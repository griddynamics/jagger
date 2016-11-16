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

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/** Invoker that invokes services of SuT via http protocol
 * @author Evelina Stepanova
 * @n
 * @par Details:
 * @details Uses HttpRequestBase as query param type unlike HttpInvoker which uses HttpQuery. That allows you to create complex requests.
 *
 * @ingroup Main_Invokers_group */
@Deprecated
public class ApacheHttpInvoker extends ApacheAbstractHttpInvoker<HttpRequestBase> {
    private static final Logger log = LoggerFactory.getLogger(ApacheHttpInvoker.class);

    /** Makes http request for apache http-client
     * @author Mairbek Khadikov
     * @n
     * @param query    - input data for http request
     * @param endpoint - url of SuT
     *
     * @return http query for apache http-client*/
    @Override
    protected HttpRequestBase getHttpMethod(HttpRequestBase query, String endpoint) {
        try {
            if (query.getURI() == null) {
                query.setURI(URI.create(endpoint));
                return query;
            } else {
                URIBuilder uriBuilder = new URIBuilder(URI.create(endpoint));
                uriBuilder.setQuery(query.getURI().getQuery());
                uriBuilder.setFragment(query.getURI().getFragment());
                uriBuilder.setUserInfo(query.getURI().getUserInfo());
                if (!query.getURI().getPath().isEmpty()) {
                    uriBuilder.setPath(query.getURI().getPath());
                }
                query.setURI(uriBuilder.build());
                return query;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Takes client params from query
     * @author Mairbek Khadikov
     * @n
     * @param query - apache http query
     *
     * @return client params for apache http-client*/
    @Override
    protected HttpParams getHttpClientParams(HttpRequestBase query) {
        return query.getParams();
    }

    @Override
    public String toString() {
        return "Apache Commons Http Invoker";
    }
}
