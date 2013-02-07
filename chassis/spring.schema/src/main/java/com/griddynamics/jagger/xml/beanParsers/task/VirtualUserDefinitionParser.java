package com.griddynamics.jagger.xml.beanParsers.task;

import com.griddynamics.jagger.user.ProcessingConfig;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/10/12
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualUserDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.Task.VirtualUser.class;
    }
}
