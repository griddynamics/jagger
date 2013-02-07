package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.DiagnosticCollectorProvider;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/21/13
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractMetricDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return DiagnosticCollectorProvider.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyValue(XMLConstants.METRIC_CALCULATOR, getCalcBean());
    }

    protected abstract BeanDefinition getCalcBean();
}
