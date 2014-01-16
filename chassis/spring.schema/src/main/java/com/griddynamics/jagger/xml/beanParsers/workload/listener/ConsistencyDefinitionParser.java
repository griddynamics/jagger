package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.ConsistencyValidatorProvider;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/7/13
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConsistencyDefinitionParser extends AbstractValidatorDefinitionParser {

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        BeanDefinitionBuilder validatorBean = BeanDefinitionBuilder.genericBeanDefinition(ConsistencyValidatorProvider.class);
        String queryEq = element.getAttribute(XMLConstants.QUERY_EQ);
        String endpointEq = element.getAttribute(XMLConstants.ENDPOINT_EQ);
        String resultEq = element.getAttribute(XMLConstants.RESULT_EQ);
        if (!queryEq.isEmpty()) validatorBean.addPropertyReference(XMLConstants.QUERY_EQ, queryEq);
        if (!endpointEq.isEmpty()) validatorBean.addPropertyReference(XMLConstants.ENDPOINT_EQ, endpointEq);
        if (!resultEq.isEmpty()) validatorBean.addPropertyReference(XMLConstants.RESULT_EQ, resultEq);
        builder.addPropertyValue(XMLConstants.VALIDATOR, validatorBean.getBeanDefinition());
    }
}
