package com.griddynamics.jagger.xml.beanParsers.workload.invoker;

import com.griddynamics.jagger.invoker.InvokerStepsProvider;
import com.griddynamics.jagger.invoker.InvokerWrapper;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by kgribov on 2/19/14.
 */
public class InvokerStepsDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return InvokerWrapper.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        BeanDefinitionBuilder invokerProvider = BeanDefinitionBuilder.genericBeanDefinition(InvokerStepsProvider.class);

        List<Element> stepsElements = DomUtils.getChildElementsByTagName(element, XMLConstants.STEP);
        ManagedList parsedList = parseCustomElements(stepsElements, parserContext, builder.getBeanDefinition());

        invokerProvider.addPropertyValue(XMLConstants.STEPS_PROVIDERS, parsedList);

        builder.addPropertyValue(XMLConstants.INVOKER_PROVIDER, invokerProvider.getBeanDefinition());
    }
}
