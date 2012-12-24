package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.user.ProcessingConfig;
import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/14/12
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class JaggerTpsAndInvocationTest {

    private ApplicationContext context;

    @Test
    public void testContext(){
        context = new ClassPathXmlApplicationContext("/example-tps-invocation.conf.xml1");
    }

    @Test
    public void testTpsTask(){
        ProcessingConfig testPlan = (ProcessingConfig)context.getBean("tp1");
        Assert.assertEquals(3, testPlan.getTests().size());

        //first tps(inner)
        Assert.assertNotNull(testPlan.getTests().get(1).getTasks().get(2).tps);
        Assert.assertEquals(new Integer(100), testPlan.getTests().get(1).getTasks().get(2).tps.getValue());

        //second(outer)
        Assert.assertNotNull(testPlan.getTests().get(2).getTasks().get(0).getTps().getValue());
        Assert.assertEquals(new Integer(10), testPlan.getTests().get(2).getTasks().get(0).getTps().getValue());
    }

    @Test
    public void testInvocationTask(){
        ProcessingConfig testPlan = (ProcessingConfig)context.getBean("tp2");
        Assert.assertEquals(2, testPlan.getTests().size());

        //first invocation(inner)
        Assert.assertNotNull(testPlan.getTests().get(1).getTasks().get(0).invocation);
        Assert.assertEquals(new Integer(2), testPlan.getTests().get(1).getTasks().get(0).invocation.threads);
        //second(outer)
        Assert.assertNotNull(testPlan.getTests().get(1).getTasks().get(3).invocation);
        Assert.assertEquals(new Integer(5), testPlan.getTests().get(1).getTasks().get(3).invocation.exactcount);
    }
}
