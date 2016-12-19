package com.griddynamics.jagger.xml.beanParsers.task;

import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

@Deprecated
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
public class UserDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.Task.User.class;
    }


    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
