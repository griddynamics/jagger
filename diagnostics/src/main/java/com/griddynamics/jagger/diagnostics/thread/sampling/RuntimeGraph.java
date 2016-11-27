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

package com.griddynamics.jagger.diagnostics.thread.sampling;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.primitives.Longs;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class RuntimeGraph implements Serializable {

    private static final Method pseudoRoot = new Method("#", "#");

    private long observations = 0;

    private DirectedGraph<MethodStatistics, Invocation> graph = new DirectedSparseGraph<MethodStatistics, Invocation>();
    private Map<Method, MethodStatistics> methodIndex = Maps.newHashMap();

    private List<Pattern> includePatterns;
    private List<Pattern> excludePatterns;

    private static Comparator<MethodStatistics> selfTimeMethodSelector = new Comparator<MethodStatistics>() {
        public int compare(MethodStatistics s1, MethodStatistics s2) {
            return -Longs.compare(s1.getOnTopObservations(), s2.getOnTopObservations());
        }
    };

    private static Comparator<MethodStatistics> frequencyMethodSelector = new Comparator<MethodStatistics>() {
        public int compare(MethodStatistics s1, MethodStatistics s2) {
            return -Longs.compare(s1.getObservations(), s2.getObservations());
        }
    };

    public synchronized void clean() {
        this.methodIndex.clear();
        locateMethodStatistics(pseudoRoot);
    }


    public RuntimeGraph() {
        clean();
    }

    public void registerSnapshot(List<Method> callTree) {

        if (callTree == null || callTree.size() == 0) {
            return;
        }

        Method caller = pseudoRoot;
        MethodStatistics method = null;
        for (Method methodId : callTree) {
            if (isAppropriate(methodId)) {
                method = locateMethodStatistics(methodId);
                method.registerObservation();

                Invocation invocation = locateInvocation(caller, methodId);
                invocation.registerObservation();

                caller = methodId;
            }
        }
        if (method != null) {
            method.registerOnTopObservation();
        }
        observations++;
    }

    private boolean isAppropriate(Method method) {
        boolean excluded = false;
        boolean included = false;

        if (excludePatterns != null) {
            for (Pattern pattern : excludePatterns) {
                if (method.matches(pattern)) {
                    excluded = true;
                    break;
                }
            }
        }

        if (includePatterns != null) {
            for (Pattern pattern : includePatterns) {
                if (method.matches(pattern)) {
                    included = true;
                    break;
                }
            }
        }

        if (includePatterns == null) {
            return !excluded;
        } else {
            return included || !excluded;
        }
    }

    public List<MethodProfile> getSelfTimeHotSpots(int maxSpots) {
        return getHotSpots(maxSpots, selfTimeMethodSelector);
    }

    public List<MethodProfile> getFrequentHotSpots(int maxSpots) {
        return getHotSpots(maxSpots, frequencyMethodSelector);
    }

    public Graph<MethodProfile, InvocationProfile> getNeighborhood(Method root, int maxCallDepth, int maxCallers) {
        DirectedGraph<MethodProfile, InvocationProfile> neighborhood = new DirectedSparseGraph<MethodProfile, InvocationProfile>();

        MethodStatistics rootStatistics = methodIndex.get(root);

        if (rootStatistics != null) {
            collectCallers(rootStatistics, maxCallDepth, maxCallers, neighborhood);
        }

        return neighborhood;
    }

    private void collectCallers(MethodStatistics root, int maxCallDepth, int maxCallers, Graph<MethodProfile, InvocationProfile> neighborhood) {
        if (maxCallDepth > 0) {
            Collection<MethodStatistics> callers = selectMethodStatistics(graph.getPredecessors(root), maxCallers, frequencyMethodSelector);

            MethodProfile rootProfile = assembleProfile(root);
            for (MethodStatistics caller : callers) {
                if (!caller.getMethod().equals(pseudoRoot)) {
                    MethodProfile callerProfile = assembleProfile(caller);
                    neighborhood.addEdge(assembleProfile(graph.findEdge(caller, root)), callerProfile, rootProfile);
                }
            }

            for (MethodStatistics caller : callers) {
                collectCallers(caller, maxCallDepth - 1, maxCallers, neighborhood);
            }
        }
    }

    private List<MethodProfile> getHotSpots(int maxSpots, Comparator<MethodStatistics> comparator) {
        List<MethodProfile> result = Lists.newArrayList();

        MinMaxPriorityQueue<MethodStatistics> hotSpots = MinMaxPriorityQueue
                .orderedBy(comparator)
                .maximumSize(maxSpots)
                .create(graph.getVertices());

        int queueSize = hotSpots.size();
        for (int i = 0; i < queueSize; i++) {
            result.add(assembleProfile(hotSpots.removeFirst()));
        }

        return result;
    }

    private List<MethodStatistics> selectMethodStatistics(Collection<MethodStatistics> statistics, int maxStatistics,
                                                          Comparator<MethodStatistics> comparator) {
        List<MethodStatistics> result = Lists.newArrayList();

        MinMaxPriorityQueue<MethodStatistics> selected = MinMaxPriorityQueue
                .orderedBy(comparator)
                .maximumSize(maxStatistics)
                .create(statistics);

        for (MethodStatistics method : selected) {
            result.add(method);
        }

        return result;
    }

    private MethodProfile assembleProfile(MethodStatistics method) {
        MethodProfile profile = new MethodProfile(method.getMethod());
        profile.setObservations(method.getObservations());
        profile.setInStackRatio(method.getObservations() / ((double) observations));
        profile.setOnTopRatio(method.getOnTopObservations() / ((double) observations));

        return profile;
    }

    private InvocationProfile assembleProfile(Invocation invocation) {
        InvocationProfile profile = new InvocationProfile();
        profile.setObservations(invocation.getObservations());

        return profile;
    }

    private MethodStatistics locateMethodStatistics(Method method) {
        MethodStatistics methodStatistics = methodIndex.get(method);
        if (methodStatistics == null) {
            methodStatistics = new MethodStatistics(method);
            methodIndex.put(method, methodStatistics);
            graph.addVertex(methodStatistics);
        }

        return methodStatistics;
    }

    private Invocation locateInvocation(Method caller, Method callee) {
        MethodStatistics callerMethod = locateMethodStatistics(caller);
        MethodStatistics calleeMethod = locateMethodStatistics(callee);

        Invocation invocation = graph.findEdge(callerMethod, calleeMethod);
        if (invocation == null) {
            invocation = new Invocation();
            graph.addEdge(invocation, callerMethod, calleeMethod);
        }

        return invocation;
    }

    public List<Pattern> getIncludePatterns() {
        return includePatterns;
    }

    public void setIncludePatterns(List<Pattern> includePatterns) {
        this.includePatterns = includePatterns;
    }

    public List<Pattern> getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(List<Pattern> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    public long getObservations() {
        return observations;
    }

    public int getMethodCount() {
        return graph.getVertexCount();
    }

    @Override
    public String toString() {
        return "RuntimeGraph{" +
                "observations=" + observations +
                ", graph=" + graph +
                ", methodIndex=" + methodIndex +
                ", includePatterns=" + includePatterns +
                ", excludePatterns=" + excludePatterns +
                '}';
    }
}