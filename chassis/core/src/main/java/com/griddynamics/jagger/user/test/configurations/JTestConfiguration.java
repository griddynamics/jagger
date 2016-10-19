package com.griddynamics.jagger.user.test.configurations;

import lombok.Builder;
import lombok.ToString;

import java.util.List;

/**
 * @author asokol
 *         created 10/18/16
 */
@Builder
@ToString
public class JTestConfiguration {

    private List<JTestGroup> testGroups;

}
