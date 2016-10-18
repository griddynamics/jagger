package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * @author asokol
 *         created 10/18/16
 */
public class JTestDescriptionTest {

    private String name = "Name";
    private String version = "Version";
    private String description = "What a neat desxription!";
    private List<String> endpoints;
    private List<String> queries;

    JTestDescription jTestDescription;

    @Before
    public void setUp() throws Exception {
        endpoints = Arrays.asList("First enpoint", "Second endpoint", "Third endpoint");
        queries = Arrays.asList("First query", "Second query", "Third query");

        jTestDescription = JTestDescription.builder()
                .withDescription(description)
                .withEndpointsProvider(endpoints)
                .withQueryProvider(queries)
                .withName(name)
                .withVersion(version)
                .build();


    }

    @Test
    public void builder() throws Exception {
        Assert.assertThat(jTestDescription.getName(), is(name));
        Assert.assertThat(jTestDescription.getVersion(), is(version));
        Assert.assertThat(jTestDescription.getDescription(), is(description));
        Assert.assertThat(jTestDescription.getEndpoints(), is(endpoints));
        Assert.assertThat(jTestDescription.getQueries(), is(queries));

    }

}