package com.griddynamics.jagger.invoker.http.v2;

import java.util.HashMap;

/**
 * Created by aantonenko on 9/27/16.
 */
public class Example {

    public static void main(String[] args) {
        JHttpQuery<Integer> httpQuery = new JHttpQuery<Integer>()
                .get()
                .cookies(new HashMap<>())
                .queryParam("query param", "value")
                .clientParam("client param", new Object())
                .body(42);
    }
}
