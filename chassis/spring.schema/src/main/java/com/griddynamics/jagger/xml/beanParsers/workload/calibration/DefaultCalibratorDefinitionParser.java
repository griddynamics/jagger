package com.griddynamics.jagger.xml.beanParsers.workload.calibration;

import com.griddynamics.jagger.engine.e1.scenario.OneNodeCalibrator;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/7/13
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultCalibratorDefinitionParser extends CustomBeanDefinitionParser{

    @Override
    protected Class getBeanClass(Element element) {
        return OneNodeCalibrator.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        //do nothing
    }
}
