package com.griddynamics.jagger.xml.beanParsers;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/25/13
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CustomBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser{

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (element.hasAttribute(XMLConstants.PARENT)){
            String parent = element.getAttribute(XMLConstants.PARENT);
            if(!parent.isEmpty()) builder.setParentName(parent);
            element.removeAttribute(XMLConstants.PARENT);
        }
        if (element.hasAttribute(XMLConstants.XSI_TYPE)) element.removeAttribute(XMLConstants.XSI_TYPE);
        preParseAttributes(element, parserContext, builder);
        super.doParse(element, parserContext, builder);
        parse(element, parserContext, builder);
    }

    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder){
        //override
    }

    protected abstract void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder);

    public static void setBeanListProperty(String propertyName, boolean mergeParent, boolean mergeOriginal, Element listParentElement, ParserContext parserContext, BeanDefinition bean){
        ManagedList result = parseCustomListElement(listParentElement, parserContext, bean);
        if (result != null){
            if (mergeOriginal){
                PropertyValue prop = bean.getPropertyValues().getPropertyValue(propertyName);
                if (prop != null){
                    ManagedList origin = (ManagedList)prop.getValue();
                    origin.addAll(result);
                    result = origin;
                }
            }
            result.setMergeEnabled(mergeParent);
            bean.getPropertyValues().addPropertyValue(propertyName, result);
        }
    }

    public static void setBeanProperty(String propertyName, Element element, ParserContext parserContext, BeanDefinition bean){
        if (element==null) return;
        bean.getPropertyValues().add(propertyName, parseCustomElement(element, parserContext, bean));
    }

    public static void addConstructorListArg(Element listParentElement, ParserContext parserContext, BeanDefinition bean){
        ManagedList result = parseCustomListElement(listParentElement, parserContext, bean);
        if (result==null) return;
        bean.getConstructorArgumentValues().addGenericArgumentValue(result);
    }

    public static void addConstructorArg(Element element, ParserContext parserContext, BeanDefinition bean){
        if (element==null) return;
        bean.getConstructorArgumentValues().addGenericArgumentValue(parseCustomElement(element, parserContext, bean));
    }

    public static Object parseCustomElement(Element element, ParserContext parserContext, BeanDefinition bean){
        if (element==null){
            return null;
        }
        if (element.hasAttribute(XMLConstants.ATTRIBUTE_REF)){
            String ref = element.getAttribute(XMLConstants.ATTRIBUTE_REF);
            if (!ref.isEmpty()){
                return new RuntimeBeanReference(ref);
            }else{
                return parserContext.getDelegate().parseCustomElement(element, bean);
            }
        }else{
            return parserContext.getDelegate().parseCustomElement(element, bean);
        }
    }

    public static ManagedList parseCustomListElement(Element listParentElement, ParserContext parserContext, BeanDefinition bean){
        if (listParentElement == null){
            return null;
        }

        List<Element> elements = DomUtils.getChildElements(listParentElement);

        return parseCustomElements(elements, parserContext, bean);
    }

    public static ManagedList parseCustomElements(List<Element> elements, ParserContext parserContext, BeanDefinition bean){
        ManagedList result = new ManagedList();
        if (elements != null && !elements.isEmpty()){
            for (Element el : elements){
                if (el.hasAttribute(XMLConstants.ATTRIBUTE_REF)){
                    String ref = el.getAttribute(XMLConstants.ATTRIBUTE_REF);
                    if (!ref.isEmpty()){
                        result.add(new RuntimeBeanReference(ref));
                    }else{
                        result.add(parserContext.getDelegate().parsePropertySubElement(el, bean));
                    }
                }else{
                    result.add(parserContext.getDelegate().parsePropertySubElement(el, bean));
                }
            }
        }
        return result;
    }
}
