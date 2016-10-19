package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;
import lombok.Builder;
import lombok.ToString;

/**
 * Test configuration holder.
 * Here a user can set termination strategy, load configuration and other parameters of a test.
 */
@Builder
@ToString
public class JTest {


    private String id;
    private JTestDescription testDescription;
    private JLoad load;
    private JTermination termination;

}
