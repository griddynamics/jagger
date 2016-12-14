package com.griddynamics.jagger.user.test.configurations.termination;

/**
 * Test with such termination strategy will wait another tests in test-group to be stopped.
 *
 * @ingroup Main_Termination_criteria_group
 */
public class JTerminationCriteriaBackground implements JTerminationCriteria {

    private static final JTerminationCriteriaBackground instance = new JTerminationCriteriaBackground();
    
    private JTerminationCriteriaBackground() {
    }
    
    public static JTerminationCriteriaBackground getInstance() {
        return instance;
    }
}
