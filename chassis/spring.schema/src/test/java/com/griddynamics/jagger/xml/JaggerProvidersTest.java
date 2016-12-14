package com.griddynamics.jagger.xml;

import static com.griddynamics.jagger.JaggerLauncher.RDB_CONFIGURATION;

import com.griddynamics.jagger.JaggerLauncher;
import com.griddynamics.jagger.invoker.http.HttpQuery;
import com.griddynamics.jagger.storage.rdb.H2DatabaseServer;
import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

/**
 * User: kgribov
 * Date: 2/19/13
 * Time: 12:27 PM
 */
public class JaggerProvidersTest {

    private static ApplicationContext ctx;
    private static H2DatabaseServer dbServer;

    @BeforeClass
    public static void testInit() throws Exception{
        URL directory = new URL("file:" + "../configuration/");
        Properties environmentProperties = new Properties();
        JaggerLauncher.loadBootProperties(directory, "profiles/local/environment.properties", environmentProperties);
        environmentProperties.put("chassis.master.configuration.include",environmentProperties.get("chassis.master.configuration.include")+", ../spring.schema/src/test/resources/example-providers.conf.xml1");

        ApplicationContext rdbContext = JaggerLauncher.loadContext(directory, RDB_CONFIGURATION, environmentProperties);
        dbServer = (H2DatabaseServer) rdbContext.getBean("databaseServer");
        dbServer.run();

        ctx = JaggerLauncher.loadContext(directory,"chassis.master.configuration",environmentProperties);
    }

    @AfterClass
    public static void testShutdown() {
        dbServer.terminate();
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
        String queryWord = query.getMethodParams().get("query");
        Assert.assertEquals(queryWord, "griddynamics");
    }

    @Test
    public void testFileQueryProvider() {
        Iterable queries = (Iterable)ctx.getBean("fileQueryProvider");
        Iterator iterator = queries.iterator();
        Assert.assertEquals("get_text", iterator.next());
        Assert.assertEquals("get_data", iterator.next());
        Assert.assertEquals("get_all", iterator.next());
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
