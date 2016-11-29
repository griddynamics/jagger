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

import java.io.Serializable;

public class CalibrationInfo<Q, E, R> implements Serializable {
    private final Q query;
    private final E endpoint;
    private final R result;

    public static <Q, E, R> CalibrationInfo<Q, E, R> create(Q query, E endpoint, R result) {
        return new CalibrationInfo<Q, E, R>(query, endpoint, result);
    }

    private CalibrationInfo(Q query, E endpoint, R result) {
        this.query = query;
        this.endpoint = endpoint;
        this.result = result;
    }

    public Q getQuery() {
        return query;
    }

    public E getEndpoint() {
        return endpoint;
    }

    public R getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "CalibrationInfo{" +
                "query=" + query +
                ", endpoint=" + endpoint +
                ", result=" + result +
                '}';
    }

}
