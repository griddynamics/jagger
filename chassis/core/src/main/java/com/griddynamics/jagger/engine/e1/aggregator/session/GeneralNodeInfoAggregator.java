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

package com.griddynamics.jagger.engine.e1.aggregator.session;

import com.google.common.base.Throwables;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.agent.model.GetGeneralNodeInfo;
import com.griddynamics.jagger.coordinator.Coordination;
import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeType;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.SessionData;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.NodeInfoEntity;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.TaskExecutionStatusProvider;
import com.griddynamics.jagger.master.configuration.SessionExecutionListener;
import com.griddynamics.jagger.master.configuration.SessionExecutionStatus;
import com.griddynamics.jagger.master.configuration.SessionListener;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import com.griddynamics.jagger.util.GeneralNodeInfo;
import com.griddynamics.jagger.util.Timeout;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.sql.SQLException;
import java.util.*;

import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.*;

/** Collects information about environment on all nodes and saves to DB
 * @author Dmitry Latnikov
 * @n
 * @par Details:
 * @details
 */
public class GeneralNodeInfoAggregator extends HibernateDaoSupport implements SessionExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(GeneralNodeInfoAggregator.class);

    private Coordinator coordinator;
    private Timeout nodeCollectInfoTime;

    @Override
    public void onSessionStarted(String sessionId, Multimap<NodeType, NodeId> nodes) {
        final String localSessionId = sessionId;
        Set<NodeId> localNodes = new HashSet<NodeId>();
        localNodes.addAll(coordinator.getAvailableNodes(NodeType.KERNEL));
        localNodes.addAll(coordinator.getAvailableNodes(NodeType.AGENT));

        for (NodeId node : localNodes)
        {
            try {
                final GeneralNodeInfo generalNodeInfo = coordinator.getExecutor(node).runSyncWithTimeout(new GetGeneralNodeInfo(sessionId),
                        Coordination.<GetGeneralNodeInfo>doNothing(), nodeCollectInfoTime);
                generalNodeInfo.setNodeId(node.toString());
                log.info("Got node info from node {}:\n {}", node, generalNodeInfo.toString());

                getHibernateTemplate().execute(new HibernateCallback<Void>() {
                    @Override
                    public Void doInHibernate(Session session) throws HibernateException, SQLException {
                        session.persist(new NodeInfoEntity(localSessionId,generalNodeInfo));
                        session.flush();
                        return null;
                    }
                });
            }
            catch (Throwable e) {
                log.error("Get node info failed for node " + node + "\n" + Throwables.getStackTraceAsString(e));
            }
        }
    }

    @Override
    public void onSessionExecuted(String sessionId, String sessionComment) {
    }

    @Required
    public void setNodeCollectInfoTime(Timeout nodeCollectInfoTime) { this.nodeCollectInfoTime = nodeCollectInfoTime;}

    @Required
    public void setCoordinator(Coordinator coordinator) { this.coordinator = coordinator;}


}
