package com.griddynamics.jagger.engine.e1.collector.testgroup;

import com.griddynamics.jagger.engine.e1.collector.test.AbstractTestListener;

/**
 * Listener, executed before and after test-group execution.
 *
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 * @ingroup Main_Listeners_Base_group
 */
public abstract class TestGroupListener extends AbstractTestListener<TestGroupInfo> {
    
    /**
     * Make it final because it will never be invoked
     * and there is no sense to override it.
     *
     * @param info - describes test-group stop information
     */
    @Override
    final public void onRun(TestGroupInfo info) {
    }
}
