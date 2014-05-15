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

package com.griddynamics.jagger.master;

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupListener;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerListener;
import com.griddynamics.jagger.master.configuration.Task;

import java.util.List;

/**
 * Presents composite tasks. Child tasks will be executed in parallel across the nodes.
 *
 * @author Mairbek Khadikov
 */
public class CompositeTask implements Task {
    private List<CompositableTask> leading;
    private List<CompositableTask> attendant = ImmutableList.of();
    private List<Provider<TestGroupListener>> listeners = ImmutableList.of();
    private List<Provider<TestGroupDecisionMakerListener>> decisionMakerListeners = ImmutableList.of();
    private int number;
    private String name;

    public List<CompositableTask> getLeading() {
        return leading;
    }

    public List<Provider<TestGroupListener>> getListeners() {
        return listeners;
    }

    public void setListeners(List<Provider<TestGroupListener>> listeners) {
        this.listeners = listeners;
    }

    public List<Provider<TestGroupDecisionMakerListener>> getDecisionMakerListeners() {
        return decisionMakerListeners;
    }

    public void setDecisionMakerListeners(List<Provider<TestGroupDecisionMakerListener>> decisionMakerListeners) {
        this.decisionMakerListeners = decisionMakerListeners;
    }

    public void setLeading(List<CompositableTask> leading) {
        this.leading = leading;
    }

    public List<CompositableTask> getAttendant() {
        return attendant;
    }

    public void setAttendant(List<CompositableTask> attendant) {
        this.attendant = attendant;
    }

    @Override
    public String getTaskName() {
        return name;
    }

    @Override
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }
}
