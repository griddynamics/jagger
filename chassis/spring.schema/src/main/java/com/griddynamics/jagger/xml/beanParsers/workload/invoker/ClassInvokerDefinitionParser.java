package com.griddynamics.jagger.xml.beanParsers.workload.invoker;

import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/12/13
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class ClassInvokerDefinitionParser extends CustomBeanDefinitionParser {

    private String className;

    @Override
    protected Class getBeanClass(Element element) {
        return String.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addConstructorArgValue(className);
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        className = element.getAttribute(XMLConstants.CLASS);
        element.removeAttribute(XMLConstants.CLASS);
    }
}
