package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.SimpleMetricCalculator;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
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
public class SimpleMetricDefinitionParser extends AbstractCollectorDefinitionParser {

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        BeanDefinition bean = BeanDefinitionBuilder.genericBeanDefinition(SimpleMetricCalculator.class).getBeanDefinition();
        builder.addPropertyValue(XMLConstants.NAME, getID(element,XMLConstants.DEFAULT_METRIC_NAME));
        builder.addPropertyValue(XMLConstants.METRIC_CALCULATOR, bean);
    }
}
