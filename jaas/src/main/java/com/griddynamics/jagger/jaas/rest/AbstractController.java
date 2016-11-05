package com.griddynamics.jagger.jaas.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

abstract class AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

    <T, R> ResponseEntity<R> produceGetResponse(T responseSource, Function<T, R> responseFunction) {
        ResponseEntity<R> responseEntity = HttpGetResponseProducer.produce(responseSource, responseFunction);
        LOGGER.debug("Produced response: {}", responseEntity);
        return responseEntity;
    }
}
