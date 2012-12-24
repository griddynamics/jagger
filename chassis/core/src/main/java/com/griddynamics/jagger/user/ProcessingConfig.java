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
    public List<Test> tests;

    public ProcessingConfig(@ElementList(name = "tests", entry = "test", inline = true) List<Test> tests) {
        this.tests = Collections.unmodifiableList(tests);
    }

    public ProcessingConfig() {
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public static class Test implements Serializable {
        @Attribute(name = "name")
        public String name;

        @Attribute(name = "duration", required = false)
        public String duration;

        @ElementList(name = "tasks", entry = "task", inline = true, required = false)
        public List<Task> tasks;

        public Test(@Attribute(name = "name") String name,
                    @Attribute(name = "duration", required = false) String duration,
                    @ElementList(name = "tasks", entry = "task", inline = true, required = false) List<Task> tasks) {
            this.name = name;
            this.duration = duration;
            this.tasks = Collections.unmodifiableList((tasks != null) ? tasks : new ArrayList<Task>(0));
        }

        public Test() {
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Task> getTasks() {
            return tasks;
        }

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
        }

        public static class Task implements Serializable {
            @Attribute(name = "name")
            public String name;

            @Attribute(name = "duration", required = false)
            public String duration;

            @Attribute(name = "sample", required = false)
            public Integer sample;

            @Attribute(name = "delay", required = false)
            public Integer delay;

            @Attribute(name = "bean")
            public String   bean;

            @ElementList(name = "users", entry = "user", inline = true, required = false)
            public List<User> users;

            @Element(name = "invocation", required = false)
            public Invocation invocation;

            @Element(name="tps", required =false)
            public Tps tps;

            @Element(name="virtualUser", required = false)
            public VirtualUser virtualUser;

            public Task(@Attribute(name = "name") String name,
                        @Attribute(name = "duration", required = false) String duration,
                        @Attribute(name = "sample", required = false) Integer sample,
                        @Attribute(name = "delay", required = false) Integer delay,
                        @Attribute(name = "bean") String bean,
                        @ElementList(name = "users", entry = "user", inline = true, required = false) List<User> users,
                        @Element(name = "invocation", required = false) Invocation invocation,
                        @Element(name = "tps", required = false) Tps tps,
                        @Element(name = "virtualUser", required = false) VirtualUser virtualUser) {
                Preconditions.checkArgument((invocation == null ||
                                             users      == null ||
                                            virtualUser == null ||
                                             tps        == null), "Malformed configuration! <invocation> and <user> elements are mutually exclusive.");
                
                this.name = name;
                this.duration = duration;
                this.sample = (sample != null) ? sample : -1;
                this.delay = (delay != null) ? delay : 0;
                this.bean = bean;
                this.users = Collections.unmodifiableList((users != null) ? users : new ArrayList<User>(0));
                this.tps = tps;
                this.invocation = invocation;
                this.setVirtualUser(virtualUser);
            }

            public Task() {
            }

            public String getBean() {
                return bean;
            }

            public void setBean(String bean) {
                this.bean = bean;
            }

            public Integer getDelay() {
                return delay;
            }

            public void setDelay(Integer delay) {
                this.delay = delay;
            }

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public Invocation getInvocation() {
                return invocation;
            }

            public void setInvocation(Invocation invocation) {
                this.invocation = invocation;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getSample() {
                return sample;
            }

            public void setSample(Integer sample) {
                this.sample = sample;
            }

            public List<User> getUsers() {
                return users;
            }

            public void setUsers(List<User> users) {
                this.users = users;
            }

            public void setTps(Tps tps){
                this.tps = tps;
            }

            public Tps getTps(){
                return this.tps;
            }

            public VirtualUser getVirtualUser() {
                return virtualUser;
            }

            public void setVirtualUser(VirtualUser virtualUser) {
                this.virtualUser = virtualUser;
            }

            public static class Invocation implements Serializable {
                @Attribute(name = "exactcount")
                public Integer exactcount;

                @Attribute(name = "threads", required = false)
                public Integer threads;

                public Invocation(@Attribute(name = "exactcount") Integer exactcount,
                                  @Attribute(name = "threads", required = false) Integer threads) {
                    this.exactcount = exactcount;
                    this.threads = threads != null ? threads : 1;
                }

                public Invocation() {
                }

                public void setExactcount(Integer exactcount) {
                    this.exactcount = exactcount;
                }

                public void setThreads(Integer threads) {
                    this.threads = threads;
                }

                public Integer getExactcount() {
                    return exactcount;
                }

                public Integer getThreads() {
                    return threads;
                }
            }

            public static class User implements Serializable {
                @Attribute(name = "count")
                public String count;

                @Attribute(name = "startCount")
                public String startCount;

                @Attribute(name = "startIn")
                public String startIn;

                @Attribute(name = "startBy")
                public String startBy;

                @Attribute(name = "life")
                public String life;

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

                public User() {
                }

                public String getCount() {
                    return count;
                }

                public void setCount(String count) {
                    this.count = count;
                }

                public String getLife() {
                    return life;
                }

                public void setLife(String life) {
                    this.life = life;
                }

                public String getStartBy() {
                    return startBy;
                }

                public void setStartBy(String startBy) {
                    this.startBy = startBy;
                }

                public String getStartCount() {
                    return startCount;
                }

                public void setStartCount(String startCount) {
                    this.startCount = startCount;
                }

                public String getStartIn() {
                    return startIn;
                }

                public void setStartIn(String startIn) {
                    this.startIn = startIn;
                }
            }
            public static class Tps implements Serializable{
                @Attribute(name = "value")
                public Integer value;

                public Tps(){
                }

                public Tps(@Attribute(name = "value") Integer value){
                    this.value = value;
                }

                public Integer getValue(){
                    return value;
                }

                public void setValue(Integer value){
                    this.value = value;
                }
            }

            public static class VirtualUser implements Serializable{
                @Attribute(name="count")
                public Integer count;

                @Attribute(name="tickInterval")
                public Integer tickInterval;

                public VirtualUser(){
                }

                public VirtualUser(@Attribute(name="count") Integer count,
                                   @Attribute(name="tickInterval") Integer tickInterval){
                    this.setCount(count);
                    this.setTickInterval(tickInterval);
                }


                public Integer getCount() {
                    return count;
                }

                public void setCount(Integer count) {
                    this.count = count;
                }

                public Integer getTickInterval() {
                    return tickInterval;
                }

                public void setTickInterval(Integer tickInterval) {
                    this.tickInterval = tickInterval;
                }
            }
        }
    }
}
