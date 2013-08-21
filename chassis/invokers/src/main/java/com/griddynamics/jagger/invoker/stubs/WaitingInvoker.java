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

package com.griddynamics.jagger.invoker.stubs;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ??? Some short description
 * @author ???
 * @n
 * @par Details:
 * @details ???
 *
 * @param <Q> -???
 * @param <R> -???
 * @param <E> -???
 *
 * @ingroup Main_Invokers_group */
public class WaitingInvoker<Q, R, E> implements Invoker<Q, R, E> {
    private static final Logger log = LoggerFactory.getLogger(WaitingInvoker.class);

    private final R result;
    private final int sleepMs;

    public WaitingInvoker(R result, int sleepMs) {
        this.result = result;
        this.sleepMs = sleepMs;
    }

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
