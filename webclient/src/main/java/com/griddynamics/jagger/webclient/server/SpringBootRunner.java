package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.JaggerWebClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;

/**
 * @author asokol
 *         created 9/21/16
 */
@Import({ApplicationContext.class, PersistenceConfig.class})
@Configuration
@SpringBootApplication
public class SpringBootRunner extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application;
    }

    @RequestMapping(value = "/download")
    @Bean
    Servlet downloadServlet() {
        return new HttpRequestHandlerServlet();
    }

    @Bean
    ServletRegistrationBean gwt() {
        ServletRegistrationBean bean = new ServletRegistrationBean();
        bean.setServlet(new DispatcherServlet());
        bean.setLoadOnStartup(1);
        bean.addUrlMappings("/com.griddynamics.jagger.webclient.JaggerWebClient/rpc/*");
        return bean;
    }

    public static void main(final String... args) {
        SpringApplication.run(SpringBootRunner.class, args);
    }
}
