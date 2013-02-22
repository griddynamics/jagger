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

package com.griddynamics.jagger.engine.e1.scenario;

import java.util.concurrent.atomic.AtomicBoolean;


public class IterationsOrDurationStrategyConfiguration implements TerminateStrategyConfiguration {
    private int iterations;
    private String duration;
    private AtomicBoolean shutdown;

    public IterationsOrDurationStrategyConfiguration(int samples, String duration, AtomicBoolean shutdown) {
        this.iterations = samples;
        this.duration = duration;
        this.shutdown = shutdown;
    }

    public IterationsOrDurationStrategyConfiguration() {
    }

    public void setShutdown(AtomicBoolean shutdown) {
        this.shutdown = shutdown;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public TerminationStrategy getTerminateStrategy() {
        return new UserTerminationStrategy(duration, iterations, shutdown);
    }

    @Override
    public String toString() {
        if (duration == null){
            return iterations + " samples";
        }else{
            if (iterations != -1){
                return duration+"; "+iterations+" samples";
            }else{
                return duration;
            }
        }
    }
}
