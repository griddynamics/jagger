package com.griddynamics.jagger.engine.e1.collector.limits;

import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;

import java.util.Set;

/** Class is used to describe result of comparison of all metrics in some test to limits */
public class DecisionPerTest {
    /** Test */
    private TestEntity testEntity;

    /** Result of comparison for every limits specified for this test */
    private Set<DecisionPerLimit> decisionsPerLimit;

    /** Result of comparison for this test */
    private Decision decisionPerTest;

    public DecisionPerTest(TestEntity testEntity, Set<DecisionPerLimit> decisionsPerLimit, Decision decisionPerTest) {
        this.testEntity = testEntity;
        this.decisionsPerLimit = decisionsPerLimit;
        this.decisionPerTest = decisionPerTest;
    }

    public TestEntity getTestEntity() {
        return testEntity;
    }

    public Set<DecisionPerLimit> getDecisionsPerLimit() {
        return decisionsPerLimit;
    }

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