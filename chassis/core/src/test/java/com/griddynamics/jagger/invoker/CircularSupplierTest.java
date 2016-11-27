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
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


public class CircularSupplierTest {

    @Test
    public void shouldReturnValuesCircular() {
        CircularSupplier<Integer> circularSupplier = CircularSupplier.create(1, 2);

        int first = circularSupplier.pop();
        assertEquals(first, 1);

        int second = circularSupplier.pop();
        assertEquals(second, 2);


        int third = circularSupplier.pop();
        assertEquals(third, 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenEmptyIterablePassed() throws Exception {
        CircularSupplier.create(ImmutableList.<Object>of());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenEmptyVarargPassed() throws Exception {
        CircularSupplier.create();
    }

    @Test
    public void shouldReturnSameItemForOneElement() {
        CircularSupplier<Integer> circularSupplier = CircularSupplier.create(1);

        int first = circularSupplier.pop();
        int second = circularSupplier.pop();

        assertEquals(first, 1);
        assertEquals(second, 1);
    }

    @Test
    public void shouldBeExceededForOneElement() {
        CircularSupplier<Integer> circularSupplier = CircularSupplier.create(1);

        assertTrue(circularSupplier.exceeded());
    }

    @Test
    public void shouldBeExceededForTwoElements() {
        CircularSupplier<Integer> circularSupplier = CircularSupplier.create(1, 2);

        assertFalse(circularSupplier.exceeded());

        circularSupplier.pop();

        assertTrue(circularSupplier.exceeded());
    }

    @Test
    public void shouldPeekElementAndNotShiftTheIterator() throws Exception {
        CircularSupplier<Integer> circularSupplier = CircularSupplier.create(1, 2);
        int peek = circularSupplier.peek();
        int pop = circularSupplier.pop();

        assertEquals(peek, 1);
        assertEquals(pop, 1);
    }
}
