package com.griddynamics.jagger.test.jaas.provider;

import com.griddynamics.jagger.invoker.http.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.HttpRelated;

import java.util.LinkedList;
import java.util.List;

/**
 * A base class for all JHttpQuery based query providers.
 */
public abstract class QueryProvider_Abstract implements Iterable, HttpRelated {
    protected List<JHttpQuery<String>> queries = new LinkedList<>();

    public QueryProvider_Abstract() {}
}