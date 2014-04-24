package com.griddynamics.jagger.xml.beanParsers.workload.invoker;

import com.griddynamics.jagger.invoker.InvokerStepProvider;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created by kgribov on 2/20/14.
 */
public abstract class AbstractInvokerStepDefinitionParser extends CustomBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return InvokerStepProvider.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        setBeanProperty(XMLConstants.QUERY_PROCESSOR_PROVIDER, DomUtils.getChildElementByTagName(element, XMLConstants.QUERY_PROCESSOR), parserContext, builder.getBeanDefinition());
        setBeanProperty(XMLConstants.LISTENERS, DomUtils.getChildElementByTagName(element, XMLConstants.INVOCATION_LISTENERS), parserContext, builder.getBeanDefinition());
        builder.addPropertyValue(XMLConstants.INVOKER_WRAPPER, getInvokerWrapper(element, parserContext, builder));
    }

    public abstract BeanDefinition getInvokerWrapper(Element element, ParserContext parserContext, BeanDefinitionBuilder builder);
}
