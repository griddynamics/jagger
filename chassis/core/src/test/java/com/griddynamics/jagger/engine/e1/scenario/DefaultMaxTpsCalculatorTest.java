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
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;


public class DefaultMaxTpsCalculatorTest {
//
//    private DefaultMaxTpsCalculator maxTpsCalculator = new DefaultMaxTpsCalculator();
//
//    @Test
//    public void shouldNotCalculateMaxForOnePointStats() throws Exception {
//        Table<Integer, Integer, BigDecimal> table = HashBasedTable.create();
//        table.put(0, 0, BigDecimal.ZERO);
//
//        BigDecimal maxTps = maxTpsCalculator.getMaxTps(statsOf(table));
//
//        assertThat(maxTps, nullValue());
//
//    }
//
//    @Test
//    public void shouldNotCalculateMaxForTwoPointStats() throws Exception {
//        Table<Integer, Integer, BigDecimal> table = HashBasedTable.create();
//        table.put(0, 0, BigDecimal.ZERO);
//        table.put(1, 0, new BigDecimal(2));
//
//        BigDecimal maxTps = maxTpsCalculator.getMaxTps(statsOf(table));
//
//        assertThat(maxTps, nullValue());
//
//    }
//
//    @Test
//    public void shouldNotCalculateMaxForThreePointStats() throws Exception {
//        Table<Integer, Integer, BigDecimal> table = HashBasedTable.create();
//        table.put(0, 0, BigDecimal.ZERO);
//        table.put(1, 0, new BigDecimal(2));
//        table.put(2, 0, new BigDecimal(3));
//
//        BigDecimal maxTps = maxTpsCalculator.getMaxTps(statsOf(table));
//
//        assertThat(maxTps, nullValue());
//
//    }
//
//    @Test
//    public void shouldNotCalculateMaxForIncreasingFunction() throws Exception {
//        Table<Integer, Integer, BigDecimal> table = HashBasedTable.create();
//        table.put(0, 0, BigDecimal.ZERO);
//        table.put(1, 0, new BigDecimal(10));
//        table.put(2, 0, new BigDecimal(12));
//        table.put(3, 0, new BigDecimal(15));
//        table.put(4, 0, new BigDecimal(18));
//
//        BigDecimal maxTps = maxTpsCalculator.getMaxTps(statsOf(table));
//
//        assertThat(maxTps, nullValue());
//
//    }
//
//    @Test
//    public void shouldCalculateMaxForDecreasingFunction() throws Exception {
//        Table<Integer, Integer, BigDecimal> table = HashBasedTable.create();
//        table.put(0, 0, BigDecimal.ZERO);
//        table.put(1, 0, new BigDecimal(15));
//        table.put(2, 0, new BigDecimal(12));
//        table.put(3, 0, new BigDecimal(10));
//        table.put(4, 0, new BigDecimal(8));
//
//        BigDecimal maxTps = maxTpsCalculator.getMaxTps(statsOf(table));
//
//        assertThat(maxTps, equalTo(new BigDecimal(15)));
//
//    }
//
//    private NodeTpsStatistics statsOf(Table<Integer, Integer, BigDecimal> table) {
//        NodeTpsStatistics statistics = Mockito.mock(NodeTpsStatistics.class);
//        Mockito.when(statistics.getThreadDelayStats()).thenReturn(table);
//        return statistics;
//    }

}
