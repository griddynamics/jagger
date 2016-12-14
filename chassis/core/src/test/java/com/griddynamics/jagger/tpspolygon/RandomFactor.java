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

package com.griddynamics.jagger.tpspolygon;

import com.griddynamics.jagger.engine.e1.scenario.WorkloadConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class RandomFactor implements TpsGenerator {
    private static final Logger log = LoggerFactory.getLogger(RandomFactor.class);

    private static final double PEEK = 0.1;
    private static final double LAG = 0.6;
    private final TpsGenerator generator;
    private Random random = new Random();

    public RandomFactor(TpsGenerator generator) {
        this.generator = generator;
    }

    @Override
    public Long generate(WorkloadConfiguration configuration) {
        double factor = random.nextDouble();
        double res = generator.generate(configuration);
        if (factor < PEEK) {
            log.debug("Peek!");
            res *= 1 - factor;
        } else if (factor < LAG) {
            log.debug("Lag!");
            res *= 1 - factor * 0.01;
        }


        return (long) Math.ceil(res);
    }
}
