package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.BasicTGDecisionMakerListener;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class BasicTGDecisionMakerListenerDefinitionParser extends CustomBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return BasicTGDecisionMakerListener.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

    }
}
