package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

/**
 * Provides default response status validator {@link JHttpResponseStatusValidator}.
 */
public class JHttpResponseStatusValidatorProvider implements ResponseValidatorProvider {

    private Set<Integer> validStatusCodes = newHashSet();
    private List<Pattern> validStatusCodesRegexps = newArrayList();

    private JHttpResponseStatusValidatorProvider(Collection<Integer> validStatusCodes) {
        this.validStatusCodes.addAll(validStatusCodes);
    }

    private JHttpResponseStatusValidatorProvider(Pattern validStatusCodesRegexp) {
        this.validStatusCodesRegexps.add(validStatusCodesRegexp);
    }

    private JHttpResponseStatusValidatorProvider(List<Pattern> validStatusCodesRegexps) {
        this.validStatusCodesRegexps.addAll(validStatusCodesRegexps);
    }

    @Override
    public ResponseValidator<?, ?, ?> provide(String sessionId, String taskId, NodeContext kernelContext) {
        JHttpResponseStatusValidator jHttpResponseStatusValidator = new JHttpResponseStatusValidator(taskId, sessionId, kernelContext);
        jHttpResponseStatusValidator.setValidStatusCodes(validStatusCodes);
        jHttpResponseStatusValidator.setValidStatusCodesRegexps(validStatusCodesRegexps);
        return jHttpResponseStatusValidator;
    }

    /**
     * @param codes valid codes
     * @return new JHttpResponseStatusValidatorProvider with valid codes provided in codes
     * @apiNote Usage: <pre>
     * {@code
     * JHttpResponseStatusValidatorProvider validator = JHttpResponseStatusValidatorProvider.of(200, 201, 202);
     * }
     * <pre/>
     */
    public static JHttpResponseStatusValidatorProvider of(Integer... codes) {
        return new JHttpResponseStatusValidatorProvider(Arrays.asList(codes));
    }

    /**
     * @param range IntStream of valid codes
     * @return new JHttpResponseStatusValidatorProvider with valid codes provided in range
     * @apiNote Usage: <pre>
     * {@code
     * JHttpResponseStatusValidatorProvider validator = JHttpResponseStatusValidatorProvider.of(IntStream.range(200, 399)); // from 200 inclusive to 399 exclusive
     *
     * // or
     *
     * JHttpResponseStatusValidatorProvider validator = JHttpResponseStatusValidatorProvider.of(IntStream.rangeClosed(200, 399)); // from 200 inclusive to 399 inclusive
     * }
     * <pre/>
     */
    public static JHttpResponseStatusValidatorProvider of(IntStream range) {
        return new JHttpResponseStatusValidatorProvider(range.boxed().collect(toSet()));
    }

    /**
     * @param stringRegExp RegExp defining valid codes
     * @return new JHttpResponseStatusValidatorProvider with valid codes regExp provided in stringRegExp
     * @throws PatternSyntaxException If the expression's syntax is invalid
     * @apiNote Usage: <pre>
     * {@code
     * JHttpResponseStatusValidatorProvider validator = JHttpResponseStatusValidatorProvider.of("(200|201|202)");
     * }
     * <pre/>
     */
    public static JHttpResponseStatusValidatorProvider of(String stringRegExp) {
        return new JHttpResponseStatusValidatorProvider(Pattern.compile(stringRegExp));
    }

    /**
     * @param stringRegExps list of RegExps defining valid codes
     * @return new JHttpResponseStatusValidatorProvider with valid codes regExps provided in stringRegExps
     * @throws PatternSyntaxException If the expression's syntax is invalid
     * @apiNote Usage: <pre>
     * {@code
     * JHttpResponseStatusValidatorProvider validator = JHttpResponseStatusValidatorProvider.of(newArrayList("(200|201|202)"));
     * }
     * <pre/>
     */
    public static JHttpResponseStatusValidatorProvider of(List<String> stringRegExps) {
        List<Pattern> patterns = stringRegExps.stream().map(Pattern::compile).collect(Collectors.toList());
        return new JHttpResponseStatusValidatorProvider(patterns);
    }
}
