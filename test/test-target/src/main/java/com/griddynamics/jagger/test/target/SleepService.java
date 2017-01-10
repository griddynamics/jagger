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

@Path("/sleep")
public class SleepService{

    private static final Random rnd = new Random();

    /**
     * This endpoint produces responses with exact waiting time (specified by delay parameter).
     *
     * Example of request: /sleep/100
     *
     * @param delay time in milliseconds before endpoint sends response back.
     * @return Actual delay and expected delay values.
     * @throws InterruptedException
     */
    @GET
    @Produces("text/plain")
    @Path("{delay}")
    public String delayFixed(@PathParam("delay") int delay) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Thread.sleep(delay);
        return "OK: targetDelay=[" + delay + "], actualDelay=[" + (System.currentTimeMillis() - startTime) + "]";
    }

    /**
     * This endpoint produces responses with random waiting time (between delayMin and delayMax).
     *
     * Example of request: /sleep/100-1000
     *
     * @param delayMin lower border of random delay
     * @param delayMax higher border of random delay
     * @return Actual delay and expected delay values.
     * @throws InterruptedException
     */
    @GET
    @Produces("text/plain")
    @Path("{delayMin}-{delayMax}")
    public String delayRandom(@PathParam("delayMin") int delayMin, @PathParam("delayMax") int delayMax) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int delay = delayMin + rnd.nextInt(delayMax - delayMin);
        Thread.sleep(delay);
        return "OK: targetDelay=[" + delay + "], actualDelay=[" + (System.currentTimeMillis() - startTime) + "]";
    }

    /**
     * This endpoint produces responses with waiting time depending on the sine function of time (between 0 and delayMax).
     *
     * Example of request: /sleep/pulse/10000/3000
     *
     * @param period period of sinusoid
     * @param delayMax higher value of sinusoid
     * @return Actual delay and expected delay values.
     * @throws InterruptedException
     */
    @GET
    @Produces("text/plain")
    @Path("pulse/{period}/{delayMax}")
    public String delayPulse(@PathParam("period") int period, @PathParam("delayMax") int delayMax) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        double arg = 2 * Math.PI * (startTime % (period)) / ((double)(period));
        int delay = (int)( delayMax * (Math.sin( arg ) + 1)/2 );
        Thread.sleep(delay);
        return "OK: targetDelay=[" + delay + "], actualDelay=[" + (System.currentTimeMillis() - startTime) + "], arg=[" + arg + "]";
    }
}
