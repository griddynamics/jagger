package com.griddynamics.jagger.xml.beanParsers.task;

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
