package com.griddynamics.jagger.xml.beanParsers.workload.balancer;

import com.griddynamics.jagger.invoker.RoundRobinLoadBalancer;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/22/13
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoundRobinBalancerDefinitionParser extends AbstractBalancerDefinitionParser{
    @Override
    public Class getBalancerClass() {
        return RoundRobinLoadBalancer.class;
    }
}
