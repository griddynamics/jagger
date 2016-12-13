package com.griddynamics.jagger.xml.beanParsers.configuration;

import com.griddynamics.jagger.engine.e1.aggregator.workload.DurationLogProcessor;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.xml.TaskGeneratorBean;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/10/12
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
@Deprecated
public class ConfigDefinitionParser extends CustomBeanDefinitionParser {

    private boolean monitoringEnable = false;

    @Override
    protected Class getBeanClass(Element element) {
        return Configuration.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.setLazyInit(true);
        Element report = DomUtils.getChildElementByTagName(element, XMLConstants.REPORT);
        if (report!=null) {
            BeanDefinition bean = parserContext.getDelegate().parseCustomElement(report, builder.getBeanDefinition());
            builder.addPropertyValue(XMLConstants.REPORT,bean);
        }

        initListeners(element,parserContext, builder);

        //parse test-plan
        Element testPlan = DomUtils.getChildElementByTagName(element, XMLConstants.LOAD_SCENARIO);

        if (testPlan != null){
            Element loadScenarioListeners = DomUtils.getChildElementByTagName(testPlan, XMLConstants.LOAD_SCENARIO_LISTENERS);
            if (loadScenarioListeners!=null){
                setBeanListProperty("loadScenarioListeners", true, true, loadScenarioListeners, parserContext, builder.getBeanDefinition());
            }
        }

        TaskGeneratorBean generator = new TaskGeneratorBean();
        generator.getBean().getPropertyValues().addPropertyValue(XMLConstants.TEST_GROUPS, parseCustomElement(testPlan, parserContext, builder.getBeanDefinition()));
        parserContext.getRegistry().registerBeanDefinition(generator.getName(), generator.getBean());

        builder.addPropertyValue(XMLConstants.TASKS, generator.generateTasks());
    }

    protected void initListeners(Element element, ParserContext parserContext, BeanDefinitionBuilder builder){

        //listeners lists
        ManagedList slList = new ManagedList();
        ManagedList tlList = new ManagedList();

        //override durationLogProcessor if needed
        Element percentilesElement = DomUtils.getChildElementByTagName(element, XMLConstants.LATENCY);
        if (percentilesElement != null){

            BeanDefinitionBuilder durationLogProcessorBean = BeanDefinitionBuilder.genericBeanDefinition(DurationLogProcessor.class);
            durationLogProcessorBean.setParentName(XMLConstants.DURATION_LOG_PROCESSOR);

            setBeanProperty(XMLConstants.GLOBAL_PERCENTILES_KEYS, percentilesElement, parserContext, durationLogProcessorBean.getBeanDefinition());
            setBeanProperty(XMLConstants.TIME_WINDOW_PERCENTILES_KEYS, percentilesElement, parserContext, durationLogProcessorBean.getBeanDefinition());

            initStandardListeners(tlList, slList);

            //add custom durationLogProcessor
            tlList.add(durationLogProcessorBean.getBeanDefinition());

            builder.addPropertyValue(XMLConstants.TASK_EXECUTION_LISTENERS_CLASS_FIELD, tlList);
            builder.addPropertyValue(XMLConstants.SESSION_EXECUTION_LISTENERS_CLASS_FIELD, slList);

            //add user's listeners
            Element sListenerGroup = DomUtils.getChildElementByTagName(element, XMLConstants.SESSION_EXECUTION_LISTENERS);
            setBeanListProperty(XMLConstants.SESSION_EXECUTION_LISTENERS_CLASS_FIELD, false, true, sListenerGroup, parserContext, builder.getBeanDefinition());

            //add user's listeners
            Element tListenerGroup = DomUtils.getChildElementByTagName(element, XMLConstants.TASK_EXECUTION_LISTENERS);
            setBeanListProperty(XMLConstants.TASK_EXECUTION_LISTENERS_CLASS_FIELD, false, true, tListenerGroup, parserContext, builder.getBeanDefinition());

        }else{
            if (builder.getBeanDefinition().getParentName() == null){
                initStandardListeners(tlList, slList);

                //add standard durationLogProcessor
                tlList.add(new RuntimeBeanReference(XMLConstants.DURATION_LOG_PROCESSOR));

                builder.addPropertyValue(XMLConstants.TASK_EXECUTION_LISTENERS_CLASS_FIELD, tlList);
                builder.addPropertyValue(XMLConstants.SESSION_EXECUTION_LISTENERS_CLASS_FIELD, slList);
            }
            //add user's listeners
            Element sListenerGroup = DomUtils.getChildElementByTagName(element, XMLConstants.SESSION_EXECUTION_LISTENERS);
            setBeanListProperty(XMLConstants.SESSION_EXECUTION_LISTENERS_CLASS_FIELD, true, true, sListenerGroup, parserContext, builder.getBeanDefinition());

            //add user's listeners
            Element tListenerGroup = DomUtils.getChildElementByTagName(element, XMLConstants.TASK_EXECUTION_LISTENERS);
            setBeanListProperty(XMLConstants.TASK_EXECUTION_LISTENERS_CLASS_FIELD, true, true, tListenerGroup, parserContext, builder.getBeanDefinition());
        }

        Element monitoring = DomUtils.getChildElementByTagName(element, XMLConstants.MONITORING);
        builder.addPropertyValue("monitoringConfiguration", parseCustomElement(monitoring, parserContext, builder.getBeanDefinition()));


    }

    protected void initStandardListeners(ManagedList tlList, ManagedList slList){
        //add standard listeners
        for (String sessionListener : XMLConstants.STANDARD_SESSION_EXEC_LISTENERS){
            slList.add(new RuntimeBeanReference(sessionListener));
        }

        for (String sessionListener : XMLConstants.STANDARD_TASK_EXEC_LISTENERS){
            tlList.add(new RuntimeBeanReference(sessionListener));
        }
    }
}
