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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class CircularSupplier<T> implements Serializable {
    private final Logger log = LoggerFactory.getLogger(CircularSupplier.class);

    private final ImmutableList<T> list;
    private int index = 0;

    public static <T> CircularSupplier<T> create(Iterable<T> iterable) {
        return new CircularSupplier<T>(ImmutableList.copyOf(iterable));
    }

    public static <T> CircularSupplier<T> create(T... instances) {
        return create(ImmutableList.copyOf(instances));
    }

    private CircularSupplier(Iterable<T> iterable) {
        if (!iterable.iterator().hasNext()) {
            throw new IllegalArgumentException("Empty iterator passed");
        }

        this.list = ImmutableList.copyOf(iterable);
    }

    public T pop() {
        log.debug("Pop method called");
        T result = peek();

        if (exceeded()) {
            index = 0;
        } else {
            index++;
        }

        log.debug("result {} index changed to {}", result, index);
        return result;
    }

    public T peek() {
        log.debug("peek called list {} index {}", list, index);
        return list.get(index);
    }

    public boolean exceeded() {
        return index == (list.size() - 1);
    }

    public int size() {
        return list.size();
    }

    @Override
    public String toString() {
        return "CircularSupplier{" +
                "list=" + list +
                '}';
    }
}
