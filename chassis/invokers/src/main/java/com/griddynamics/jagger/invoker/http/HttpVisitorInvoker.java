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

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.util.Nothing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** Creates http request via java.net.URL
 * @author Dmitry Kotlyarov
 * @n
 * @par Details:
 * @details Creates http request(GET request) to endpoint. Return response of http service. In common ways returns http page.
 *
 * @ingroup Main_Invokers_group */
@Deprecated
public class HttpVisitorInvoker implements Invoker<Nothing, String, String> {

    public HttpVisitorInvoker() {
    }

    /** Create GET http request to endpoint
     * @author Mairbek Khadikov
     * @n
     *
     * @param query - query of type Nothing
     * @param endpoint - target url
     *
     * @return text of http response
     * @throws InvocationException when invocation failed*/
    @Override
	public String invoke(Nothing query, String endpoint) throws InvocationException {
		try {
			return doHttpGet(endpoint);
		} catch (Exception e) {
			throw new InvocationException(e.getMessage(), e);
		}
	}

	private String doHttpGet(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setInstanceFollowRedirects(false);
		connection.setUseCaches(false);

		InputStream is = connection.getInputStream();

		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String theLine;

        StringBuilder response = new StringBuilder();
		while ((theLine = br.readLine()) != null) {
			response.append(theLine);
		}
		connection.disconnect();

		return response.toString();
	}

    @Override
    public String toString() {
        return "Http Invoker";
    }

    private static final long serialVersionUID = 1323093228502340420L;

}
