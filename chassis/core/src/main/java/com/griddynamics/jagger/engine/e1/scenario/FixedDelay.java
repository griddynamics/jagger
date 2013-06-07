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

package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Required;

public class FixedDelay implements InvocationDelayConfiguration {
    private int delay;

    public FixedDelay(int delay) {
        this.delay = delay;
    }

    public FixedDelay() {
    }

    public static FixedDelay noDelay() {
        return new FixedDelay(0);
    }

    @Override
    public InvocationDelay getInvocationDelay() {
        return new FixedInvocationDelay(delay);
    }

    @Required
    public void setDelay(int delay) {
        Preconditions.checkArgument(delay >= 0, "delay should be >= 0");
        this.delay = delay;
    }

    private static class FixedInvocationDelay implements InvocationDelay {
        private final int delay;

        public FixedInvocationDelay(int delay) {
            this.delay = delay;
        }

        @Override
        public int getValue() {
            return delay;
        }

        @Override
        public String toString() {
            return "FixedInvocationDelay{" +
                    "delay=" + delay +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "fixed " + delay + "ms";
    }
}
