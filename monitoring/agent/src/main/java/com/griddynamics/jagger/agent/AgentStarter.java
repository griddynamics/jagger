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

package com.griddynamics.jagger.agent;

import com.google.common.base.Objects;
import com.griddynamics.jagger.agent.worker.AgentWorker;
import com.griddynamics.jagger.coordinator.Coordination;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.http.Ack;
import com.griddynamics.jagger.coordinator.http.RegistrationPack;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class AgentStarter {
    private static final Logger log = LoggerFactory.getLogger(AgentStarter.class);
    public static CountDownLatch agentLatch;
    public static AtomicBoolean alive = new AtomicBoolean(false);
    public static final int REGISTRATION_PERIOD = 10000;

    public static void main(String[] args) {
        log.info("Going to start agent");
        log.debug("Agent initialization started");

        Properties props = System.getProperties();
        for (Map.Entry<Object, Object> prop : props.entrySet()) {
            log.info("{}: '{}'", prop.getKey(), prop.getValue());
        }
        log.info("");

        Sigar sigar = new Sigar();
        try {
            sigar.getMem();
        } catch (SigarException e) {
            log.error("", e);
        }

        ApplicationContext context = new ClassPathXmlApplicationContext("spring/agent.config.xml");
        String name = ((AgentConfig) context.getBean("agentConfig")).getName();
        Agent agent = (Agent) context.getBean("agent");
        agent.setNodeContext(Coordination.emptyContext(NodeId.agentNode(name)));
        agent.init();

        AgentWorker agentWorker = (AgentWorker) context.getBean("agentWorker");

        log.info("Agent {} initialized", agent.getNodeContext().getId());

        try {
            do {
                agentWorker.getProfiler().stopPolling();
                agent.stop();
                agentLatch = new CountDownLatch(1);
                alive.set(false);
                do {
                    try {
                        RegistrationPack registrationPack =
                                RegistrationPack.create(agent.getNodeContext().getId(), agentWorker.getQualifiers());
                        Ack ack = agent.getExchangeClient().registerNode(registrationPack);
                        if (Ack.SUCCESS.equals(ack)) break;
                    } catch (IOException e) {
                        log.info("Agent {} can't do registration {}", agent.getNodeContext().getId(), e);
                        log.info("Wait 10 seconds and try again");
                        Thread.sleep(REGISTRATION_PERIOD);
                    }
                }
                while (true);
                log.info("Registration agent {} has been done", agent.getNodeContext().getId());


                agent.start();
                agentLatch.await();
            }
            while (alive.get());
        } catch (InterruptedException e) {
            // do nothing;
        }

        agentWorker.getProfiler().stopPolling();
        agent.stop();
        log.info("Agent finish work");
    }



    public static void resetAgent(Agent agent) {
        alive.set(true);
        agent.stop();
        agentLatch.countDown();
    }
}
