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

import com.griddynamics.jagger.util.Pair;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class PairSupplierTest {

    OneByOnePairSupplier<String, Integer> oneByOnePairSupplier;
    RoundRobinPairSupplier<String, Integer> roundRobinPairSupplier;

    @BeforeClass
    public void setUp() {
        Iterable<String> queryProvider = Arrays.asList("first", "second", "third");
        Iterable<Integer> endpointProvider = Arrays.asList(1, 2);
        oneByOnePairSupplier = OneByOnePairSupplier.create(queryProvider, endpointProvider);
        roundRobinPairSupplier = RoundRobinPairSupplier.create(queryProvider, endpointProvider);
    }

    @Test
    public void roundRobinPairSupplierSequenceTest() {
        assertEquals(roundRobinPairSupplier.get(0), Pair.of("first",  1));
        assertEquals(roundRobinPairSupplier.get(1), Pair.of("second", 2));
        assertEquals(roundRobinPairSupplier.get(2), Pair.of("third",  1));
        assertEquals(roundRobinPairSupplier.get(3), Pair.of("first",  2));
        assertEquals(roundRobinPairSupplier.get(4), Pair.of("second", 1));
        assertEquals(roundRobinPairSupplier.get(5), Pair.of("third",  2));
        assertEquals(roundRobinPairSupplier.get(0), Pair.of("first",  1));
        assertEquals(roundRobinPairSupplier.get(1), Pair.of("second", 2));
    }

    @Test
    public void roundRobinPairSupplierSizeTest() {
        assertEquals(roundRobinPairSupplier.size(), 6 , "Wrong size of list in Par Supplier");
    }

    @Test
    public void oneByOnePairSupplierSequenceTest() {

        assertEquals(oneByOnePairSupplier.get(0), Pair.of("first",  1));
        assertEquals(oneByOnePairSupplier.get(1), Pair.of("first",  2));
        assertEquals(oneByOnePairSupplier.get(2), Pair.of("second", 1));
        assertEquals(oneByOnePairSupplier.get(3), Pair.of("second", 2));
        assertEquals(oneByOnePairSupplier.get(4), Pair.of("third",  1));
        assertEquals(oneByOnePairSupplier.get(5), Pair.of("third",  2));
        assertEquals(oneByOnePairSupplier.get(0), Pair.of("first",  1));
        assertEquals(oneByOnePairSupplier.get(1), Pair.of("first",  2));
    }

    @Test
    public void oneByOnePairSupplierSizeTest() {
        assertEquals(oneByOnePairSupplier.size(), 6 , "Wrong size of list in Par Supplier");
    }
}
