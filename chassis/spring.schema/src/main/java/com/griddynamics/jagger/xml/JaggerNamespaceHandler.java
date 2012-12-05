package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.xml.beanParsers.*;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class JaggerNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("invocation", new InvocationDefinitionParser());
        registerBeanDefinitionParser("task", new TaskDefinitionParser());
        registerBeanDefinitionParser("test", new TestDefinitionParser());
        registerBeanDefinitionParser("user", new UserDefinitionParser());
        registerBeanDefinitionParser("test-plan", new TestPlanDefinitionParser());
        registerBeanDefinitionParser("processing", new TestPlanDefinitionParser());
        registerBeanDefinitionParser("extension", new ExtensionDefinitionParser());
        registerBeanDefinitionParser("extensions", new ExtensionsDefinitionParser());
        registerBeanDefinitionParser("comparator", new ComparatorDefinitionParser());
        registerBeanDefinitionParser("sessionComparators", new SessionComparatorsDefinitionParser());
        registerBeanDefinitionParser("report", new ReportDefinitionParser());
    }
}
