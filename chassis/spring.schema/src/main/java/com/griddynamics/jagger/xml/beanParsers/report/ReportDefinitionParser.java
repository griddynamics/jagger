package com.griddynamics.jagger.xml.beanParsers.report;

import com.griddynamics.jagger.engine.e1.reporting.OverallSessionComparisonReporter;
import com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider;
import com.griddynamics.jagger.extension.ExtensionRegistry;
import com.griddynamics.jagger.reporting.ReportingContext;
import com.griddynamics.jagger.reporting.ReportingService;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: nmusienko
 * Date: 03.12.12
 * Time: 19:26
 * To change this template use File | Settings | File Templates.
 */
public class ReportDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ReportingService.class;
    }

    @Override
    public void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element,parserContext,builder);

        builder.setParentName(XMLConstants.DEFAULT_REPORTING_SERVICE);

        //TODO
        Element extensionsElement = DomUtils.getChildElementByTagName(element, XMLConstants.EXTENSIONS);
        if (extensionsElement != null)
            parserContext.getDelegate().parseCustomElement(extensionsElement);

        //parse comparators
        BeanDefinitionBuilder registry = BeanDefinitionBuilder.genericBeanDefinition(ExtensionRegistry.class);
        registry.setParentName(XMLConstants.REPORTER_REGISTRY);

        BeanDefinitionBuilder comparisonReporter = BeanDefinitionBuilder.genericBeanDefinition(OverallSessionComparisonReporter.class);
        comparisonReporter.setParentName(XMLConstants.REPORTER_COMPARISON);

        //parse baselineProvider
        BeanDefinitionBuilder baseLineSessionProvider = BeanDefinitionBuilder.genericBeanDefinition(BaselineSessionProvider.class);
        baseLineSessionProvider.setParentName(XMLConstants.REPORTER_BASELINE_PROVIDER);

        Element sessionComparatorsElement = DomUtils.getChildElementByTagName(element, XMLConstants.SESSION_COMPARATORS_ELEMENT);

        if (sessionComparatorsElement != null){
            String baseLineId = sessionComparatorsElement.getAttribute(XMLConstants.BASELINE_ID);
            if (!baseLineId.isEmpty()){
                baseLineSessionProvider.addPropertyValue(XMLConstants.SESSION_ID, baseLineId);
            }else{
                baseLineSessionProvider.addPropertyValue(XMLConstants.SESSION_ID, BaselineSessionProvider.IDENTITY_SESSION);
            }

            BeanDefinition comparators = parserContext.getDelegate().parseCustomElement(sessionComparatorsElement, builder.getBeanDefinition());
            comparisonReporter.addPropertyValue(XMLConstants.SESSION_COMPARATOR, comparators);
        }

        comparisonReporter.addPropertyValue(XMLConstants.BASELINE_SESSION_PROVIDER, baseLineSessionProvider.getBeanDefinition());

        ManagedMap registryMap = new ManagedMap();
        registryMap.put(XMLConstants.SESSION_COMPARISON, comparisonReporter.getBeanDefinition());

        registry.addPropertyValue(XMLConstants.EXTENSIONS, registryMap);

        BeanDefinitionBuilder reportContext = BeanDefinitionBuilder.genericBeanDefinition(ReportingContext.class);
        reportContext.setParentName(XMLConstants.REPORTING_CONTEXT);
        reportContext.addPropertyValue(XMLConstants.PROVIDER_REGISTRY, registry.getBeanDefinition());

        builder.addPropertyValue(XMLConstants.CONTEXT, reportContext.getBeanDefinition());
    }
}
