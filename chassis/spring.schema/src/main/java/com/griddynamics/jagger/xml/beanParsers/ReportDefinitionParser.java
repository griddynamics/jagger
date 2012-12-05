package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.reporting.ReportingService;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

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
        element.setAttribute(XMLConstants.ID,XMLConstants.CUSTOM_REPORTING_SERVICE);
        builder.setParentName(XMLConstants.DEFAULT_REPORTING_SERVICE);


        List<Element> elements = DomUtils.getChildElements(element);
        for(Element el : elements){
            parserContext.getDelegate().parseCustomElement(el);
        }

        if(parserContext.getRegistry().getAliases(XMLConstants.REPORTING_SERVICE).length>0){
            parserContext.getRegistry().removeAlias(XMLConstants.REPORTING_SERVICE);
        }
        parserContext.getRegistry().registerAlias(XMLConstants.CUSTOM_REPORTING_SERVICE, XMLConstants.REPORTING_SERVICE);

    }
}
