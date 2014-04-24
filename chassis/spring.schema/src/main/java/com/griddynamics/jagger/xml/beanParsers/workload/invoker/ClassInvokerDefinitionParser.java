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
public class ClassInvokerDefinitionParser extends InvokerClassDefinitionParser {

    private String className;

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        className = element.getAttribute(XMLConstants.CLASS);
        element.removeAttribute(XMLConstants.CLASS);
    }

    @Override
    protected String getInvokerClass() {
        return className;
    }
}
