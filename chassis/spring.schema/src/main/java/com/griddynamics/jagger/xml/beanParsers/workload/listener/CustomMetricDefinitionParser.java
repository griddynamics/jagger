package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/20/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomMetricDefinitionParser extends AbstractCollectorDefinitionParser {


    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyValue(XMLConstants.NAME, getID(element,XMLConstants.DEFAULT_METRIC_NAME));
        builder.addPropertyValue(XMLConstants.METRIC_CALCULATOR, new RuntimeBeanReference(element.getAttribute(XMLConstants.CALCULATOR)));
    }
}
