package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.user.ProcessingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.w3c.dom.Element;


public class UserDefinitionParser extends AbstractSimpleBeanDefinitionParser {
    private static final Logger log = LoggerFactory.getLogger(UserDefinitionParser.class);

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.Task.User.class;
    }
}
