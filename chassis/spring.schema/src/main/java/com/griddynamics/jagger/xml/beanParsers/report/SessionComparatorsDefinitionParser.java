package com.griddynamics.jagger.xml.beanParsers.report;

import com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider;
import com.griddynamics.jagger.engine.e1.sessioncomparation.ConfigurableSessionComparator;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import gnu.kawa.slib.XML;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: nmusienko
 * Date: 30.11.12
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class SessionComparatorsDefinitionParser  extends AbstractSimpleBeanDefinitionParser {


    @Override
    protected Class getBeanClass(Element element) {
        return ConfigurableSessionComparator.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        ManagedList<BeanDefinition> chain=new ManagedList<BeanDefinition>();

        builder.addPropertyValue(XMLConstants.COMPARATOR_CHAIN, chain);

        String decisionMaker=element.getAttribute(XMLConstants.STRATEGY);
        if(StringUtils.hasText(decisionMaker)){
            builder.addPropertyReference(XMLConstants.DECISION_MAKER, getDecisionMaker(decisionMaker));
        }
        String baselineSessionId=element.getAttribute(XMLConstants.BASELINE_ID);
        if(StringUtils.hasText(baselineSessionId)){
            registerBaselineSessionProvider(baselineSessionId, parserContext, builder);
        }
        element.setAttribute(XMLConstants.ID, XMLConstants.CUSTOM_SESSION_COMPARATOR);
        if(parserContext.getRegistry().getAliases(XMLConstants.SESSION_COMPARATOR).length>0){
            parserContext.getRegistry().removeAlias(XMLConstants.SESSION_COMPARATOR);
        }
        parserContext.getRegistry().registerAlias(XMLConstants.CUSTOM_SESSION_COMPARATOR, XMLConstants.SESSION_COMPARATOR);

        for(Element el: DomUtils.getChildElementsByTagName(element, XMLConstants.COMPARATOR)){
            chain.add(parserContext.getDelegate().parseCustomElement(el,builder.getBeanDefinition()));
        }

    }

    private String getDecisionMaker(String decisionMaker) {
        if(decisionMaker.equals(XMLConstants.WORST_CASE)){
            return XMLConstants.WORST_CASE_DECISION_MAKER;
        }
        return null;
    }

    private void registerBaselineSessionProvider(String baselineSessionId, ParserContext parserContext, BeanDefinitionBuilder builder) {
        BeanDefinitionBuilder provider=BeanDefinitionBuilder.rootBeanDefinition(BaselineSessionProvider.class);
        provider.addPropertyValue(XMLConstants.SESSION_ID,baselineSessionId);
        provider.addPropertyReference(XMLConstants.SESSION_ID_PROVIDER, XMLConstants.SESSION_ID_PROVIDER);
        parserContext.getRegistry().registerBeanDefinition(XMLConstants.CUSTOM_BASELINE_SESSION_PROVIDER,provider.getBeanDefinition());
        if(parserContext.getRegistry().getAliases(XMLConstants.BASELINE_SESSION_PROVIDER).length>0){
            parserContext.getRegistry().removeAlias(XMLConstants.BASELINE_SESSION_PROVIDER);
        }
        parserContext.getRegistry().registerAlias(XMLConstants.CUSTOM_BASELINE_SESSION_PROVIDER, XMLConstants.BASELINE_SESSION_PROVIDER);
    }
}
