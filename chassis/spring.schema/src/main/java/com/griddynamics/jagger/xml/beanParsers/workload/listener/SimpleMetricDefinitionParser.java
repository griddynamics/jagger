package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.SimpleMetricCalculator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/22/13
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleMetricDefinitionParser extends AbstractMetricDefinitionParser implements BeanDefinitionParser {

    @Override
    protected BeanDefinition getCalcBean() {
        return BeanDefinitionBuilder.genericBeanDefinition(SimpleMetricCalculator.class).getBeanDefinition();
    }
}
