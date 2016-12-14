package com.griddynamics.jagger.xml.beanParsers.configuration;

import com.griddynamics.jagger.xml.beanParsers.ListCustomDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/16/13
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
public class TestSuiteDefinitionParser extends ListCustomDefinitionParser {

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        Object result = parseCustomElements(DomUtils.getChildElementsByTagName(element, XMLConstants.TEST_GROUP), parserContext, builder.getBeanDefinition());
        if (result != null){
            builder.addConstructorArgValue(result);
        }
    }
}
