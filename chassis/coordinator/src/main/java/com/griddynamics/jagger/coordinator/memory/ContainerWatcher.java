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

package com.griddynamics.jagger.coordinator.memory;

import com.griddynamics.jagger.coordinator.AbstractRemoteExecutor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Alexey Kiselyov
 *         Date: 28.07.11
 */
public interface ContainerWatcher<E> extends Set<E> {

    abstract class ModifiedContainerWatcher<E> implements ContainerWatcher<E> {

        @Override
        public int size() {
            return 0;  // do nothing
        }

        @Override
        public boolean isEmpty() {
            return false;  // do nothing
        }

        @Override
        public boolean contains(Object o) {
            return false;  // do nothing
        }

        @Override
        public Iterator iterator() {
            return null;  // do nothing
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;  // do nothing
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;  // do nothing
        }
    }
}
