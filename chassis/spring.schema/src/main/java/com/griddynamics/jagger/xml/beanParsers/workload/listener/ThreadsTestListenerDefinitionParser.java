package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.engine.e1.collector.CollectThreadsTestListener;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/2/13
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class ThreadsTestListenerDefinitionParser extends CustomBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return CollectThreadsTestListener.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

    }
}
