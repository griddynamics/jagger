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

package com.griddynamics.jagger.providers.creators;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

public class SimpleHttpQueryCreator implements ObjectCreator<HttpGet> {

    private String paramName;
    private String path;
    private String fragment;

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    @Override
    public HttpGet createObject(String... strings) {
        URIBuilder builder = new URIBuilder();
        if (paramName != null) {
            builder.setParameter(paramName, strings[0]);
        }
        if (path != null) {
            builder.setPath(path);
        }
        if (fragment != null) {
            builder.setFragment(fragment);
        }
        try {
            return new HttpGet(builder.build());
        } catch (URISyntaxException e) {
            throw new RuntimeException("URIBuilder.build()", e);
        }
    }

    @Override
    public void setHeader(String[] header) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
