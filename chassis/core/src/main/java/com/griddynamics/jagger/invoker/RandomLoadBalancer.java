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

import com.google.common.collect.AbstractIterator;
import com.griddynamics.jagger.util.Pair;
import java.util.Iterator;
import java.util.Random;

public class RandomLoadBalancer<Q, E> extends PairSupplierFactoryLoadBalancer<Q, E> {

    private long randomSeed;

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    @Override
    public Iterator<Pair<Q, E>> provide() {

        return new AbstractIterator<Pair<Q,E>>() {

            private PairSupplier<Q, E> pairs = getPairSupplier();
            private int size = pairs.size();
            private Random random = new Random(randomSeed++);

            @Override
            protected Pair<Q, E> computeNext() {
                return pairs.get(random.nextInt(size));
            }

            @Override
            public String toString() {
                return "RandomLoadBalancer iterator";
            }
        };
    }
}
