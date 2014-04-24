package com.griddynamics.jagger.xml.beanParsers.workload.invoker;

import com.griddynamics.jagger.invoker.InvokerWrapper;
import com.griddynamics.jagger.invoker.soap.SOAPInvoker;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/22/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class InvokerClassDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return InvokerWrapper.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyValue(XMLConstants.INVOKER_CLAZZ, getInvokerClass());
    }

    protected abstract String getInvokerClass();

    @Override
    protected boolean shouldGenerateId() {
        return true;
    }
}
