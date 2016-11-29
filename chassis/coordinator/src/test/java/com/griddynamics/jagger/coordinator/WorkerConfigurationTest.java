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

package com.griddynamics.jagger.coordinator;

import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WorkerConfigurationTest {

    @Test
    public void shouldConfigure() throws Exception {
        ConfigurableWorker worker = new ConfigurableWorker() {

            @Override
            public void configure() {
                onCommandReceived(FirstCommand.class).execute(new CommandExecutor<FirstCommand, Serializable>() {
                    @Override
                    public Qualifier<FirstCommand> getQualifier() {
                        return null;
                    }

                    @Override
                    public Serializable execute(FirstCommand command, NodeContext nodeContext) {
                        return null;
                    }
                });
                onCommandReceived(SecondCommand.class).execute(new CommandExecutor<SecondCommand, Serializable>() {
                    @Override
                    public Qualifier<SecondCommand> getQualifier() {
                        return null;
                    }

                    @Override
                    public Serializable execute(SecondCommand command, NodeContext nodeContext) {
                        return null;
                    }
                });

            }
        };

        Collection<CommandExecutor<?,?>> executors = worker.getExecutors();

        assertThat(executors.size(), is(2));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotBeAbleToSpecifySeveralExecutorsForOneCommand() throws Exception {

        Worker worker = new ConfigurableWorker() {

            @Override
            public void configure() {
                onCommandReceived(FirstCommand.class).execute(new CommandExecutor<FirstCommand, Serializable>() {
                    @Override
                    public Qualifier<FirstCommand> getQualifier() {
                        return null;
                    }

                    @Override
                    public Serializable execute(FirstCommand command, NodeContext nodeContext) {
                        return null;
                    }
                });
                onCommandReceived(FirstCommand.class).execute(new CommandExecutor<FirstCommand, Serializable>() {
                    @Override
                    public Qualifier<FirstCommand> getQualifier() {
                        return null;
                    }

                    @Override
                    public Serializable execute(FirstCommand command, NodeContext nodeContext) {
                        return null;
                    }
                });

            }
        };

        worker.getExecutors();

    }

    public static class FirstCommand implements Command<Serializable> {

        @Override
        public String getSessionId() {
            return null;
        }

    }

    public static class SecondCommand implements Command<Serializable> {

        @Override
        public String getSessionId() {
            return null;
        }

    }
}
