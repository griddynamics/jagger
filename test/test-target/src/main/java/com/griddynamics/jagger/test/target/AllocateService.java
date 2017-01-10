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
import java.util.Arrays;

@Path("/allocate")
public class AllocateService {

    /** Call to this endpoint will allocate bytes n-times (n = cycles param value) before sending response back.
     *
     * Example of request: /allocate/100x100
     *
     * @param bytes number of bytes to generate per cycle.
     * @param cycles number of cycles.
     * @return Actual delay, number of allocated bytes, number of cycles.
     * @throws InterruptedException
     */
    @GET
    @Produces("text/plain")
    @Path("{bytes}x{cycles}")
    public String delayFixed(@PathParam("bytes") int bytes, @PathParam("cycles") long cycles) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long sum = 0;
        byte[] array;
        for(long i = 0; i < cycles; i++) {
            array = new byte[bytes];
            Arrays.fill(array, (byte)(i));
            for(int b=0; b<bytes; b++) {
                sum += array[b];
            }
        }
        return String.format("OK: bytes=[%d], cycles=[%d], sum=[%d], actualDelay=[%d]", bytes, cycles, sum, (System.currentTimeMillis() - startTime) );
    }
}
