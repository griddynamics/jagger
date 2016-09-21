package com.griddynamics.jagger.webclient.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author asokol
 *         created 9/21/16
 */
@Configuration
public class ApplicationContext {

    @Bean
    public FileDownloadRequestHandler downloadServlet() {
        FileDownloadRequestHandler handler = new FileDownloadRequestHandler();
        handler.setFileStorage(fileStore());
        return handler;
    }

    @Bean
    public InMemoryFileStorage fileStore() {
        return new InMemoryFileStorage();
    }
}
