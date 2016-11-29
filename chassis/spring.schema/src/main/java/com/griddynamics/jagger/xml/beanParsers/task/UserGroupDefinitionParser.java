package com.griddynamics.jagger.xml.beanParsers.task;

import com.griddynamics.jagger.engine.e1.scenario.FixedDelay;
import com.griddynamics.jagger.engine.e1.scenario.UserGroupsClockConfiguration;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

@Deprecated
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
public class UserGroupDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return UserGroupsClockConfiguration.class;
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if(element.getAttribute(XMLConstants.TICK_INTERVAL).isEmpty()){
            builder.addPropertyValue(XMLConstants.TICK_INTERVAL, XMLConstants.DEFAULT_TICK_INTERVAL);
        }else{
            builder.addPropertyValue(XMLConstants.TICK_INTERVAL, element.getAttribute("tickInterval"));
        }
        element.removeAttribute(XMLConstants.TICK_INTERVAL);
        if (!element.getAttribute(XMLConstants.DELAY).isEmpty()){
            builder.addPropertyValue(XMLConstants.DELAY, new FixedDelay(Integer.parseInt(element.getAttribute(XMLConstants.DELAY))));
        }
        element.removeAttribute(XMLConstants.DELAY);

        BeanDefinition bd = new UserDefinitionParser().parse(element, parserContext);
        ManagedList users = new ManagedList();
        users.add(bd);
        builder.addPropertyValue(XMLConstants.USERS, users);


        //TODO refactor CustomBeanDefinitionParser
        List<String> attributes = new ArrayList<String>(element.getAttributes().getLength());
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            attributes.add(element.getAttributes().item(i).getNodeName());
        }
        for (String attribute : attributes){
            element.removeAttribute(attribute);
        }
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

    }
}
