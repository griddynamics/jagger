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

package com.griddynamics.jagger.invoker.stubs;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Sleeps for specified time
 * @author Dmitry Kotlyarov
 * @n
 * @par Details:
 * @details It is no matter what type you select for query, endpoint and result. This kind of invoker has no logic. It is very helpful when you would like to create a pause between tests or you are waiting when service will be available.
 *
 * @param <Q> - Query type
 * @param <R> - Result type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Invokers_group */
@Deprecated
public class WaitingInvoker<Q, R, E> implements Invoker<Q, R, E> {
    private static final Logger log = LoggerFactory.getLogger(WaitingInvoker.class);

    private final R result;
    private final int sleepMs;

    /** Make an invocation to target
     * @author Dmitry Kotlyarov
     * @n
     * @param result - an object, which will returns as the result of invocation
     * @param sleepMs - time for sleeping*/
    public WaitingInvoker(R result, int sleepMs) {
        this.result = result;
        this.sleepMs = sleepMs;
    }

    /** Sleep for exact time
     * @author Mairbek Khadikov
     * @n
     * @param query    - some query
     * @param endpoint - some endpoint
     *
     * @return invocation result
     * @throws InvocationException when invocation failed */
    @Override
    public R invoke(Q query, E endpoint) throws InvocationException {
        log.debug("Invoked query {} on endpoint {}", query, endpoint);
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            log.error("Error", e);
        }
        return result;
    }
}
