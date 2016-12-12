package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.engine.e1.scenario.ReflectionProvider;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/21/13
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotNullResponseDefinitionParser extends AbstractValidatorDefinitionParser {

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        ReflectionProvider provider = new ReflectionProvider();
        provider.setClazz(NotNullResponseValidator.class);
        builder.addPropertyValue(XMLConstants.VALIDATOR, provider);
    }
}
