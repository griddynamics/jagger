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

package com.griddynamics.jagger.test.target;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Random;

@Path("/net")
public class NetService {
    private static final Random rnd = new Random();

    /** Call to this endpoint will produce random text (with length of bytes param value) before sending response back.
     *
     * Example of request: /net/text/1000
     *
     * @param bytes length of produced text.
     * @return Actual delay, length of produced text, randomly produced text.
     * @throws InterruptedException
     */
    @GET
    @Produces("text/plain")
    @Path("text/{bytes}")
    public String delayFixed(@PathParam("bytes") long bytes) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        StringBuffer buffer = new StringBuffer();
        int lineLength = 128;
        for(long i = 0; i < bytes; i++) {
            buffer.append( (char)('A' + rnd.nextInt('Z' - 'A')) );
            if(i % lineLength == 0 && i != 0) {
                buffer.append('\n');
                i++;
            }
        }
        return String.format("OK: bytes=[%d], actualDelay=[%d], text=\n%s", bytes, (System.currentTimeMillis() - startTime), buffer.toString() );
    }
}
