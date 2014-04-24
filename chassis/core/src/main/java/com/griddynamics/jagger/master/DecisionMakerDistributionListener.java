package com.griddynamics.jagger.master;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.ProviderUtil;
import com.griddynamics.jagger.engine.e1.services.JaggerPlace;
import com.griddynamics.jagger.engine.e1.sessioncomparation.DecisionMakerInfo;
import com.griddynamics.jagger.engine.e1.sessioncomparation.TestGroupDecisionMakerListener;
import com.griddynamics.jagger.master.configuration.Task;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 2/7/14
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecisionMakerDistributionListener implements DistributionListener {
    private NodeContext nodeContext;

    public DecisionMakerDistributionListener(NodeContext nodeContext) {
        this.nodeContext = nodeContext;
    }

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        //do nothing
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        if (task instanceof CompositeTask) {
            TestGroupDecisionMakerListener decisionMakerListener = TestGroupDecisionMakerListener.Composer.compose(ProviderUtil.provideElementsAndInjectContext(((CompositeTask) task).getDecisionMakerListeners(),
                    sessionId,
                    taskId,
                    nodeContext,
                    JaggerPlace.TEST_GROUP_DECISION_MAKER_LISTENER));

            DecisionMakerInfo decisionMakerInfo = new DecisionMakerInfo();
            decisionMakerListener.onDecisionMaking(decisionMakerInfo);
        }
    }
}
