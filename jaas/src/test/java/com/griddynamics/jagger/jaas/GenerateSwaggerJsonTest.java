package com.griddynamics.jagger.jaas;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.jagger.jaas.config.SwaggerConfig;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {JaasStarter.class, SwaggerConfig.class})
@AutoConfigureMockMvc
public class GenerateSwaggerJsonTest {

    private static final Logger logger = LoggerFactory.getLogger(GenerateSwaggerJsonTest.class);

    @Value("${server.contextPath}")
    private String basePath;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createSpringfoxSwaggerJson() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/v2/api-docs")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String outputDir = System.getProperty("io.springfox.staticdocs.outputDir");
        MockHttpServletResponse response = mvcResult.getResponse();
        String swaggerJson = response.getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(swaggerJson, Object.class);
        String prettySwaggerJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        prettySwaggerJson = StringUtils.replace(prettySwaggerJson, "\"basePath\" : \"/\"", "\"basePath\" : \"" + basePath + "\"");
        Files.createDirectories(Paths.get(outputDir));
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir, "swagger.json"), StandardCharsets.UTF_8)){
            writer.write(prettySwaggerJson);
        }
        logger.info("swagger.json successfully generated!");
    }
}
