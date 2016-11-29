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

class StandardCommands {
    static def clearNetwork = {
        context ->
        String.format("tc qdisc del dev %s root", context.hostConfiguration["netInterface"]);
    }

    static def emulateDelays = {
        delayFunction, deviationFunction ->
        {
            context ->
            String.format("tc qdisc add dev %s root netem delay %fms %fms", context.hostConfiguration["netInterface"], (Float)delayFunction(context), (Float)deviationFunction(context));
        }
    }

    static def emulateLosses = {
        lossPercentageFunction, correlationPercentageFunction ->
        {
            context ->
            String.format("tc qdisc change dev %s root netem %f %f", context.hostConfiguration["netInterface"], lossPercentageFunction(context), correlationPercentageFunction(context))
        }
    }
}
