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

package com.griddynamics.jagger.invoker;

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.util.Pair;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class RoundRobinLoadBalancerTest {
    @Test
    public void shouldProvidePairs() throws Exception {
        List<Integer> queries = ImmutableList.of(1, 2, 3);
        List<Integer> endpoints = ImmutableList.of(1, 2);
        RoundRobinLoadBalancer<Integer, Integer> balancer = new RoundRobinLoadBalancer<Integer, Integer>(queries, endpoints);
        Iterator<Pair<Integer, Integer>> chunks = balancer.provide();

        assertEquals(chunks.next(), Pair.of(1, 1));
        assertEquals(chunks.next(), Pair.of(2, 2));
        assertEquals(chunks.next(), Pair.of(3, 1));
        assertEquals(chunks.next(), Pair.of(1, 2));
        assertEquals(chunks.next(), Pair.of(2, 1));
        assertEquals(chunks.next(), Pair.of(3, 2));
    }

    @Test
    public void shouldProvidePairsForOneEndpoint() throws Exception {
        List<Integer> queries = ImmutableList.of(1, 2, 3);
        List<Integer> endpoints = ImmutableList.of(1);
        RoundRobinLoadBalancer<Integer, Integer> balancer = new RoundRobinLoadBalancer<Integer, Integer>(queries, endpoints);
        Iterator<Pair<Integer, Integer>> chunks = balancer.provide();

        assertEquals(chunks.next(), Pair.of(1, 1));
        assertEquals(chunks.next(), Pair.of(2, 1));
        assertEquals(chunks.next(), Pair.of(3, 1));
        assertEquals(chunks.next(), Pair.of(1, 1));
        assertEquals(chunks.next(), Pair.of(2, 1));
        assertEquals(chunks.next(), Pair.of(3, 1));
    }

    @Test
    public void shouldProvidePairsForOneQuery() throws Exception {
        List<Integer> queries = ImmutableList.of(1);
        List<Integer> endpoints = ImmutableList.of(1, 2, 3, 4);
        RoundRobinLoadBalancer<Integer, Integer> balancer = new RoundRobinLoadBalancer<Integer, Integer>(queries, endpoints);
        Iterator<Pair<Integer, Integer>> chunks = balancer.provide();

        assertEquals(chunks.next(), Pair.of(1, 1));
        assertEquals(chunks.next(), Pair.of(1, 2));
        assertEquals(chunks.next(), Pair.of(1, 3));
        assertEquals(chunks.next(), Pair.of(1, 4));
        assertEquals(chunks.next(), Pair.of(1, 1));
        assertEquals(chunks.next(), Pair.of(1, 2));
        assertEquals(chunks.next(), Pair.of(1, 3));
        assertEquals(chunks.next(), Pair.of(1, 4));
    }

}
