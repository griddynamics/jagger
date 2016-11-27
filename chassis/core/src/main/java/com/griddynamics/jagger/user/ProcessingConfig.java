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
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
@Deprecated
@Root(name = "processing")
public class ProcessingConfig implements Serializable {
    private List<Test> tests;


    public ProcessingConfig(@ElementList(name = "tests", entry = "test", inline = true) List<Test> tests) {
        this.tests = Collections.unmodifiableList(tests);
    }

    public ProcessingConfig() {
    }

    @ElementList(name = "tests", entry = "test", inline = true)
    public List<Test> getTests() {
        return tests;
    }

    @ElementList(name = "tests", entry = "test", inline = true)
    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public static class Test implements Serializable {
        private String name;
        private String duration;
        private List<Task> tasks;

        public Test(@Attribute(name = "name") String name,
                    @Attribute(name = "duration", required = false) String duration,
                    @ElementList(name = "tasks", entry = "task", inline = true, required = false) List<Task> tasks) {
            this.name = name;
            this.duration = duration;
            this.tasks = Collections.unmodifiableList((tasks != null) ? tasks : new ArrayList<Task>(0));
        }

        public Test() {
        }

        @Attribute(name = "duration", required = false)
        public void setDuration(String duration) {
            this.duration = duration;
        }

        @Attribute(name = "duration", required = false)
        public String getDuration() {
            return duration;
        }

        @Attribute(name = "name")
        public String getName() {
            return name;
        }

        @Attribute(name = "name")
        public void setName(String name) {
            this.name = name;
        }

        @ElementList(name = "tasks", entry = "task", inline = true, required = false)
        public List<Task> getTasks() {
            return tasks;
        }

        @ElementList(name = "tasks", entry = "task", inline = true, required = false)
        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
        }

        public static class Task implements Serializable {


            private String name;
            private String duration;
            private Integer sample = -1;
            private Integer delay = 0;
            private String   bean;
            private List<User> users = new ArrayList<User>(0);
            private Invocation invocation;
            private Tps tps;
            private VirtualUser virtualUser;
            private boolean attendant;

            public Task(@Attribute(name = "name") String name,
                        @Attribute(name = "duration", required = false) String duration,
                        @Attribute(name = "sample", required = false) Integer sample,
                        @Attribute(name = "delay", required = false) Integer delay,
                        @Attribute(name = "attendant", required = false) boolean attendant,
                        @Attribute(name = "bean") String bean,
                        @ElementList(name = "users", entry = "user", inline = true, required = false) List<User> users,
                        @Element(name = "invocation", required = false) Invocation invocation) {
                Preconditions.checkArgument((invocation == null || users == null), "Malformed configuration! <invocation> and <user> elements are mutually exclusive.");

                this.setName(name);
                this.setDuration(duration);
                if(sample!=null){
                    this.setSample(sample);
                }
                if(delay!=null){
                    this.setDelay(delay);
                }
                this.setBean(bean);
                if(users!=null){
                    this.setUsers(users);
                }
                this.setInvocation(invocation);
                this.setAttendant(attendant);
            }

            public Task() {
            }

            @Attribute(name = "attendant", required = false)
            public boolean isAttendant() {
                return attendant;
            }

            @Attribute(name = "attendant", required = false)
            public void setAttendant(boolean attendant) {
                this.attendant = attendant;
            }

            @Attribute(name = "description", required = false)
            public String getTestDescription() {
                return bean;
            }

            @Attribute(name = "description", required = false)
            public void setTestDescription(String description) {
                this.bean = description;
            }

            @Attribute(name = "bean")
            public void setBean(String bean){
                this.bean = bean;
            }

            @Attribute(name = "bean")
            public String getBean(){
                return this.bean;
            }

            @Attribute(name = "delay", required = false)
            public Integer getDelay() {
                return delay;
            }

            @Attribute(name = "delay", required = false)
            public void setDelay(Integer delay) {
                this.delay = delay;
            }

            @Attribute(name = "duration", required = false)
            public String getDuration() {
                return duration;
            }

            @Attribute(name = "duration", required = false)
            public void setDuration(String duration) {
                this.duration = duration;
            }

