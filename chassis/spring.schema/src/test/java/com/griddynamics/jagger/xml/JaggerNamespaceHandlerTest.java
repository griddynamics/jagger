package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.user.ProcessingConfig;
import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

public class JaggerNamespaceHandlerTest {

    @Test
    public void beanTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-plan.conf.xml1");
        Object bean = ctx.getBean("bean1");
        Assert.assertNotNull(bean);
    }

    @Test
    public void testPlanTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-plan.conf.xml1");
        ProcessingConfig processingConfig = (ProcessingConfig) ctx.getBean("tp1");
        checkTest(processingConfig.getTests().get(0), null, 2);
        checkTestTst1(processingConfig.getTests().get(1));
    }

    @Test
    public void processingTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-plan.conf.xml1");
        ProcessingConfig processingConfig = (ProcessingConfig) ctx.getBean("tp2");
        checkTest(processingConfig.getTests().get(0), null, 2);
        checkTestTst1(processingConfig.getTests().get(1));
    }

    @Test
    public void invocationTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-plan.conf.xml1");
        ProcessingConfig.Test.Task.Invocation invocation = (ProcessingConfig.Test.Task.Invocation)ctx.getBean("i1");
        checkInvocationI1(invocation);
    }

    @Test
    public void taskTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-plan.conf.xml1");
        ProcessingConfig.Test.Task task = (ProcessingConfig.Test.Task)ctx.getBean("t1");
        checkTaskT1(task);
    }

    @Test
    public void testTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-plan.conf.xml1");
        ProcessingConfig.Test test = (ProcessingConfig.Test)ctx.getBean("tst1");
        checkTestTst1(test);
    }

    @Test
    public void taskInvocationTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-plan.conf.xml1");
        ProcessingConfig.Test.Task task = (ProcessingConfig.Test.Task)ctx.getBean("t2");
        checkTaskT2(task);
    }

    @Test
    public void userTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-plan.conf.xml1");
        ProcessingConfig.Test.Task.User user = (ProcessingConfig.Test.Task.User)ctx.getBean("u1");
        checkUserU1(user);
    }

    private void checkUserU1(ProcessingConfig.Test.Task.User user) {
        checkUser(user, "50", "2h", "0", "0");
    }

    private void checkTaskT2(ProcessingConfig.Test.Task task) {
        Assert.assertEquals("task2",    task.getName());
        Assert.assertEquals(24250, (int)task.getSample());
        Assert.assertEquals(10,    (int)task.getDelay());
        Assert.assertEquals("bean2",    task.getBean());
        Assert.assertEquals("300",      task.getDuration());
        Assert.assertNotNull(task.getInvocation());
        checkInvocationI1(task.getInvocation());
    }

    private void checkTestTst1(ProcessingConfig.Test test) {
        checkTest(test, "2h", 2);
        checkTask(test.getTasks().get(0), null, 34250, 0, "getTopLevelCategoryTreeCatalogService", null);
        checkTaskT1(test.getTasks().get(1));
    }

    private void checkTest(ProcessingConfig.Test test, String duration, int tasks) {
        Assert.assertEquals(duration, test.getDuration());
        Assert.assertEquals(tasks, test.getTasks().size());
    }

    private void checkTaskT1(ProcessingConfig.Test.Task task) {
        checkTask(task, "task1", 34250, 0, "bean1", "200");
    }

    private void checkInvocationI1(ProcessingConfig.Test.Task.Invocation invocation) {
        checkInvocation(invocation, 2, 5);
    }

    private void checkInvocation(ProcessingConfig.Test.Task.Invocation invocation, int threads, int exactcount) {
        Assert.assertEquals(threads,    (int) invocation.getThreads());
        Assert.assertEquals(exactcount, (int) invocation.getExactcount());
    }

    private void checkTask(ProcessingConfig.Test.Task task, String name, int sample, int delay, String bean, String duration) {
        Assert.assertEquals(name,         task.getName());
        Assert.assertEquals(sample, (int) task.getSample());
        Assert.assertEquals(delay,  (int) task.getDelay());
        Assert.assertEquals(bean,         task.getBean());
        Assert.assertEquals(duration,     task.getDuration());
    }

    private void checkUser(ProcessingConfig.Test.Task.User user, String count, String life, String startIn, String startBy) {
        Assert.assertEquals(count,      user.getCount());
        Assert.assertEquals(life,       user.getLife());
        Assert.assertEquals(startIn,    user.getStartIn());
        Assert.assertEquals(startBy,    user.getStartBy());
    }
}
