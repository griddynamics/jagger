package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.invoker.http.HttpQuery;
import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 2/19/13
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class JaggerProvidersTest {

    private ApplicationContext ctx;

    @BeforeClass
    public void testInit() throws Exception{
        URL directory = new URL("file:" + "../configuration/");
        Properties environmentProperties = new Properties();
        JaggerLauncher.loadBootProperties(directory, "profiles/local/environment.properties", environmentProperties);
        environmentProperties.put("chassis.master.configuration.include",environmentProperties.get("chassis.master.configuration.include")+", ../spring.schema/src/test/resources/example-providers.conf.xml1");
        ctx = JaggerLauncher.loadContext(directory,"chassis.master.configuration",environmentProperties);
    }

    @Test
    public void testEndpointProvider(){
        Iterable endpoints = (Iterable)ctx.getBean("endpointProvider");
        Assert.assertEquals(getSize(endpoints), 3);

        String firstEndpoint =  (String)endpoints.iterator().next();
        Assert.assertEquals(firstEndpoint, "http://localhost:8090/sleep/5");
    }

    @Test
    public void testQueryProvider(){
        Iterable queries = (Iterable)ctx.getBean("queryProvider");
        Assert.assertNotNull(queries);
        Assert.assertEquals(getSize(queries), 3);

        HttpQuery query =  (HttpQuery)queries.iterator().next();
        Assert.assertNotNull(query);
        int timeOut = (Integer)query.getClientParams().get("http.protocol.max-redirects");
        Assert.assertEquals(timeOut, 2);
    }

    private int getSize(Iterable iterable){
        int size = 0;
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()){
            iterator.next();
            size++;
        }
        return size;
    }
}
