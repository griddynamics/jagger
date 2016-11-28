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

package com.griddynamics.jagger.cloudshaper

class StandardTriggers {
    static def noCondition = {
        return true
    }

    static def onceWithDelay = {
        {
            delay ->
            long targetDelay = delay;
            boolean executed = false;
            {
                context ->
                boolean result = false;
                if(!executed && (context.currentTime - context.absoluteStartTime) >= targetDelay) {
                    result = true;
                    executed = true;
                }
                return result;
            }
        }
    }

    static def onceAtTime = {
        {
            time ->
            long targetTime = time;
            boolean executed = false;
            {
                context ->
                boolean result = false;
                if(!executed && context.currentTime >= time) {
                    result = true;
                    executed = true;
                }
                return result;
            }
        }
    }

    static def withPeriod = {
        {
            period ->
            long targetPeriod = period;
            long lastExecutionTime = 0;
            {
                context ->
                if(lastExecutionTime == 0) lastExecutionTime = context.absoluteStartTime
                if(context.currentTime - targetPeriod >= lastExecutionTime) {
                    lastExecutionTime = context.currentTime
                    return true
                }

                return false
            }
        }
    }
}
