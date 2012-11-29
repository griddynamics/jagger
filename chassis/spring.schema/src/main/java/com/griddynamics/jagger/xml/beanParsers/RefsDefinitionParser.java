package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.user.ProcessingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


public class RefsDefinitionParser implements BeanDefinitionParser {
    private static final Logger log = LoggerFactory.getLogger(RefsDefinitionParser.class);

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parserContext.getRegistry().getBeanDefinition(element.getAttribute("bean"));
    }
}
