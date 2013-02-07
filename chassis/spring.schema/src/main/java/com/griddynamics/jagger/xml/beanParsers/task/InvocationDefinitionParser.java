package com.griddynamics.jagger.xml.beanParsers.task;

import com.griddynamics.jagger.user.ProcessingConfig;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.w3c.dom.Element;


public class InvocationDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.Task.Invocation.class;
    }
}
