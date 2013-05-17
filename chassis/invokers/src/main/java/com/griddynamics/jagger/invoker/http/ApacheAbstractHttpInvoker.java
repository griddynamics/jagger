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

import com.google.common.base.Preconditions;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class ApacheAbstractHttpInvoker<Q> implements Invoker<Q, HttpResponse, String> {
    private static final Logger log = LoggerFactory.getLogger(ApacheAbstractHttpInvoker.class);

    private HttpClient httpClient;

    @Required
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public final HttpResponse invoke(Q query, String endpoint) throws InvocationException {
        Preconditions.checkNotNull(query);
        Preconditions.checkNotNull(endpoint);

        httpClient.setParams(getHttpClientParams(query));
        HttpMethod method = null;
        BufferedReader br = null;
        try {
            method = getHttpMethod(query, endpoint);

            int returnCode = httpClient.executeMethod(method);

            br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            StringBuilder response = new StringBuilder();

            String readLine;
            while ((readLine = br.readLine()) != null) {
                response.append(readLine);
            }

            return HttpResponse.create(returnCode, response.toString());
        } catch (HttpException e) {
            log.debug("Error during invocation", e);
            throw new InvocationException("InvocationException : ", e);
        } catch (IOException e) {
            log.debug("Error during invocation", e);
            throw new InvocationException("InvocationException : ", e);
        } finally {
            try {
                if (method != null) {
                    method.releaseConnection();
                }
            } catch (Throwable e) {
                log.error("Cannot release connection", e);
            }
            if (br != null) {
                try {
                    br.close();
                } catch (Throwable e) {
                    log.error("Can't close connection", e);
                }
            }
        }
    }

    protected abstract HttpMethod getHttpMethod(Q query, String endpoint) throws URIException;

    protected abstract HttpClientParams getHttpClientParams(Q query);
}
