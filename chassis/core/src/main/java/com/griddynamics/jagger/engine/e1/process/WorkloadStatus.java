package com.griddynamics.jagger.engine.e1.process;/*
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

import java.io.Serializable;

/**
 * @author Nikolay Musienko
 *         Date: 26.06.13
 */
public class WorkloadStatus implements Serializable {
    private final int startedSamples;
    private final int finishedSamples;
    private final int currentThreadNumber;
    private final long emptyTransactions;

    public WorkloadStatus(int startedSamples, int finishedSamples, int currentThreadNumber, long emptyTransactions) {
        this.startedSamples = startedSamples;
        this.finishedSamples = finishedSamples;
        this.currentThreadNumber = currentThreadNumber;
        this.emptyTransactions = emptyTransactions;
    }

    public int getStartedSamples() {
        return startedSamples;
    }

    public int getFinishedSamples() {
        return finishedSamples;
    }

    public int getCurrentThreadNumber() {
        return currentThreadNumber;
    }
    
    public long getEmptyTransactions() {
        return emptyTransactions;
    }
    
    @Override
    public String toString() {
        return "WorkloadStatus{" + "startedSamples=" + startedSamples + ", finishedSamples=" + finishedSamples
               + ", currentThreadNumber=" + currentThreadNumber + ", emptyTransactions=" + emptyTransactions + '}';
    }
}
