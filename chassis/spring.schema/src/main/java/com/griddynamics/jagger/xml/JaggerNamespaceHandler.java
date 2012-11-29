package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.xml.beanParsers.*;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.context.config.ContextNamespaceHandler;

public class JaggerNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("invocation", new InvocationDefinitionParser());
        registerBeanDefinitionParser("task", new TaskDefinitionParser());
        registerBeanDefinitionParser("test", new TestDefinitionParser());
        registerBeanDefinitionParser("user", new UserDefinitionParser());
        registerBeanDefinitionParser("test-plan", new TestPlanDefinitionParser());
        registerBeanDefinitionParser("processing", new TestPlanDefinitionParser());
    }
}
