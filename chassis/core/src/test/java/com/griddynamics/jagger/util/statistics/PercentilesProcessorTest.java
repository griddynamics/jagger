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

package com.griddynamics.jagger.util.statistics;

import com.griddynamics.jagger.util.statistics.percentiles.PercentilesProcessor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;

public class PercentilesProcessorTest {
    @Test
    public void testPercentilesEngine() {

        double maxDeviation = 0.06; // 6%

        for(double p : new double[] {5, 10, 20, 50, 80, 85, 95, 99}) {
            PercentilesProcessor heuristic = new PercentilesProcessor(PercentilesProcessor.EstimationStrategy.HEURISTIC);
            PercentilesProcessor exact = new PercentilesProcessor(PercentilesProcessor.EstimationStrategy.EXACT);

            Random rnd = new Random(1234);
            for(int i = 0; i < 1000; i++) {
                double value = rnd.nextDouble() + 100;
                heuristic.addValue(value);
                exact.addValue(value);
            }
            for(int i = 0; i < 10000; i++) {
                double value = rnd.nextDouble() * 10;
                heuristic.addValue(value);
                exact.addValue(value);
            }

            Assert.assertEquals( Double.compare(Math.abs(exact.getPercentile(p) / heuristic.getPercentile(p) - 1), maxDeviation), -1 );
        }
    }
}
