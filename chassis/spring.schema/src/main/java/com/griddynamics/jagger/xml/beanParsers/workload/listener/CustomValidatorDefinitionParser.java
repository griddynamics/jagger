package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.scenario.ReflectionProvider;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * User: kirilkadurilka
 * Date: 11.03.13
 * Time: 12:48
 */
@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class CustomValidatorDefinitionParser extends AbstractValidatorDefinitionParser {

    private static final Logger log = LoggerFactory.getLogger(CustomValidatorDefinitionParser.class);

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        ReflectionProvider provider = new ReflectionProvider();
        try{
            provider.setClazz(Class.forName(element.getAttribute(XMLConstants.VALIDATOR)));
        }catch (ClassNotFoundException ex){
            log.warn("Can't find class="+element.getAttribute(XMLConstants.VALIDATOR), ex);
        }
        builder.addPropertyValue(XMLConstants.VALIDATOR, provider);
    }
}
