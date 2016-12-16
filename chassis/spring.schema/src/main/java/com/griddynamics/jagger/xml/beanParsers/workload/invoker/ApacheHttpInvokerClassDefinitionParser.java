package com.griddynamics.jagger.xml.beanParsers.workload.invoker;

import com.griddynamics.jagger.invoker.http.ApacheHttpInvoker;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: amikryukov
 * Date: 5/22/13
 */
@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class ApacheHttpInvokerClassDefinitionParser extends InvokerClassDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return getClassNameBeanDefinition(ApacheHttpInvoker.class);
    }
}
