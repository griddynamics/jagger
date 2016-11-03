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

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.util.Nothing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/** Creates TCP connections
 * @author Dmitry Kotlyarov
 * @n
 * @par Details:
 * @details Creates an 10000 of TCP connections to SuT
 *
 * @ingroup Main_Invokers_group */
@Deprecated
public class TcpTrafficInvoker implements Invoker<Nothing, String, String> {
    private static final long serialVersionUID = -1L;
    private static final Logger log = LoggerFactory.getLogger(TcpTrafficInvoker.class);

    //@todo - add an ability to customize number of requests
    public static final long COUNT = 10000;

    public TcpTrafficInvoker() {
    }

    /** Makes a number of tcp connections
     * @author Dmitry Kotlyarovv
     * @n
     * @param query    - empty query
     * @param endpoint - url of SuT
     *
     * @return empty string
     * @throws InvocationException when invocation failed */
    @Override
    public String invoke(Nothing query, String endpoint) throws InvocationException {
        try {
            URL url = new URL(endpoint);
            for (long i = 0; i < COUNT; ++i) {
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

                if (i % 500 == 0) {
                    log.debug("i = {}", i);
                }
            }
            return "";
        } catch (Exception ex) {
            throw new InvocationException(ex.getMessage(), ex);
        }
    }
}
