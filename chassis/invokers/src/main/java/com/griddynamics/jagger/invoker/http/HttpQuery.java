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

import com.google.common.collect.Maps;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.springframework.beans.factory.annotation.Required;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Alexey Kiselyov
 *         Date: 04.08.11
 */
@Deprecated
public class HttpQuery implements Serializable {

    public static enum Method {
        POST,
        PUT,
        GET,
        DELETE,
        TRACE,
        HEAD,
        OPTIONS
    }

    private Method method = Method.GET;
    private Map<String, String> methodParams = Maps.newHashMap();
    private Map<String, Object> clientParams = Maps.newHashMap();

    public HttpQuery() {
    }

    public Method getMethod() {
        return this.method;
    }

    @Required
    public void setMethod(Method method) {
        this.method = method;
    }

    public Map<String, String> getMethodParams() {
        return this.methodParams;
    }

    public void setMethodParams(Map<String, String> params) {
        this.methodParams = params;
    }

    public Map<String, Object> getClientParams() {
        return this.clientParams;
    }

    public void setClientParams(Map<String, Object> clientParams) {
        this.clientParams = clientParams;
    }

    @Override
    public String toString() {
        return "HttpQuery{" +
                "method=" + method +
                ", methodParams=" + methodParams +
                ", clientParams=" + clientParams +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpQuery httpQuery = (HttpQuery) o;

        if (!clientParams.equals(httpQuery.clientParams)) return false;
        if (method != httpQuery.method) return false;
        if (!methodParams.equals(httpQuery.methodParams)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + methodParams.hashCode();
        result = 31 * result + clientParams.hashCode();
        return result;
    }
}
