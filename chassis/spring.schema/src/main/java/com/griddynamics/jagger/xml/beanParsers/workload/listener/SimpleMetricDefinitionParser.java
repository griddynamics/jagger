package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.SimpleMetricCalculator;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/22/13
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleMetricDefinitionParser extends AbstractCalculatorBasedDefinitionParser {

    @Override
    protected Object getMetricCalculator(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        return BeanDefinitionBuilder.genericBeanDefinition(SimpleMetricCalculator.class).getBeanDefinition();
    }

    @Override
    protected String getDefaultCollectorName() {
        return "NotNullResponse";
    }
}
