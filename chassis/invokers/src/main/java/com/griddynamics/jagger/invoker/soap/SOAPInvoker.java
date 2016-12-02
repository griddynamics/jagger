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

package com.griddynamics.jagger.invoker.soap;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

// @todo Lowercase SOAP
// @todo Refactor code

/** Create a SOAP request to SuT
 * @author Mairbek Khadikov
 * @n
 *
 * @ingroup Main_Invokers_group */
@Deprecated
public class SOAPInvoker implements Invoker<SOAPQuery, String, String> {
    private static final Logger log = LoggerFactory.getLogger(SOAPInvoker.class);

    @Override
    public String invoke(SOAPQuery query, String endpoint) throws InvocationException {
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnectionFactory.createConnection();
            SOAPFactory soapFactory = SOAPFactory.newInstance();

            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage message = factory.createMessage();

            SOAPBody body = message.getSOAPBody();

            Name bodyName = soapFactory.createName(query.getMethod(), "", "urn:ActiveStations");
            SOAPBodyElement soapBodyElement = body.addBodyElement(bodyName);
            for (Map.Entry<String, Object> methodParam : query.getMethodParams().entrySet()) {
                soapBodyElement.addChildElement(methodParam.getKey(), methodParam.getValue().toString());
            }
//
//            System.out.print("\nPrinting the message that is being sent: \n\n");
//            message.writeTo(System.out);
//            System.out.println("\n\n");

            URL url = new URL(endpoint);
            SOAPMessage response = connection.call(message, url);
            connection.close();

//            System.out.println("\nPrinting the respone that was recieved: \n\n");
//            response.writeTo(System.out);

//            FileOutputStream fout = new FileOutputStream("SoapResponse.xml");
//            response.writeTo(fout);
//            fout.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.writeTo(out);
            return out.toString();

//            SOAPBody responseBody = response.getSOAPBody();
        } catch (SOAPException e) {
            throw new InvocationException("SOAPException: ", e);
        } catch (IOException e) {
            throw new InvocationException("IOException: ", e);
        }
    }

    @Override
    public String toString() {
        return "SoapInvoker";
    }
}
