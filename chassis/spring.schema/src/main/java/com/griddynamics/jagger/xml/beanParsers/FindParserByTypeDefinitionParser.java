package com.griddynamics.jagger.xml.beanParsers;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * User: kgribov
 * Date: 1/22/13
 * Time: 4:25 PM
 */
@Deprecated
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
public class FindParserByTypeDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String type = element.getSchemaTypeInfo().getTypeName();
        if (type.equals(element.getTagName()+"Type")){
            return new PrimitiveDefinitionParser().parse(element, parserContext);
        }else{
            Document parent = element.getParentNode().getOwnerDocument();
            parent.renameNode(element, null, type);
            return parserContext.getReaderContext().getNamespaceHandlerResolver().resolve(XMLConstants.DEFAULT_NAMESPACE).parse(element, parserContext);
        }
    }
}
