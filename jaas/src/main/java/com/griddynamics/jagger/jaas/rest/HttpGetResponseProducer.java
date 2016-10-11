package com.griddynamics.jagger.jaas.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Produces {@link org.springframework.http.ResponseEntity} response with possible results:
 * <ul>
 * <li>{@link org.springframework.http.HttpStatus#OK} if there is payload to return </li>
 * <li>{@link org.springframework.http.HttpStatus#NOT_FOUND} if there is nothing to return </li>
 * <li>{@link org.springframework.http.HttpStatus#NO_CONTENT} if there is a an empty collection to return </li>
 * </ul>
 */
public class HttpGetResponseProducer<T, R> {
    
    private final T responseSource;
    private final Function<T, R> responseFunction;
    
    public HttpGetResponseProducer(T responseSource, Function<T, R> responseFunction) {
        this.responseSource = responseSource;
        this.responseFunction = responseFunction;
    }
    
    public static <T, R> ResponseEntity<R> produce(T responseSource, Function<T, R> responseFunction) {
        return new HttpGetResponseProducer<>(responseSource, responseFunction).produce();
    }
    
    public ResponseEntity<R> produce() {
        if (responseSource != null) {
            R response = responseFunction.apply(responseSource);
            if (Objects.nonNull(response)) {
                if (isEmptyCollection(response)) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    private boolean isEmptyCollection(Object object) {
        if (object instanceof Collection) {
            return ((Collection) object).isEmpty();
        } else if (object instanceof Map) {
            return ((Map) object).isEmpty();
        }
        return false;
    }
}
