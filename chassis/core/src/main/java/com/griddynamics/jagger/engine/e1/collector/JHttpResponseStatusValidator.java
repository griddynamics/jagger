package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Validates JHttpResponse status code.
 * If {@link JHttpResponseStatusValidator#validStatusCodesRegexps} is not null, it will be used for validation, otherwise
 * {@link JHttpResponseStatusValidator#validStatusCodes} is used.
 */
public class JHttpResponseStatusValidator extends ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse> {

    private Set<Integer> validStatusCodes = newHashSet();

    private List<Pattern> validStatusCodesRegexps = newArrayList();

    public JHttpResponseStatusValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "Default Http Response Status Validator";
    }

    /**
     * Validates JHttpResponse status code.<p>
     * If {@link JHttpResponseStatusValidator#validStatusCodesRegexps} is not empty, it will be used for validation, otherwise
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
        if (isNotEmpty(validStatusCodesRegexps)) {
            return validStatusCodesRegexps.stream()
                    .map(regexp -> regexp.matcher(result.getStatus().toString()))
                    .allMatch(Matcher::matches);
        }
        return validStatusCodes.contains(result.getStatus().value());
    }

    public Set<Integer> getValidStatusCodes() {
        return validStatusCodes;
    }

    public void setValidStatusCodes(Set<Integer> validStatusCodes) {
        this.validStatusCodes = validStatusCodes;
    }

    public List<Pattern> getValidStatusCodesRegexps() {
        return validStatusCodesRegexps;
    }

    public void setValidStatusCodesRegexps(List<Pattern> validStatusCodesRegexp) {
        this.validStatusCodesRegexps = validStatusCodesRegexp;
    }
}
