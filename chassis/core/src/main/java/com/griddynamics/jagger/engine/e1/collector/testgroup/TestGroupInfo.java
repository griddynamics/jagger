package com.griddynamics.jagger.engine.e1.collector.testgroup;

import com.griddynamics.jagger.engine.e1.collector.test.TaskBasedTestInfo;
import com.griddynamics.jagger.master.CompositeTask;

/**
 * Contains information related to an instance
 * of {@link com.griddynamics.jagger.master.CompositeTask} class.
 *
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 */
public class TestGroupInfo extends TaskBasedTestInfo<CompositeTask> {
    
    public TestGroupInfo(CompositeTask testGroup) {
        super(testGroup);
    }
}
