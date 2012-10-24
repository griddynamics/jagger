package com.griddynamics.jagger.kernel.agent;

import com.griddynamics.jagger.coordinator.Coordination;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.http.Ack;
import com.griddynamics.jagger.coordinator.http.RegistrationPack;
import com.griddynamics.jagger.kernel.agent.worker.KernelAgentWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Vladimir Kondrashchenko
 */
public class KernelAgentLauncher {

    private static Logger log = LoggerFactory.getLogger(KernelAgentLauncher.class);

    public static final int AGENT_REGISTRATION_PERIOD = 10000;

    public CountDownLatch agentLatch;
    public AtomicBoolean alive = new AtomicBoolean(false);

    private ApplicationContext applicationContext;

    public static KernelAgentLauncher create(ApplicationContext configurationContext) {
        KernelAgentLauncher result = configurationContext.getBean("kernelAgentLauncher", KernelAgentLauncher.class);
        result.setApplicationContext(configurationContext);

        return result;
    }

    private void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setAlive(boolean alive) {
        this.alive.set(alive);
    }

    public CountDownLatch getAgentLatch() {
        return agentLatch;
    }

    public void launchKernelAgent() {
        log.info("Starting Kernel Agent");

        String name = "KERNEL-AGENT";

        KernelAgent agent = (KernelAgent) applicationContext.getBean("kernelAgent");

        KernelAgentWorker kernelAgentWorker = (KernelAgentWorker) applicationContext.getBean("kernelAgentWorker");
        agent.setNodeContext(Coordination.emptyContext(NodeId.kernelAgentNode(name)));
        agent.init();

        log.info("Agent {} initialized", agent.getNodeContext().getId());

        try {
            do {
                agent.stop();
                agentLatch = new CountDownLatch(1);
                alive.set(false);
                do {
                    try {
                        RegistrationPack registrationPack =
                                RegistrationPack.create(agent.getNodeContext().getId(), kernelAgentWorker.getQualifiers());
                        Ack ack = agent.getExchangeClient().registerNode(registrationPack);
                        if (Ack.SUCCESS.equals(ack)) break;
                    } catch (IOException e) {
                        log.info("Agent {} can't do registration {}", agent.getNodeContext().getId(), e);
                        log.info("Wait 10 seconds and try again");
                        Thread.sleep(AGENT_REGISTRATION_PERIOD);
                    }
                }
                while (true);
                log.info("Registration agent {} has been done", agent.getNodeContext().getId());


                agent.start();
                agentLatch.await();
            } while (alive.get());
        } catch (InterruptedException e) {
            // do nothing;
        }

        agent.stop();
        log.info("Kernel Agent finish work");
    }

    public void resetAgent(KernelAgent agent) {
        alive.set(true);
        agent.stop();
        agentLatch.countDown();
    }
}
