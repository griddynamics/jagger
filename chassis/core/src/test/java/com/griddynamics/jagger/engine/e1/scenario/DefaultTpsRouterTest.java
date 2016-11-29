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

import com.google.common.collect.ImmutableMap;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.util.JavaSystemClock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;

import static com.griddynamics.jagger.util.DecimalUtil.areEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultTpsRouterTest {
    private static final NodeId FIRST_NODE = NodeId.kernelNode("1");
    private static final NodeId SECOND_NODE = NodeId.kernelNode("2");

    private DefaultTpsRouter tpsRouter;
    private MaxTpsCalculator maxTpsCalculator;

    @BeforeMethod
    public void setUp() throws Exception {
        maxTpsCalculator = mock(MaxTpsCalculator.class);
    }

    @Test
    public void shouldRouteForOneNodeWithEmptyStats() throws Exception {
        tpsRouter = router();

        assertThat(tpsRouter.getDesiredTps(), equalTo(BigDecimal.TEN));
        Map<NodeId, NodeTpsStatistics> map = ImmutableMap.<NodeId, NodeTpsStatistics>builder().put(FIRST_NODE, emptyStats()).build();
        Map<NodeId, BigDecimal> desiredTpsPerNode = tpsRouter.getDesiredTpsPerNode(map);

        assertThat(desiredTpsPerNode.size(), is(1));
        assertThat(desiredTpsPerNode.containsKey(FIRST_NODE), is(true));

        // todo create matcher!
        assertThat(areEqual(desiredTpsPerNode.get(FIRST_NODE), BigDecimal.TEN), is(true));
    }

    private DefaultTpsRouter router() {
        return new DefaultTpsRouter(new ConstantTps(BigDecimal.TEN), maxTpsCalculator, new JavaSystemClock());
    }

    @Test
    public void shouldRouteForTwoNodesWithEmptyStats() throws Exception {
        tpsRouter = router();

        assertThat(tpsRouter.getDesiredTps(), equalTo(BigDecimal.TEN));
        Map<NodeId, NodeTpsStatistics> statsPerNode = ImmutableMap.<NodeId, NodeTpsStatistics>builder()
                .put(FIRST_NODE, emptyStats())
                .put(SECOND_NODE, emptyStats())
                .build();
        Map<NodeId, BigDecimal> desiredTpsPerNode = tpsRouter.getDesiredTpsPerNode(statsPerNode);

        assertThat(desiredTpsPerNode.size(), is(2));
        assertThat(desiredTpsPerNode.containsKey(FIRST_NODE), is(true));
        assertThat(desiredTpsPerNode.containsKey(SECOND_NODE), is(true));

        assertThat(areEqual(desiredTpsPerNode.get(FIRST_NODE), new BigDecimal(5)), is(true));
        assertThat(areEqual(desiredTpsPerNode.get(SECOND_NODE), new BigDecimal(5)), is(true));
    }

    @Test
    public void shouldRouteForTwoFastNodes() throws Exception {
        tpsRouter = router();

        assertThat(tpsRouter.getDesiredTps(), equalTo(BigDecimal.TEN));
        NodeTpsStatistics fastStats = mock(NodeTpsStatistics.class);
        Map<NodeId, NodeTpsStatistics> statsPerNode = ImmutableMap.<NodeId, NodeTpsStatistics>builder()
                .put(FIRST_NODE, fastStats)
                .put(SECOND_NODE, fastStats)
                .build();

        when(maxTpsCalculator.getMaxTps(fastStats)).thenReturn(new BigDecimal(11));
        Map<NodeId, BigDecimal> desiredTpsPerNode = tpsRouter.getDesiredTpsPerNode(statsPerNode);

        assertThat(desiredTpsPerNode.size(), is(2));
        assertThat(desiredTpsPerNode.containsKey(FIRST_NODE), is(true));
        assertThat(desiredTpsPerNode.containsKey(SECOND_NODE), is(true));

        assertThat(areEqual(desiredTpsPerNode.get(FIRST_NODE), new BigDecimal(5)), is(true));
        assertThat(areEqual(desiredTpsPerNode.get(SECOND_NODE), new BigDecimal(5)), is(true));
    }

    @Test
    public void shouldRouteForTwoSlowNodes() throws Exception {
        tpsRouter = router();

        assertThat(tpsRouter.getDesiredTps(), equalTo(BigDecimal.TEN));
        NodeTpsStatistics slowStats = mock(NodeTpsStatistics.class);

        Map<NodeId, NodeTpsStatistics> statsPerNode = ImmutableMap.<NodeId, NodeTpsStatistics>builder()
                .put(FIRST_NODE, slowStats)
                .put(SECOND_NODE, slowStats)
                .build();

        when(maxTpsCalculator.getMaxTps(slowStats)).thenReturn(new BigDecimal(2));
        Map<NodeId, BigDecimal> desiredTpsPerNode = tpsRouter.getDesiredTpsPerNode(statsPerNode);

        assertThat(desiredTpsPerNode.size(), is(2));
        assertThat(desiredTpsPerNode.containsKey(FIRST_NODE), is(true));
        assertThat(desiredTpsPerNode.containsKey(SECOND_NODE), is(true));

        assertThat(areEqual(desiredTpsPerNode.get(FIRST_NODE), new BigDecimal(2)), is(true));
        assertThat(areEqual(desiredTpsPerNode.get(SECOND_NODE), new BigDecimal(2)), is(true));
    }

    @Test
    public void shouldBalanceWorkWhenOneNodeIsSlow() throws Exception {
        tpsRouter = router();

        assertThat(tpsRouter.getDesiredTps(), equalTo(BigDecimal.TEN));
        NodeTpsStatistics slowStats = mock(NodeTpsStatistics.class);
        NodeTpsStatistics fastStats = mock(NodeTpsStatistics.class);

        Map<NodeId, NodeTpsStatistics> statsPerNode = ImmutableMap.<NodeId, NodeTpsStatistics>builder()
                .put(FIRST_NODE, slowStats)
                .put(SECOND_NODE, fastStats)
                .build();

        when(maxTpsCalculator.getMaxTps(slowStats)).thenReturn(new BigDecimal(2));
        when(maxTpsCalculator.getMaxTps(fastStats)).thenReturn(new BigDecimal(10));
        Map<NodeId, BigDecimal> desiredTpsPerNode = tpsRouter.getDesiredTpsPerNode(statsPerNode);

        assertThat(desiredTpsPerNode.size(), is(2));
        assertThat(desiredTpsPerNode.containsKey(FIRST_NODE), is(true));
        assertThat(desiredTpsPerNode.containsKey(SECOND_NODE), is(true));

        assertThat(areEqual(desiredTpsPerNode.get(FIRST_NODE), new BigDecimal(2)), is(true));
        assertThat(areEqual(desiredTpsPerNode.get(SECOND_NODE), new BigDecimal(8)), is(true));
    }

    private NodeTpsRecorder emptyStats() {
        NodeTpsRecorder nodeTpsRecorder = new NodeTpsRecorder(100);
        nodeTpsRecorder.recordStatus(0, 0, 0, 1L);
        return nodeTpsRecorder;
    }
}
