package com.griddynamics.jagger.xml.beanParsers.workload.balancer;

import com.griddynamics.jagger.invoker.OneByOneLoadBalancer;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/22/13
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class OneByOneBalancerDefinitionParser extends AbstractBalancerDefinitionParser{

    @Override
    public Class getBalancerClass() {
        return OneByOneLoadBalancer.class;
    }
}
