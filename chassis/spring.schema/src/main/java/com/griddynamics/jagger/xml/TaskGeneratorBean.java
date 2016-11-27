package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.master.configuration.UserTaskGenerator;
import com.griddynamics.jagger.user.TestSuitConfiguration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/15/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
// TODO: GD 11/25/16 Should be removed with xml configuration JFG-906
@Deprecated
public class TaskGeneratorBean {

    private static int id = 0;

    private String name;
    private BeanDefinition bean;

    public TaskGeneratorBean(){
        name = "generator"+Integer.toString(id++);
        bean = BeanDefinitionBuilder.genericBeanDefinition(TestSuitConfiguration.class).getBeanDefinition();
        bean.setLazyInit(true);
    }

    public String generateTasks(){
        return "#{"+name+".generate()}";
    }

    public String getName(){
        return name;
    }

    public BeanDefinition getBean(){
        return bean;
    }

    @Override
    public String toString(){
        return name;
    }
}

