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

hostConfigurations =
    [
            [address:"127.0.0.1", keyName:"path/to/key", login:"login", netInterface:"eth0"]
    ]

triggerOnce = StandardTriggers.onceWithDelay()(400);
triggerWithPeriod = StandardTriggers.withPeriod()(500);

actionConfigurations =
    [
            primaryActions: [
                    [
                            trigger : triggerOnce,
                            command: StandardCommands.emulateDelays(
                                    {
                                        context ->
                                        5 * (context.tick / 2) // any function of context can be defined
                                    },
                                    StandardFunctions.constant(5)
                            )
                    ],
                    [
                            trigger : triggerWithPeriod,
                            command : StandardCommands.clearNetwork
                    ]
            ],

            finalActions: [
                    [
                            trigger : StandardTriggers.noCondition,
                            command : StandardCommands.clearNetwork
                    ]
            ]
    ]

Supervisor supervisor = new Supervisor()
supervisor.isDryRun = true
supervisor.hostConfigurations = hostConfigurations
supervisor.supervisionLoopInterval = 100
supervisor.actionConfigurations = actionConfigurations
supervisor.numberOfExecutionThreads = 3

supervisor.runAsync()

Thread.sleep(3000)

supervisor.stop = true
