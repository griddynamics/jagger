package com.griddynamics.jagger.user.builders;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.Validator;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.scenario.Calibrator;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.QueryPoolLoadBalancer;
import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import com.griddynamics.jagger.invoker.ScenarioFactory;
import com.griddynamics.jagger.user.TestDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * @author asokol
 *         created 10/14/16
 */
public class JTestDescriptionBuilder {

    private String id;
    private String comment;
    private String name;
    private String description;
    private String version;

    private ScenarioFactory scenarioFactory;

    private List<Provider<InvocationListener<Object, Object, Object>>> listeners;
    private List<KernelSideObjectProvider<Validator>> validators;
    private Calibrator calibrator;


    public JTestDescriptionBuilder() {
        this.description = "";
        this.scenarioFactory = new QueryPoolScenarioFactory<>();
        this.validators = new ArrayList<>();
    }

    public JTestDescriptionBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public JTestDescriptionBuilder withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public JTestDescriptionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public JTestDescriptionBuilder withEndpointsProvider(Iterable endpointsProvider) {
        ((QueryPoolScenarioFactory) scenarioFactory).setEndpointProvider(endpointsProvider);
        return this;
    }


    public JTestDescriptionBuilder withQueryProvider(Iterable queryProvider) {
        ((QueryPoolScenarioFactory) scenarioFactory).setEndpointProvider(queryProvider);
        return this;
    }

    public JTestDescriptionBuilder withLoadBalancer(QueryPoolLoadBalancer loadBalancer) {
        ((QueryPoolScenarioFactory) scenarioFactory).setLoadBalancer(loadBalancer);
        return this;
    }

    public JTestDescriptionBuilder withInvoker(Invoker invoker) {
        ((QueryPoolScenarioFactory) scenarioFactory).setInvokerClazz(invoker.getClass());
        return this;
    }

    public JTestDescriptionBuilder withListeners(List<Provider<InvocationListener<Object, Object, Object>>> listeners) {
        this.listeners = listeners;
        return this;
    }

    public JTestDescriptionBuilder withListener(Provider<InvocationListener<Object, Object, Object>> listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
        return this;
    }

    public JTestDescriptionBuilder withValidators(List<KernelSideObjectProvider<Validator>> validators) {
        this.validators = validators;
        return this;
    }

    public JTestDescriptionBuilder withVersion(String version) {
        this.version = version;
        return this;
    }


    public TestDescription build() {
        TestDescription testDescription = new TestDescription();
        testDescription.setName(name);
        testDescription.setScenarioFactory(scenarioFactory);
        testDescription.setListeners(listeners);
        testDescription.setDescription(description);
        testDescription.setValidators(validators);
        testDescription.setVersion(version);
        return testDescription;
    }
}
