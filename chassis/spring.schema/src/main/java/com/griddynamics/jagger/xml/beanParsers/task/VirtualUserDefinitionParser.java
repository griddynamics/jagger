package com.griddynamics.jagger.xml.beanParsers.task;

import com.griddynamics.jagger.engine.e1.scenario.FixedDelay;
import com.griddynamics.jagger.engine.e1.scenario.VirtualUsersClockConfiguration;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/10/12
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
public class VirtualUserDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return VirtualUsersClockConfiguration.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String tickInterval = element.getAttribute(XMLConstants.TICK_INTERVAL);
        if (tickInterval.isEmpty()){
            builder.addPropertyValue(XMLConstants.TICK_INTERVAL, XMLConstants.DEFAULT_TICK_INTERVAL);
        } else {
            builder.addPropertyValue(XMLConstants.TICK_INTERVAL, tickInterval);
        }
        if (!element.getAttribute(XMLConstants.DELAY).isEmpty()){
            builder.addPropertyValue(XMLConstants.DELAY, new FixedDelay(Integer.parseInt(element.getAttribute(XMLConstants.DELAY))));
        }
        if (!element.getAttribute(XMLConstants.COUNT).isEmpty()){
            builder.addPropertyValue(XMLConstants.COUNT, Integer.parseInt(element.getAttribute(XMLConstants.COUNT)));
        }
    }
}
