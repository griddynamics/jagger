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

package com.griddynamics.jagger.util;

import com.google.common.base.Equivalence;

/**
 * Contains static factory methods for creating {@code Equivalence} instances.
 *
 * @author Mairbek Khadikov
 */
public class Eq {

    private static final Equivalence ALWAYS_TRUE = new AlwaysTrue();
    private static final Equivalence ALWAYS_FALSE = new AlwaysFalse();

    private Eq() {
    }

    public static Equivalence<Object> alwaysTrue() {
        return ALWAYS_TRUE;
    }

    public static Equivalence<Object> alwaysFalse() {
        return ALWAYS_FALSE;
    }

    private static final class AlwaysTrue extends Equivalence {

        @Override
        protected boolean doEquivalent(Object a, Object b) {
            return true;
        }

        @Override
        protected int doHash(Object o) {
            return 0;
        }
    }

    private static final class AlwaysFalse extends Equivalence<Object> {

        @Override
        protected boolean doEquivalent(Object a, Object b) {
            return false;
        }

        @Override
        protected int doHash(Object o) {
            return 0;
        }
    }
}
