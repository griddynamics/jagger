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

package com.griddynamics.jagger.engine.e1.collector;

import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.CalibrationInfo;
import com.griddynamics.jagger.storage.fs.logging.LogReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/** Compare invocation result with calibration result
 * @author Dmitry Kotlyarov
 * @n
 * @par Details:
 * @details To use this validator you should set attribute of @xlink{test-description} @xlink{test-description,calibration} to true value.
 * It means when Jagger launch test it takes all pairs(of queries and endpoints) and creates an invocations with this pairs.
 * The results will be stored as calibration info. This describes how SuT behave without high load.
 * All invocations of tests will be compared with results from calibration info.
 *
 * @param <Q> - Query type
 * @param <R> - Result type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Collectors_group */
public class ConsistencyValidator<Q, E, R> extends ResponseValidator<Q, E, R> {
    private static final Logger log = LoggerFactory.getLogger(ConsistencyValidator.class);

    private final Equivalence<Q> queryEquivalence;
    private final Equivalence<E> endpointEquivalence;
    private final Equivalence<R> resultEquivalence;


    private /*lazy*/ Collection<CalibrationInfo<Q, E, R>> expected;
    private final Object lock = new Object();

    public ConsistencyValidator(String taskId, NodeContext kernelContext, String sessionId, Equivalence<Q> queryEquivalence, Equivalence<E> endpointEquivalence, Equivalence<R> resultEquivalence) {
        super(taskId, sessionId, kernelContext);
        this.queryEquivalence = queryEquivalence;
        this.endpointEquivalence = endpointEquivalence;
        this.resultEquivalence = resultEquivalence;
    }

    @Override
    public String getName() {
        return "Consistency Validator";
    }
    /** Returns true if invocation result equals calibration result
     * @author Dmitry Kotlyarov
     * @n
     *
     * @param query     - the query of current invocation
     * @param endpoint  - the endpoint of current invocation
     * @param result    - the result of invocation
     * @param duration  - the duration of invocation
     *
     * @return true if invocation result equals calibration result */
    @Override
    public boolean validate(Q query, E endpoint, R result, long duration) {
        if (getExpected().isEmpty()) {
            return false;
        }

        CalibrationInfo<Q, E, R> info = null;
        for (CalibrationInfo<Q, E, R> expected : getExpected()) {
            if (match(CalibrationInfo.create(query, endpoint, result), expected)) {
                info = expected;
                break;
            }
        }

        if (info == null) {
            log.warn("Cannot find calibration info for query {} and endpoint {}", query, endpoint);
            return false;
        }

        return resultEquivalence.equivalent(info.getResult(), result);
    }

    private boolean match(CalibrationInfo<Q, E, R> actual, CalibrationInfo<Q, E, R> expected) {
        if (!queryEquivalence.equivalent(actual.getQuery(), expected.getQuery())) {
            return false;
        }
        if (!endpointEquivalence.equivalent(actual.getEndpoint(), expected.getEndpoint())) {
            return false;
        }

        return true;
    }

    protected Collection<CalibrationInfo<Q, E, R>> getExpected() {
        if (expected == null) {
            synchronized (lock) {
                if (expected == null) {
                    expected = loadCalibrationInfo();
                }
            }
        }
        return expected;
    }

    @SuppressWarnings("unchecked")
    private Collection<CalibrationInfo<Q, E, R>> loadCalibrationInfo() {
        LogReader logReader = kernelContext.getService(LogReader.class);
        final String nodeId = kernelContext.getId().toString();
        LogReader.FileReader reader = null;
        try {
            reader = logReader.read(sessionId, taskId + "/" + "Calibration", "kernel", CalibrationInfo.class);
            return ImmutableList.copyOf(reader.iterator());
        } catch (Exception e) {
            log.warn("Calibration data not available. Skipping validation!");
            log.debug("Calibration info not found: {}: {}", e.getMessage(), e);
            return Lists.newLinkedList();
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
