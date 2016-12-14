package com.griddynamics.jagger.xml.beanParsers.workload.balancer;

import com.griddynamics.jagger.invoker.RoundRobinPairSupplierFactory;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/22/13
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoundRobinBalancerDefinitionParser extends LoadBalancerDefinitionParser {

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        builder.addPropertyValue(XMLConstants.PAIR_SUPPLIER_FACTORY, new RoundRobinPairSupplierFactory());
    }
}
