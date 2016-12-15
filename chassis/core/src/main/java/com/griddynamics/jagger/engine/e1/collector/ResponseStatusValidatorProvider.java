package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.IntStream;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

/**
 * Provides default response status validator {@link JHttpResponseStatusValidator}.
 */
public class ResponseStatusValidatorProvider implements ResponseValidatorProvider {

    private Set<Integer> validStatusCodes = newHashSet();
    private Pattern validStatusCodesRegexp;

    private ResponseStatusValidatorProvider(Collection<Integer> validStatusCodes) {
        this.validStatusCodes.addAll(validStatusCodes);
    }

    private ResponseStatusValidatorProvider(Pattern validStatusCodesRegexp) {
        this.validStatusCodesRegexp = validStatusCodesRegexp;
    }

    @Override
    public ResponseValidator<?, ?, ?> provide(String sessionId, String taskId, NodeContext kernelContext) {
        JHttpResponseStatusValidator jHttpResponseStatusValidator = new JHttpResponseStatusValidator(taskId, sessionId, kernelContext);
        jHttpResponseStatusValidator.setValidStatusCodes(validStatusCodes);
        jHttpResponseStatusValidator.setValidStatusCodesRegexp(validStatusCodesRegexp);
        return jHttpResponseStatusValidator;
    }

    /**
     * @param codes valid codes
     * @return this
     * @apiNote Usage: <pre>
     * {@code
     * ResponseStatusValidatorProvider validator = ResponseStatusValidatorProvider.withValidCodes(200, 201, 202);
     * }
     * <pre/>
     */
    public static ResponseStatusValidatorProvider withValidCodes(Integer... codes) {
        return new ResponseStatusValidatorProvider(Arrays.asList(codes));
    }

    /**
     * @param range IntStream of valid codes
     * @return this
     * @apiNote Usage: <pre>
     * {@code
     * ResponseStatusValidatorProvider validator = ResponseStatusValidatorProvider.withValidCodes(IntStream.range(200, 399)); // from 200 inclusive to 399 exclusive
     *
     * // or
     *
     * ResponseStatusValidatorProvider validator = ResponseStatusValidatorProvider.withValidCodes(IntStream.rangeClosed(200, 399)); // from 200 inclusive to 399 inclusive
     * }
     * <pre/>
     */
    public static ResponseStatusValidatorProvider withValidCodes(IntStream range) {
        return new ResponseStatusValidatorProvider(range.boxed().collect(toSet()));
    }

    /**
     * @param stringRegExp RegExp defining valid codes
     * @return this
     * @throws PatternSyntaxException If the expression's syntax is invalid
     * @apiNote Usage: <pre>
     * {@code
     * ResponseStatusValidatorProvider validator = ResponseStatusValidatorProvider.withValidCodes("(200|201|202)");
     * }
     * <pre/>
     */
    public static ResponseStatusValidatorProvider withValidCodes(String stringRegExp) {
        return new ResponseStatusValidatorProvider(Pattern.compile(stringRegExp));
    }
}
