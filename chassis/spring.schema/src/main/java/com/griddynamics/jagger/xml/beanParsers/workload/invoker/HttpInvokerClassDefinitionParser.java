package com.griddynamics.jagger.xml.beanParsers.workload.invoker;

import com.griddynamics.jagger.invoker.http.HttpInvoker;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/22/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpInvokerClassDefinitionParser extends InvokerClassDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return getClassNameBeanDefinition(HttpInvoker.class);
    }
}
