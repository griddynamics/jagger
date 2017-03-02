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

import java.io.Serializable;

/** Responsible for action invocation on specified endpoint and query
 * @author Mairbek Khadikov
 * @n
 * @par Details:
 * @details Create a request to some target with specified query. The result of invocation can be collected by metrics and validators. Note that Invoker is used in multi thread environment, so realize thread-safe implementation @n
 *
 * @param <Q> - Query type
 * @param <R> - Result type
 * @param <E> - Endpoint type
 */
public interface Invoker<Q,R,E> extends Serializable {


	/** Makes an invocation to target
     * @author Mairbek Khadikov
     * @n
     * @par Details:
     * @details If method throw some exception current invocation will be marked as failed
     * @n
     * @param query    - input data for the invocation
	 * @param endpoint - endpoint
     *
     * @return invocation result
     * @throws InvocationException when invocation failed */
      R invoke(Q query, E endpoint) throws InvocationException;

}

/* Below is doxygen documentation for Jagger customization */

