package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.IntStream;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

/**
 * Provides default response status validator {@link JHttpResponseStatusValidator}.
 */
public class DefaultResponseStatusValidatorProvider implements ResponseValidatorProvider {

    private Set<Integer> validStatusCodes = newHashSet();
    private Pattern validStatusCodesRegexp;

    public DefaultResponseStatusValidatorProvider(Builder builder) {
        this.validStatusCodes = builder.validStatusCodes;
        this.validStatusCodesRegexp = builder.validStatusCodesRegexp;
    }

    @Override
    public ResponseValidator<?, ?, ?> provide(String sessionId, String taskId, NodeContext kernelContext) {
        JHttpResponseStatusValidator jHttpResponseStatusValidator = new JHttpResponseStatusValidator(taskId, sessionId, kernelContext);
        jHttpResponseStatusValidator.setValidStatusCodes(validStatusCodes);
        jHttpResponseStatusValidator.setValidStatusCodesRegexp(validStatusCodesRegexp);
        return jHttpResponseStatusValidator;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Set<Integer> validStatusCodes = newHashSet();
        private Pattern validStatusCodesRegexp;

        public DefaultResponseStatusValidatorProvider build() {
            return new DefaultResponseStatusValidatorProvider(this);
        }

        /**
         * @param codes valid codes
         * @return this
         * @apiNote Usage: <pre>
         * {@code
         * DefaultResponseStatusValidatorProvider validator = DefaultResponseStatusValidatorProvider.builder()
         *                                                    .withValidCodes(200, 201, 202).build();
         * }
         * <pre/>
         */
        public Builder withValidCodes(Integer... codes) {
            validStatusCodes.addAll(Arrays.asList(codes));
            return this;
        }

        /**
         * @param range IntStream of valid codes
         * @return this
         * @apiNote Usage: <pre>
         * {@code
         * DefaultResponseStatusValidatorProvider validator = DefaultResponseStatusValidatorProvider.builder()
         *                                                    .withValidCodes(IntStream.range(200, 399)).build(); // from 200 inclusive to 399 exclusive
         *
         * // or
         *
         * DefaultResponseStatusValidatorProvider validator = DefaultResponseStatusValidatorProvider.builder()
         *                                                    .withValidCodes(IntStream.rangeClosed(200, 399)).build(); // from 200 inclusive to 399 inclusive
         *
         * }
         * <pre/>
         */
        public Builder withValidCodes(IntStream range) {
            validStatusCodes.addAll(range.boxed().collect(toSet()));
            return this;
        }

        /**
         * @param stringRegExp RegExp defining valid codes
         * @return this
         * @throws PatternSyntaxException If the expression's syntax is invalid
         * @apiNote Usage: <pre>
         * {@code
         * DefaultResponseStatusValidatorProvider validator = DefaultResponseStatusValidatorProvider.builder()
         *                                                    .withValidCodes("(200|201|202)").build();
         * }
         * <pre/>
         */
        public Builder withValidCodes(String stringRegExp) {
            validStatusCodesRegexp = Pattern.compile(stringRegExp);
            return this;
        }
    }
}
