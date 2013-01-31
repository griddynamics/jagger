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

package com.griddynamics.jagger.user;

import com.google.common.base.Preconditions;
import com.griddynamics.jagger.reporting.ReportingService;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: dkotlyarov
 */
@Root(name = "processing")
public class ProcessingConfig implements Serializable {
    @ElementList(name = "tests", entry = "test", inline = true)
    public final List<Test> tests;

    public ProcessingConfig(@ElementList(name = "tests", entry = "test", inline = true) List<Test> tests) {
        this.tests = Collections.unmodifiableList(tests);
    }

    public static class Test implements Serializable {
        @Attribute(name = "name")
        public final String name;

        @Attribute(name = "duration", required = false)
        public final String duration;

        @ElementList(name = "tasks", entry = "task", inline = true, required = false)
        public final List<Task> tasks;

        public Test(@Attribute(name = "name") String name,
                    @Attribute(name = "duration", required = false) String duration,
                    @ElementList(name = "tasks", entry = "task", inline = true, required = false) List<Task> tasks) {
            this.name = name;
            this.duration = duration;
            this.tasks = Collections.unmodifiableList((tasks != null) ? tasks : new ArrayList<Task>(0));
        }

        public static class Task implements Serializable {
            @Attribute(name = "name")
            public final String name;

            @Attribute(name = "duration", required = false)
            public final String duration;

            @Attribute(name = "sample", required = false)
            public final Integer sample;

            @Attribute(name = "delay", required = false)
            public final Integer delay;

            @Attribute(name = "bean")
            public final String bean;

            @ElementList(name = "users", entry = "user", inline = true, required = false)
            public final List<User> users;

            @Element(name = "invocation", required = false)
            public final Invocation invocation;

            @Attribute(name = "attendant", required = false)
            public final Boolean attendant;

            public Task(@Attribute(name = "name") String name,
                        @Attribute(name = "duration", required = false) String duration,
                        @Attribute(name = "sample", required = false) Integer sample,
                        @Attribute(name = "delay", required = false) Integer delay,
                        @Attribute(name = "attendant", required = false) Boolean attendant,
                        @Attribute(name = "bean") String bean,
                        @ElementList(name = "users", entry = "user", inline = true, required = false) List<User> users,
                        @Element(name = "invocation", required = false) Invocation invocation) {
                Preconditions.checkArgument((invocation == null || users == null), "Malformed configuration! <invocation> and <user> elements are mutually exclusive.");
                
                this.name = name;
                this.duration = duration;
                this.sample = (sample != null) ? sample : -1;
                this.delay = (delay != null) ? delay : 0;
                this.bean = bean;
                this.users = Collections.unmodifiableList((users != null) ? users : new ArrayList<User>(0));
                this.invocation = invocation;
                this.attendant = (attendant != null) ? attendant : false;
            }

            public static class Invocation implements Serializable {
                @Attribute(name = "exactcount")
                public final Integer count;

                @Attribute(name = "threads", required = false)
                public final Integer threads;

                public Invocation(@Attribute(name = "exactcount") Integer count,
                                  @Attribute(name = "threads", required = false) Integer threads) {
                    this.count = count;
                    this.threads = threads != null ? threads : 1;
                }
            }

            public static class User implements Serializable {
                @Attribute(name = "count")
                public final String count;

                @Attribute(name = "startCount")
                public final String startCount;

                @Attribute(name = "startIn")
                public final String startIn;

                @Attribute(name = "startBy")
                public final String startBy;

                @Attribute(name = "life")
                public final String life;

                public User(@Attribute(name = "count") String count,
                            @Attribute(name = "startCount") String startCount,
                            @Attribute(name = "startIn") String startIn,
                            @Attribute(name = "startBy") String startBy,
                            @Attribute(name = "life") String life) {
                    this.count = count;
                    this.startCount = startCount;
                    this.startIn = startIn;
                    this.startBy = startBy;
                    this.life = life;
                }
            }
        }
    }
}
