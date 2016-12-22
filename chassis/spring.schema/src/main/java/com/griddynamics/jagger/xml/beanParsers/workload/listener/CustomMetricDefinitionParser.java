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
@Deprecated
public class CustomMetricDefinitionParser extends AbstractCalculatorBasedDefinitionParser {

    @Override
    protected Object getMetricCalculator(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        return new RuntimeBeanReference(element.getAttribute(XMLConstants.CALCULATOR));
    }
}
