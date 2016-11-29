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

import com.google.common.collect.Sets;
import com.griddynamics.jagger.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Alexey Kiselyov
 *         Date: 28.07.11
 */
public class WatchableSet<N extends CoordinatorNode> implements Set<N> {
    private static final Logger log = LoggerFactory.getLogger(WatchableSet.class);

    private final Set<ContainerWatcher> watchers;
    private final Set<N> container;

    public WatchableSet(Class<Set<N>> clazzContainer, ContainerWatcher... containerWatchers) {
        try {
            this.container = clazzContainer.newInstance();
        } catch (Exception e) {
            log.error("Instantiation error ", e);
            throw new TechnicalException(e);
        }
        this.watchers = Collections.synchronizedSet(Sets.newHashSet(containerWatchers));
    }


    public void addWatcher(ContainerWatcher watcher) {
        this.watchers.add(watcher);
    }

    @Override
    public int size() {
        for (ContainerWatcher watcher : watchers) {
            watcher.size(); // value does not matter
        }
        return this.container.size();
    }

    @Override
    public boolean isEmpty() {
        for (ContainerWatcher watcher : watchers) {
            watcher.isEmpty(); // value does not matter
        }
        return this.container.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        for (ContainerWatcher watcher : watchers) {
            watcher.contains(o); // value does not matter
        }
        return this.container.contains(o);
    }

    @Override
    public Iterator<N> iterator() {
        for (ContainerWatcher watcher : watchers) {
            watcher.iterator(); // value does not matter
        }
        return this.container.iterator();
    }

    @Override
    public Object[] toArray() {
        for (ContainerWatcher watcher : watchers) {
            watcher.toArray(); // value does not matter
        }
        return this.container.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        for (ContainerWatcher watcher : watchers) {
            watcher.toArray(a); // value does not matter
        }
        return this.container.toArray(a);
    }

    @Override
    public boolean add(N n) {
        for (ContainerWatcher watcher : watchers) {
            watcher.add(n); // value does not matter
        }
        return this.container.add(n);
    }

    @Override
    public boolean remove(Object o) {
        for (ContainerWatcher watcher : watchers) {
            watcher.remove(o); // value does not matter
        }
        return this.container.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (ContainerWatcher watcher : watchers) {
            watcher.containsAll(c); // value does not matter
        }
        return this.container.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends N> c) {
        for (ContainerWatcher watcher : watchers) {
            watcher.addAll(c); // value does not matter
        }
        return this.container.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        for (ContainerWatcher watcher : watchers) {
            watcher.retainAll(c); // value does not matter
        }
        return this.container.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (ContainerWatcher watcher : watchers) {
            watcher.removeAll(c); // value does not matter
        }
        return this.container.removeAll(c);
    }

    @Override
    public void clear() {
        for (ContainerWatcher watcher : watchers) {
            watcher.clear();
        }
        this.container.clear();
    }
}