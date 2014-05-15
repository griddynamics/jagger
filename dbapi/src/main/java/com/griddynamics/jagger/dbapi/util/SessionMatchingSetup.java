package com.griddynamics.jagger.dbapi.util;

import java.util.Set;

/** Class is used to setup matching strategy for sessions with same tests
 *
 * @details
 * If you are fetching data from DB for several sessions, you may want to compare @n
 * results for matching tests. With use of this class you can tell database service @n
 * what attributes to check to determine that tests are matching @n
 */
public class SessionMatchingSetup {

    /** When true - only matched tests for different sessions will be returned by database service */
    private boolean showOnlyMatchedTests;

    /** Set of attributes used by database service to determine that tests are matching. Pass empty set if you don't need matching at all */
    private Set<MatchBy> matchingSetup;

    public SessionMatchingSetup(boolean showOnlyMatchedTests, Set<MatchBy> matchingSetup) {
        this.showOnlyMatchedTests = showOnlyMatchedTests;
        this.matchingSetup = matchingSetup;
    }

    public boolean isShowOnlyMatchedTests() {
        return showOnlyMatchedTests;
    }

    public Set<MatchBy> getMatchingSetup() {
        return matchingSetup;
    }

    public static enum MatchBy {
        DESCRIPTION,
        NAME,
        TERMINATION,
        CLOCK,
        CLOCK_VALUE,
        ALL                /* all above */
    }
}
