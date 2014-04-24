package com.griddynamics.jagger.xml.beanParsers.workload.invoker;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by kgribov on 2/20/14.
 */
public class ApacheHttpInvokerStepDefinitionParser extends AbstractInvokerStepDefinitionParser{

    @Override
    public BeanDefinition getInvokerWrapper(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        return new ApacheHttpInvokerClassDefinitionParser().parse(element, parserContext);
    }
}