            @Element(name = "invocation", required = false)
            public Invocation getInvocation() {
                return invocation;
            }

            @Element(name = "invocation", required = false)
            public void setInvocation(Invocation invocation) {
                this.invocation = invocation;
            }

            @Attribute(name = "name")
            public String getName() {
                return name;
            }

            @Attribute(name = "name")
            public void setName(String name) {
                this.name = name;
            }

            @Attribute(name = "sample", required = false)
            public Integer getSample() {
                return sample;
            }

            @Attribute(name = "sample", required = false)
            public void setSample(Integer sample) {
                this.sample = sample;
            }

            @ElementList(name = "users", entry = "user", inline = true, required = false)
            public List<User> getUsers() {
                return users;
            }

            @ElementList(name = "users", entry = "user", inline = true, required = false)
            public void setUsers(List<User> users) {
                this.users =users;
            }

            @Element(name="tps", required =false)
            public void setTps(Tps tps){
                this.tps = tps;
            }

            @Element(name="tps", required =false)
            public Tps getTps(){
                return this.tps;
            }

            @Element(name="virtualUser", required = false)
            public VirtualUser getVirtualUser() {
                return virtualUser;
            }

            @Element(name="virtualUser", required = false)
            public void setVirtualUser(VirtualUser virtualUser) {
                this.virtualUser = virtualUser;
            }

            public static class Invocation implements Serializable {
                private Integer exactcount;
                private Integer threads;

                public Invocation(@Attribute(name = "exactcount") Integer exactcount,
                                  @Attribute(name = "threads", required = false) Integer threads) {
                    this.exactcount = exactcount;
                    this.threads = threads != null ? threads : 1;
                }

                public Invocation() {
                }

                @Attribute(name = "exactcount")
                public void setExactcount(Integer exactcount) {
                    this.exactcount = exactcount;
                }

                @Attribute(name = "threads", required = false)
                public void setThreads(Integer threads) {
                    this.threads = threads;
                }

                @Attribute(name = "exactcount")
                public Integer getExactcount() {
                    return exactcount;
                }

                @Attribute(name = "threads", required = false)
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

                @Attribute(name = "count")
                public String getCount() {
                    return count;
                }

                @Attribute(name = "count")
                public void setCount(String count) {
                    this.count = count;
                }

                @Attribute(name = "life")
                public String getLife() {
                    return life;
                }

                @Attribute(name = "life")
                public void setLife(String life) {
                    this.life = life;
                }

                @Attribute(name = "startBy")
                public String getStartBy() {
                    return startBy;
                }

                @Attribute(name = "startBy")
                public void setStartBy(String startBy) {
                    this.startBy = startBy;
                }

                @Attribute(name = "startCount")
                public String getStartCount() {
                    return startCount;
                }

                @Attribute(name = "startCount")
                public void setStartCount(String startCount) {
                    this.startCount = startCount;
                }

                @Attribute(name = "startIn")
                public String getStartIn() {
                    return startIn;
                }

                @Attribute(name = "startIn")
                public void setStartIn(String startIn) {
                    this.startIn = startIn;
                }
            }
            public static class Tps implements Serializable{
                private Integer value;

                public Tps(){
                }

                public Tps(Integer value){
                    this.value = value;
                }

                @Attribute(name = "value")
                public Integer getValue(){
                    return value;
                }

                @Attribute(name = "value")
                public void setValue(Integer value){
                    this.value = value;
                }
            }

            public static class VirtualUser implements Serializable{
                private Integer count;
                private Integer tickInterval;

                public VirtualUser(){
                }

                public VirtualUser(@Attribute(name="count") Integer count,
                                   @Attribute(name="tickInterval") Integer tickInterval){
                    this.setCount(count);
                    this.setTickInterval(tickInterval);
                }


                @Attribute(name="count")
                public Integer getCount() {
                    return count;
                }

                @Attribute(name="count")
                public void setCount(Integer count) {
                    this.count = count;
                }

                @Attribute(name="tickInterval")
                public Integer getTickInterval() {
                    return tickInterval;
                }

                @Attribute(name="tickInterval")
                public void setTickInterval(Integer tickInterval) {
                    this.tickInterval = tickInterval;
                }
            }
        }
    }
}
