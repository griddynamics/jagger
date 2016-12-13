package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.IntStream;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class JHttpResponseStatusValidator extends ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse> {

    private Set<Integer> validStatusCodes = newHashSet();

    private Pattern validStatusCodesRegexp;

    public JHttpResponseStatusValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "Default Http Response Status Validator";
    }

    /**
     * Validates JHttpResponse status code.<p>
     * If {@link JHttpResponseStatusValidator#validStatusCodesRegexp} is not null, it will be used for validation, otherwise
     * {@link JHttpResponseStatusValidator#validStatusCodes} is used.
     *
     * @param query    - the query of current invocation
     * @param endpoint - the endpoint of current invocation
     * @param result   - the result of invocation
     * @param duration - the duration of invocation
     * @return true if response code is valid, false otherwise.
     */
    @Override
    public boolean validate(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result, long duration) {
        if (validStatusCodesRegexp != null) {
            Matcher matcher = validStatusCodesRegexp.matcher(result.getStatus().toString());
            return matcher.matches();
        }
        return validStatusCodes.contains(result.getStatus().value());
    }

    /**
     * @param codes valid codes
     * @return this
     * @apiNote Usage: <p>
     * {@code
     * JHttpResponseStatusValidator validator = new JHttpResponseStatusValidator(taskId, sessionId, kernelContext)
     * .withValidCodes(200, 201, 202);
     * }
     */
    public JHttpResponseStatusValidator withValidCodes(Integer... codes) {
        validStatusCodes.addAll(Arrays.asList(codes));
        return this;
    }

    /**
     * @param range IntStream of valid codes
     * @return this
     * @apiNote Usage: <p>
     * {@code
     * JHttpResponseStatusValidator validator = new JHttpResponseStatusValidator(taskId, sessionId, kernelContext)
     * .withValidCodes(IntStream.range(200, 399)); // from 200 inclusive to 399 exclusive
     * <p>
     * // or
     * <p>
     * JHttpResponseStatusValidator validator = new JHttpResponseStatusValidator(taskId, sessionId, kernelContext)
     * .withValidCodes(IntStream.rangeClosed(200, 399)); // from 200 inclusive to 399 inclusive
     * <p>
     * }
     */
    public JHttpResponseStatusValidator withValidCodes(IntStream range) {
        validStatusCodes.addAll(range.boxed().collect(toSet()));
        return this;
    }

    /**
     * @param stringRegExp RegExp defining valid codes
     * @return this
     * @throws PatternSyntaxException If the expression's syntax is invalid
     * @apiNote Usage: <p>
     * {@code
     * JHttpResponseStatusValidator validator = new JHttpResponseStatusValidator(taskId, sessionId, kernelContext)
     * .withValidCodes("(200|201|202)");
     * }
     */
    public JHttpResponseStatusValidator withValidCodes(String stringRegExp) {
        validStatusCodesRegexp = Pattern.compile(stringRegExp);
        return this;
    }
}
