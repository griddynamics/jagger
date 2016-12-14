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

package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.griddynamics.jagger.util.Pair;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

public class DefaultWorkloadSuggestionMakerTest {

//    @Test
//    public void shouldMakeSuggestionForEmptyStats() throws Exception {
//
//        DefaultWorkloadSuggestionMaker suggestionMaker = defaultSuggestionMaker();
//
//        Table<Integer, Integer, Pair<Long, BigDecimal>> table = HashBasedTable.create();
//        table.put(0, 0, Pair.of(1L, BigDecimal.ZERO));
//        NodeTpsStatistics statistics = statsOf(table, WorkloadConfiguration.with(0, 0));
//        WorkloadConfiguration suggest = suggestionMaker.suggest(new BigDecimal(5), statistics, 500);
//        assertThat(suggest, equalTo(WorkloadConfiguration.with(1, 0)));
//    }
//
//    @Test
//    public void shouldMakeSuggestionForExactPoint() throws Exception {
//
//        DefaultWorkloadSuggestionMaker suggestionMaker = defaultSuggestionMaker();
//
//        Table<Integer, Integer, Pair<Long, BigDecimal>> table = HashBasedTable.create();
//        table.put(0, 0, Pair.of(1L, BigDecimal.ZERO));
//        table.put(1, 0, Pair.of(2L, BigDecimal.ZERO));
//        table.put(5, 0, Pair.of(3L, BigDecimal.ZERO));
//        NodeTpsStatistics statistics = statsOf(table, WorkloadConfiguration.with(5, 0));
//        WorkloadConfiguration suggest = suggestionMaker.suggest(new BigDecimal(6), statistics, 500);
//        assertThat(suggest, equalTo(WorkloadConfiguration.with(5, 0)));
//    }
//
//    @Test
//    public void shouldMakeSuggestionForMiddlePoint() throws Exception {
//
//        DefaultWorkloadSuggestionMaker suggestionMaker = defaultSuggestionMaker();
//
//        Table<Integer, Integer, Pair<Long, BigDecimal> table = HashBasedTable.create();
//        table.put(0, 0, BigDecimal.ZERO);
//        table.put(1, 0, BigDecimal.ONE);
//        table.put(6, 0, new BigDecimal(6));
//        NodeTpsStatistics statistics = statsOf(table, WorkloadConfiguration.with(6, 0));
//        WorkloadConfiguration suggest = suggestionMaker.suggest(new BigDecimal(3), statistics, 500);
//        assertThat(suggest, equalTo(WorkloadConfiguration.with(3, 0)));
//    }
//
//    @Test
//    public void shouldMakeSuggestionForUpperBoundedPoint() throws Exception {
//
//        DefaultWorkloadSuggestionMaker suggestionMaker = defaultSuggestionMaker();
//
//        Table<Integer, Integer, BigDecimal> table = HashBasedTable.create();
//        table.put(0, 0, BigDecimal.ZERO);
//        table.put(1, 0, new BigDecimal(10));
//        table.put(7, 0, new BigDecimal(70));
//        NodeTpsStatistics statistics = statsOf(table, WorkloadConfiguration.with(7, 0));
//        WorkloadConfiguration suggest = suggestionMaker.suggest(new BigDecimal(39), statistics, 500);
//        assertThat(suggest, equalTo(WorkloadConfiguration.with(4, 0)));
//    }
//
//    @Test
//    public void shouldMakeSuggestionForFastTps() throws Exception {
//
//        DefaultWorkloadSuggestionMaker suggestionMaker = defaultSuggestionMaker();
//
//        Table<Integer, Integer, BigDecimal> table = HashBasedTable.create();
//        table.put(0, 0, BigDecimal.ZERO);
//        table.put(1, 0, new BigDecimal(60));
//        NodeTpsStatistics statistics = statsOf(table, WorkloadConfiguration.with(1, 0));
//        WorkloadConfiguration suggest = suggestionMaker.suggest(new BigDecimal(30), statistics, 500);
//        assertThat(suggest, equalTo(WorkloadConfiguration.with(1, 16)));
//    }
//
//    @Test
//    public void shouldMakeSecondSuggestionForFastTps() throws Exception {
//
//        DefaultWorkloadSuggestionMaker suggestionMaker = defaultSuggestionMaker();
//
//        Table<Integer, Integer, BigDecimal> table = HashBasedTable.create();
//        table.put(0, 0, BigDecimal.ZERO);
//        table.put(1, 0, new BigDecimal(60));
//        table.put(1, 200, new BigDecimal(50));
//        NodeTpsStatistics statistics = statsOf(table, WorkloadConfiguration.with(1, 200));
//        WorkloadConfiguration suggest = suggestionMaker.suggest(new BigDecimal(30), statistics, 500);
//        assertThat(suggest, equalTo(WorkloadConfiguration.with(1, 600)));
//    }
//
//    @Test
//    public void shouldMakeThirdSuggestionForFastTps() throws Exception {
//
//        DefaultWorkloadSuggestionMaker suggestionMaker = defaultSuggestionMaker();
//
//        Table<Integer, Integer, BigDecimal> table = HashBasedTable.create();
//        table.put(0, 0, BigDecimal.ZERO);
//        table.put(1, 0, new BigDecimal(60));
//        table.put(1, 200, new BigDecimal(50));
//        table.put(1, 400, new BigDecimal(45));
//        NodeTpsStatistics statistics = statsOf(table, WorkloadConfiguration.with(1, 400));
//        WorkloadConfiguration suggest = suggestionMaker.suggest(new BigDecimal(30), statistics, 500);
//        assertThat(suggest, equalTo(WorkloadConfiguration.with(1, 1000)));
//    }
//
//    @Test
//    public void shouldRetryToGatherInitialStatistics() throws Exception {
//
//        DefaultWorkloadSuggestionMaker suggestionMaker = defaultSuggestionMaker();
//
//        Table<Integer, Integer, BigDecimal> table = HashBasedTable.create();
//        table.put(0, 0, BigDecimal.ZERO);
//        table.put(1, 0, BigDecimal.ZERO);
//        NodeTpsStatistics statistics = statsOf(table, WorkloadConfiguration.with(1, 0));
//        WorkloadConfiguration suggest = suggestionMaker.suggest(new BigDecimal(30), statistics, 500);
//        assertThat(suggest, equalTo(WorkloadConfiguration.with(1, 0)));
//    }
//
//    private NodeTpsStatistics statsOf(Table<Integer, Integer, Pair<Long, BigDecimal>> table, WorkloadConfiguration currentConfig) {
//        NodeTpsStatistics statistics = Mockito.mock(NodeTpsStatistics.class);
//        when(statistics.getThreadDelayStats()).thenReturn(table);
//        when(statistics.getCurrentWorkloadConfiguration()).thenReturn(currentConfig);
//        return statistics;
//    }
//
//    private static DefaultWorkloadSuggestionMaker defaultSuggestionMaker() {
//        return new DefaultWorkloadSuggestionMaker(20);
//    }
}
