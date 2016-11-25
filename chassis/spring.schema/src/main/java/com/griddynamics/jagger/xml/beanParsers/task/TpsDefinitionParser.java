package com.griddynamics.jagger.xml.beanParsers.task;

import com.griddynamics.jagger.engine.e1.scenario.TpsClockConfiguration;
import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.util.Parser;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/6/12
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
public class TpsDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return TpsClockConfiguration.class;
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (element.getAttribute(XMLConstants.TICK_INTERVAL).isEmpty()){
            builder.addPropertyValue(XMLConstants.TICK_INTERVAL, XMLConstants.DEFAULT_TICK_INTERVAL);
        }
        if (!element.getAttribute(XMLConstants.MAX_THREAD_NUMBER).isEmpty()){
            builder.addPropertyValue(XMLConstants.MAX_THREAD_NUMBER, element.getAttribute(XMLConstants.MAX_THREAD_NUMBER));
        }else{
            builder.addPropertyValue(XMLConstants.MAX_THREAD_NUMBER, XMLConstants.DEFAULT_MAX_THREAD_COUNT);
        }
        if (element.hasAttribute(XMLConstants.WARM_UP_TIME)) {
            builder.addPropertyValue(XMLConstants.WARM_UP_TIME, Parser.parseTimeMillis(element.getAttribute(XMLConstants.WARM_UP_TIME)));
            element.removeAttribute(XMLConstants.WARM_UP_TIME);
        }
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

    }
}
