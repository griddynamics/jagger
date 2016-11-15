package com.griddynamics.jagger.test.jagger2;

import com.griddynamics.jagger.invoker.v2.JHttpQuery;

import java.util.Iterator;
import java.util.stream.Stream;

public class QueriesProvider implements Iterable {
    private String staticPart;

    public QueriesProvider() {
        this.staticPart = "";
    }

    public QueriesProvider(String staticPart) {
        this.staticPart = staticPart;
    }

    @Override
    public Iterator iterator() {
        return Stream.of("55", "asd", "12", "77").map(q -> new JHttpQuery().get().path(staticPart, q)).iterator();
    }
}
