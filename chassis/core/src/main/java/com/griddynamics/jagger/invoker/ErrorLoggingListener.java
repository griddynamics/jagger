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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception-free implementation of {@see LoadInvocationListener}.
 * 
 * @author Mairbek Khadikov
 * 
 * @param <Q>
 *            query type
 * @param <R>
 *            result type
 * @param <E>
 *            endpoint type
 */
public class ErrorLoggingListener<Q, R, E> implements LoadInvocationListener<Q, R, E> {
	private static final Logger log = LoggerFactory.getLogger(ErrorLoggingListener.class);

	private final LoadInvocationListener<Q, R, E> delegate;

	public ErrorLoggingListener(LoadInvocationListener<Q, R, E> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onStart(Q query, E endpoint) {
		try {
			delegate.onStart(query, endpoint);
		} catch (Throwable throwable) {
			log.error("Error during listener execution", throwable);
		}
	}

	@Override
	public void onSuccess(Q query, E endpoint, R result, long duration) {
		try {
			delegate.onSuccess(query, endpoint, result, duration);
		} catch (Throwable throwable) {
			log.error("Error during listener execution", throwable);
		}
	}

	@Override
	public void onFail(Q query, E endpoint, InvocationException e) {
		try {
			delegate.onFail(query, endpoint, e);
		} catch (Throwable throwable) {
			log.error("Error during listener execution", throwable);
		}
	}

	@Override
	public void onError(Q query, E endpoint, Throwable error) {
		try {
			delegate.onError(query, endpoint, error);
		} catch (Throwable throwable) {
			log.error("Error during listener execution", throwable);
		}
	}

}
