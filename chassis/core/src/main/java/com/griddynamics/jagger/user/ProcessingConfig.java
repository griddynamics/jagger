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

package com.griddynamics.jagger.user;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ProcessingConfig implements Serializable {
    private List<Test> tests;

    public ProcessingConfig(List<Test> tests) {
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
        private String name;
        private String duration;
        private List<Task> tasks;

        public Test(String name,
                    String duration,
                    List<Task> tasks) {
            this.name = name;
            this.duration = duration;
            this.tasks = Collections.unmodifiableList((tasks != null) ? tasks : new ArrayList<Task>(0));
        }

        public Test() {
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getDuration() {
            return duration;
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


            private String name;
            private String duration;
            private Integer sample = -1;
            private Integer delay = 0;
            private String bean;
            private List<User> users = new ArrayList<User>(0);
            private Invocation invocation;
            private Tps tps;
            private VirtualUser virtualUser;
            private boolean attendant;

            public Task(String name,
                        String duration,
                        Integer sample,
                        Integer delay,
                        boolean attendant,
                        String bean,
                        List<User> users,
                        Invocation invocation) {
                Preconditions.checkArgument((invocation == null || users == null),
                        "Malformed configuration! <invocation> and <user> elements are mutually exclusive.");

                this.setName(name);
                this.setDuration(duration);
                if (sample != null) {
                    this.setSample(sample);
                }
                if (delay != null) {
                    this.setDelay(delay);
                }
                this.setBean(bean);
                if (users != null) {
                    this.setUsers(users);
                }
                this.setInvocation(invocation);
                this.setAttendant(attendant);
            }

            public Task() {
            }

            public boolean isAttendant() {
                return attendant;
            }

            public void setAttendant(boolean attendant) {
                this.attendant = attendant;
            }

            public String getTestDescription() {
                return bean;
            }

            public void setTestDescription(String description) {
                this.bean = description;
            }

            public void setBean(String bean) {
                this.bean = bean;
            }

            public String getBean() {
                return this.bean;
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

            public void setTps(Tps tps) {
                this.tps = tps;
            }

            public Tps getTps() {
                return this.tps;
            }

            public VirtualUser getVirtualUser() {
                return virtualUser;
            }

            public void setVirtualUser(VirtualUser virtualUser) {
                this.virtualUser = virtualUser;
            }

            public static class Invocation implements Serializable {
                private Integer exactcount;
                private Integer threads;

                public Invocation(Integer exactcount,
                                  Integer threads) {
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
                private String count;
                private String startCount;
                private String startIn;
                private String startBy;
                private String life;

                public User(String count,
                            String startCount,
                            String startIn,
                            String startBy,
                            String life) {
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

            public static class Tps implements Serializable {
                private Integer value;

                public Tps() {
                }

                public Tps(Integer value) {
                    this.value = value;
                }

                public Integer getValue() {
                    return value;
                }

                public void setValue(Integer value) {
                    this.value = value;
                }
            }

            public static class VirtualUser implements Serializable {
                private Integer count;
                private Integer tickInterval;

                public VirtualUser() {
                }

                public VirtualUser(Integer count,
                                   Integer tickInterval) {
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
