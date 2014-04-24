/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
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

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes to log statuses during load invocation.
 *
 * @param <Q> query type
 * @param <R> response type
 * @param <E> endpoint type
 * @author Mairbek Khadikov
 */
public class LoadInvocationLogger<Q, R, E> implements LoadInvocationListener<Q, R, E> {
    private static final Logger log = LoggerFactory.getLogger(LoadInvocationLogger.class);

    public static <Q, R, E> LoadInvocationLogger<Q, R, E> create() {
        return new LoadInvocationLogger<Q, R, E>();
    }

    private LoadInvocationLogger() {

    }

    @Override
    public void onStart(Q query, E endpoint) {
        log.debug("Going to doTransaction query {} on endpoint {}", query, endpoint);
    }

    @Override
    public void onSuccess(Q query, E endpoint, R result, long duration) {
        log.debug("Invoked query {} on endpoint {} result {} duration {}", new Object[] {query, endpoint, result, duration});
    }

    @Override
    public void onFail(Q query, E endpoint, InvocationException e) {
        log.warn("Invocation failed on {} error {}", new Object[] {endpoint, e.getMessage()});
    }

    @Override
    public void onError(Q query, E endpoint, Throwable error) {
        log.warn("Invocation failed on {} error {} \n{}", new Object[] {endpoint, error.getMessage(), Throwables.getStackTraceAsString(error)});
    }
}
