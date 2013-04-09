package com.griddynamics.jagger.xml.beanParsers.report;

import com.griddynamics.jagger.engine.e1.sessioncomparation.ConfigurableSessionComparator;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: nmusienko
 * Date: 30.11.12
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class SessionComparatorsDefinitionParser extends AbstractSimpleBeanDefinitionParser {


    @Override
    protected Class getBeanClass(Element element) {
        return ConfigurableSessionComparator.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.setParentName(XMLConstants.REPORTER_SESSION_COMPARATOR);
        String decisionMaker=element.getAttribute(XMLConstants.STRATEGY);
        if(StringUtils.hasText(decisionMaker)){
            builder.addPropertyReference(XMLConstants.DECISION_MAKER, getDecisionMaker(decisionMaker));
        }

        CustomBeanDefinitionParser.setBeanListProperty(XMLConstants.COMPARATOR_CHAIN, false, false, element, parserContext, builder.getBeanDefinition());
    }

    private String getDecisionMaker(String decisionMaker) {
        if(decisionMaker.equals(XMLConstants.WORST_CASE)){
            return XMLConstants.WORST_CASE_DECISION_MAKER;
        }
        return null;
    }
}
