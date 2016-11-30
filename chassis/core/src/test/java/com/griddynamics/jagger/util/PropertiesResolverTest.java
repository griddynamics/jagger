/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class PropertiesResolverTest {

    @Test
    public void testPropertiesResolver() throws MalformedURLException {
        System.setProperty("test.properties.resolver.test","spring/priority.user-1.properties,spring/priority.user-2.properties");

        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring/test-properties-resolver.xml"});
        // test
        Assert.assertEquals(context.getBean("stringA"), "x-override");
        Assert.assertEquals(context.getBean("stringB"), "x-override/ex");
        Assert.assertEquals(context.getBean("stringC"), "x-override/y-default/ex");
        Assert.assertEquals(context.getBean("stringD"), "x-override+y-default");
        Assert.assertEquals(context.getBean("stringE"), "z-priority+x-override+y-default");
        Assert.assertEquals(context.getBean("stringF"), "a-priority-user-1-override");
        Assert.assertEquals(context.getBean("stringG"), "b-priority-user-2-override");
    }
}
