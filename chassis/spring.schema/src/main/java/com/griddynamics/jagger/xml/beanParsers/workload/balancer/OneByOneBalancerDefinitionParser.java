package com.griddynamics.jagger.xml.beanParsers.workload.balancer;

import com.griddynamics.jagger.invoker.SimpleCircularLoadBalancer;
import com.griddynamics.jagger.invoker.OneByOnePairSupplierFactory;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/22/13
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class OneByOneBalancerDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return BeanDefinitionBuilder.genericBeanDefinition(SimpleCircularLoadBalancer.class)
                .addPropertyValue(XMLConstants.PAIR_SUPPLIER_FACTORY, new OneByOnePairSupplierFactory())
                .getBeanDefinition();
    }
}
