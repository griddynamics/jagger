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

import com.griddynamics.jagger.engine.e1.scenario.DefaultWorkloadSuggestionMaker;
import com.griddynamics.jagger.engine.e1.scenario.NodeTpsRecorder;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadConfiguration;
import com.griddynamics.jagger.util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class TpsTest {

    public static void main(String[] args) {
        DefaultWorkloadSuggestionMaker workloadSuggestionMaker = new DefaultWorkloadSuggestionMaker(10);

        BigDecimal desired = new BigDecimal(1000000000);
        int maxThreads = 100;
        NodeTpsRecorder stats = new NodeTpsRecorder(5);
//        TpsGenerator tpsGenerator = new PowTpsGenerator(3);
        TpsGenerator tpsGenerator = new RandomFactor(new PowTpsGenerator(2));

        StubCreator stubCreator = new StubCreator(100L, tpsGenerator);


        stats.recordStatus(0, 0, 0, 0);

        for (int i = 0; i < 2000; i++) {
            WorkloadConfiguration suggest = workloadSuggestionMaker.suggest(desired, stats, maxThreads);
            Pair<Long, Long> generate = stubCreator.create(suggest);

            stats.recordStatus(suggest.getThreads(), suggest.getDelay(), generate.getFirst(), generate.getSecond());
        }

        Map<Long, Pair<WorkloadConfiguration, BigDecimal>> tpsHistory = stats.getTpsHistory();

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (Pair<WorkloadConfiguration, BigDecimal> pair : tpsHistory.values()) {
            count++;
            BigDecimal second = pair.getSecond();
            total = total.add(second);
        }

        BigDecimal avg = total.divide(new BigDecimal(count), 3, RoundingMode.HALF_UP);
        System.out.println("avg " + avg);

    }
}
