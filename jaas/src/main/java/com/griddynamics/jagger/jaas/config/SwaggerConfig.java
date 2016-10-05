package com.griddynamics.jagger.jaas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@EnableSwagger2
@Configuration
/**
 * Configures Swagger REST API docs. Available at /swagger-ui.html url.
 */
public class SwaggerConfig {

    @Value("${application.version}")
    private String version;

    /**
     * Docket object for swagger configuration.
     *
     * @return Docket object.
     */
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/jaas.*"))
                .build()
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, responseMessages());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Jagger as a Service")
                .description("JaaS in a main artifact in Jagger 2. It is an always listening component.\n\n"
                        + "It provides different information through its REST API.\n\n"
                        + "JaaS artifact packed as an all in jar (with all dependencies inside) with embedded Tomcat.\n\n"
                        + "JaaS based on Spring Boot, so its properties can be configured using one of Spring Boot ways"
                        + "by default JaaS is listening on port 8080.\n"
                        + "to change it just override property \"server.port\".")
                .license("GNU LESSER GENERAL PUBLIC LICENSE Version 2.1")
                .licenseUrl("https://github.com/griddynamics/jagger/blob/master/license.txt")
                .version(version)
                .build();
    }

    private List<ResponseMessage> responseMessages() {
        List<ResponseMessage> responseMessages = new ArrayList<>();
        responseMessages
                .add(new ResponseMessageBuilder()
                        .code(500)
                        .message("500 - Internal server error.\n\n"
                                + "There is a problem with the resource you are looking for, and it can not be dispayed.")
                        .responseModel(new ModelRef("Error"))
                        .build());
        return responseMessages;
    }
}
