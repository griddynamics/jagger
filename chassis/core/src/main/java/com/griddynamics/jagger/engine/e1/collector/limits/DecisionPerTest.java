package com.griddynamics.jagger.engine.e1.collector.limits;

import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.util.Decision;

import java.util.Set;

/** Class is used to describe result of comparison of all metrics in some test to limits */
public class DecisionPerTest {

    private TestEntity testEntity;
    private Set<DecisionPerLimit> decisionsPerLimit;
    private Decision decisionPerTest;

    public DecisionPerTest(TestEntity testEntity, Set<DecisionPerLimit> decisionsPerLimit, Decision decisionPerTest) {
        this.testEntity = testEntity;
        this.decisionsPerLimit = decisionsPerLimit;
        this.decisionPerTest = decisionPerTest;
    }

    /** Returns information about test */
    public TestEntity getTestEntity() {
        return testEntity;
    }

    /** Returns detailed information about decision per every limit in the limit set attached to this test */
    public Set<DecisionPerLimit> getDecisionsPerLimit() {
        return decisionsPerLimit;
    }

    /** Returns decision for this test */
    public Decision getDecisionPerTest() {
        return decisionPerTest;
    }

    @Override
    public String toString() {
        return "DecisionPerTest{" +
                "testName=" + testEntity.getName() +
                ", decisionPerTest=" + decisionPerTest +
                '}';
    }
}