package com.griddynamics.jagger.test.javabuilders.smoke_components;

import com.griddynamics.jagger.engine.e1.collector.DefaultResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.invocation.NotNullInvocationListener;
import com.griddynamics.jagger.invoker.v2.DefaultInvokerProvider;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.test.javabuilders.utils.EndpointsProvider;
import com.griddynamics.jagger.test.javabuilders.utils.JaggerPropertiesProvider;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * For smoke tests of JTestDefinition each optional parameter should be specified and unspecified at least once
 */
public class TestDefinitionVariations {

    private static final List<JHttpQuery> SINGLE_QUERY = Collections.singletonList(new JHttpQuery().get().path("/sleep/10"));
    private JaggerPropertiesProvider properties;

    public TestDefinitionVariations(JaggerPropertiesProvider properties) {
        this.properties = properties;
    }

    /**
     * All optional fields are unspecified
     */
    public JTestDefinition allDefaults(){
        return JTestDefinition.builder(Id.of("all defaults"), getEndpoints()).build();
    }

    public JTestDefinition singleQuery(){
        return JTestDefinition.builder(Id.of("single query"), getEndpoints())
                .withQueryProvider(SINGLE_QUERY)
                .build();
    }

    public JTestDefinition listOfQueries(){
        // create query with different latency to test query rotation
        Iterable<JHttpQuery> queries = Stream.of("100", "50", "25")
                .map(q -> new JHttpQuery().get().path("/sleep", q))
                .collect(Collectors.toList());
        return JTestDefinition.builder(Id.of("queries list"), getEndpoints())
                .withQueryProvider(queries)
                .build();
    }

    public JTestDefinition withComment(){
        return JTestDefinition.builder(Id.of("comment"), getEndpoints())
                .withComment("definition with comment")
                .build();
    }

    public JTestDefinition singleValidator(){
        return JTestDefinition.builder(Id.of("single validator"), getEndpoints())
                .addValidator(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class))
                .build();
    }

    public JTestDefinition listOfValidators(){
        return JTestDefinition.builder(Id.of("validators list"), getEndpoints())
                .addValidators(Arrays.asList(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class),
                        DefaultResponseValidatorProvider.of(TrueValidator.class)))
                .build();
    }

    public JTestDefinition allFields(){
        return JTestDefinition.builder(Id.of("all fields"), getEndpoints())
                .withQueryProvider(SINGLE_QUERY)
                .withInvoker(DefaultInvokerProvider.of(DummyCustomInvoker.class))
                .addValidators(Arrays.asList(DefaultResponseValidatorProvider.of(NotNullResponseValidator.class),
                        DefaultResponseValidatorProvider.of(TrueValidator.class)))
                .withComment("all fields definition")
                .addListener(new NotNullInvocationListener())
                .build();
    }


    public JTestDefinition load_cpu_service_10000000(){
        return JTestDefinition.builder(Id.of("load-cpu-service-10000000"), getEndpoints())
                .withQueryProvider(Collections.singletonList(new JHttpQuery().get().path("/load/10000000")))
                .addListener(new NotNullInvocationListener())
                .build();
    }

    public JTestDefinition allocate_memory_service_1000000x200(){
        return JTestDefinition.builder(Id.of("allocate-memory-service-1000000x200"), getEndpoints())
                .withQueryProvider(Collections.singletonList(new JHttpQuery().get().path("/allocate/1000000x200")))
                .build();
    }


    private Iterable<JHttpEndpoint> getEndpoints() {
        return new EndpointsProvider(properties);
    }

}
