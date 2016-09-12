package com.griddynamics.jagger.engine.e1.collector.testsuite;

import com.griddynamics.jagger.engine.e1.collector.test.AbstractTestListener;

/**
 * Listener, executed before and after test suite
 *
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details Possible applications for test suite listener: @n
 * @li Check some setup before executing of test suite (f.e. via properties)
 * @li Provide smoke tests before test suite
 * @li Add some resume to session comment after test suite is over
 * @n
 * @ingroup Main_Listeners_Base_group
 */
public abstract class TestSuiteListener extends AbstractTestListener<TestSuiteInfo> {
    
    /**
     * Make it final because it will never be invoked
     * and there is no sense to override it.
     *
     * @param info - describes test-group stop information
     */
    @Override
    final public void onRun(TestSuiteInfo info) {
    }
}