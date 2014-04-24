package com.griddynamics.jagger.xml.beanParsers.workload.invoker;

import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created by kgribov on 2/20/14.
 */
public class CustomInvokerStepDefinitionParser extends AbstractInvokerStepDefinitionParser {

    @Override
    public BeanDefinition getInvokerWrapper(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        return parserContext.getDelegate().parseCustomElement(DomUtils.getChildElementByTagName(element, XMLConstants.INVOKER));
    }
}
