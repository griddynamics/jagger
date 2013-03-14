package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.DiagnosticCollectorProvider;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/20/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomMetricDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return DiagnosticCollectorProvider.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
    if (element.getAttribute(XMLConstants.ID)!=null && !element.getAttribute(XMLConstants.ID).isEmpty()){
        builder.addPropertyValue(XMLConstants.NAME, element.getAttribute(XMLConstants.ID));
    }else{
        builder.addPropertyValue(XMLConstants.NAME, "No name metric");
    }
        builder.addPropertyValue(XMLConstants.METRIC_CALCULATOR, new RuntimeBeanReference(element.getAttribute(XMLConstants.CALCULATOR)));
    }
}
