package com.griddynamics.jagger.xml.beanParsers.report;

import static com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider.IDENTITY_SESSION;
import static com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser.setBeanProperty;
import static com.griddynamics.jagger.xml.beanParsers.XMLConstants.BASELINE_ID;
import static com.griddynamics.jagger.xml.beanParsers.XMLConstants.BASELINE_SESSION_ID;
import static com.griddynamics.jagger.xml.beanParsers.XMLConstants.BASELINE_SESSION_PROVIDER;
import static com.griddynamics.jagger.xml.beanParsers.XMLConstants.CONTEXT;
import static com.griddynamics.jagger.xml.beanParsers.XMLConstants.DEFAULT_REPORTING_SERVICE;
import static com.griddynamics.jagger.xml.beanParsers.XMLConstants.EXTENSIONS;
import static com.griddynamics.jagger.xml.beanParsers.XMLConstants.PROVIDER_REGISTRY;
import static com.griddynamics.jagger.xml.beanParsers.XMLConstants.REPORTER_REGISTRY;
import static com.griddynamics.jagger.xml.beanParsers.XMLConstants.REPORTING_CONTEXT;
import static com.griddynamics.jagger.xml.beanParsers.XMLConstants.SESSION_COMPARATOR;
import static com.griddynamics.jagger.xml.beanParsers.XMLConstants.SESSION_COMPARATORS_ELEMENT;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;
import static org.springframework.util.xml.DomUtils.getChildElementByTagName;

import com.griddynamics.jagger.engine.e1.reporting.OverallSessionComparisonReporter;
import com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider;
import com.griddynamics.jagger.extension.ExtensionRegistry;
import com.griddynamics.jagger.reporting.ReportingContext;
import com.griddynamics.jagger.reporting.ReportingService;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
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
        super.doParse(element, parserContext, builder);

        builder.setParentName(DEFAULT_REPORTING_SERVICE);

        //parse extensions
        Element extensionsElement = getChildElementByTagName(element, EXTENSIONS);
        if (extensionsElement != null)
            parserContext.getDelegate().parseCustomElement(extensionsElement);

        Element sessionComparatorsElement = getChildElementByTagName(element, SESSION_COMPARATORS_ELEMENT);
        if (sessionComparatorsElement != null) {
            //context
            BeanDefinitionBuilder reportContext = genericBeanDefinition(ReportingContext.class);
            reportContext.setParentName(REPORTING_CONTEXT);
            String reportContextName = parserContext.getReaderContext().generateBeanName(reportContext.getBeanDefinition());
            parserContext.getRegistry().registerBeanDefinition(reportContextName, reportContext.getBeanDefinition());

            //parse comparators
            BeanDefinitionBuilder registry = genericBeanDefinition(ExtensionRegistry.class);
            registry.setParentName(REPORTER_REGISTRY);

            BeanDefinitionBuilder comparisonReporter = genericBeanDefinition(OverallSessionComparisonReporter.class);
            comparisonReporter.setParentName("abstractReportProviderBean");
            comparisonReporter.addPropertyValue("template", "/reporting/session-comparison-report.jrxml");
            comparisonReporter.addPropertyReference("statusImageProvider", "statusImageProvider");

            //parse baselineProvider
            BeanDefinitionBuilder baseLineSessionProvider = genericBeanDefinition(BaselineSessionProvider.class);
            String baseLineId = sessionComparatorsElement.getAttribute(BASELINE_ID);
            if (!baseLineId.isEmpty()) {
                baseLineSessionProvider.addPropertyValue(BASELINE_SESSION_ID, baseLineId);
            } else {
                baseLineSessionProvider.addPropertyValue(BASELINE_SESSION_ID, IDENTITY_SESSION);
            }
            reportContext.addPropertyValue(BASELINE_SESSION_PROVIDER, baseLineSessionProvider.getBeanDefinition());

            //parse comparators chain
            setBeanProperty(SESSION_COMPARATOR, sessionComparatorsElement, parserContext, comparisonReporter.getBeanDefinition());

            //set all parameters
            ManagedMap registryMap = new ManagedMap();
            registryMap.setMergeEnabled(true);
            registryMap.put(XMLConstants.SESSION_COMPARISON, comparisonReporter.getBeanDefinition());

            registry.addPropertyValue(EXTENSIONS, registryMap);

            reportContext.addPropertyValue(PROVIDER_REGISTRY, registry.getBeanDefinition());

            builder.addPropertyReference(CONTEXT, reportContextName);
        }
    }
}
