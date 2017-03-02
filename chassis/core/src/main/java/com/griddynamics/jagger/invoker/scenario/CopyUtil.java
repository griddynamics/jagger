package com.griddynamics.jagger.invoker.scenario;

import org.springframework.http.HttpHeaders;

import java.util.LinkedList;

public class CopyUtil {

    public static HttpHeaders copyOf(HttpHeaders headers) {
        if (headers == null)
            return null;
        HttpHeaders headersCopy = new HttpHeaders();
        headers.forEach((key, values) -> headersCopy.put(key, new LinkedList<>(values)));
        return headersCopy;
    }
}
