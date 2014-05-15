package com.griddynamics.jagger.engine.e1.collector.testgroup;

import com.griddynamics.jagger.engine.e1.collector.limits.DecisionPerTest;
import com.griddynamics.jagger.master.CompositeTask;

import java.util.Set;

/** Class, which contains information for decision making
 * @author Novozhilov Mark
 * @n
 * @par Details:
 * @details
 * @n
 * */

//todo ??? JFG-744 docu for decision making with use of limits
public class TestGroupDecisionMakerInfo {
    private CompositeTask testGroup;
    private String sessionId;
    private Set<DecisionPerTest> decisionsPerTest;

    public TestGroupDecisionMakerInfo(CompositeTask testGroup, String sessionId, Set<DecisionPerTest> decisionsPerTest) {
        this.testGroup = testGroup;
        this.sessionId = sessionId;
        this.decisionsPerTest = decisionsPerTest;
    }

    public CompositeTask getTestGroup() {
        return testGroup;
    }

    public void setTestGroup(CompositeTask testGroup) {
        this.testGroup = testGroup;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Set<DecisionPerTest> getDecisionsPerTest() {
        return decisionsPerTest;
    }

    public void setDecisionsPerTest(Set<DecisionPerTest> decisionsPerTest) {
        this.decisionsPerTest = decisionsPerTest;
    }


}
