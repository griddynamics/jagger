package com.griddynamics.jagger.invoker.scenario;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Global context for {@link JHttpUserScenario} which is used for storing global parameters: endpoints, headers, etc.
 *
 * @ingroup Main_Http_User_Scenario_group
 */
public class JHttpScenarioGlobalContext {
    private String userName;
    private String password;
    private JHttpEndpoint globalEndpoint;
    private HttpHeaders globalHeaders;

    public JHttpScenarioGlobalContext copy() {
        JHttpScenarioGlobalContext copy = new JHttpScenarioGlobalContext();
        return copy.withBasicAuth(this.userName, this.password)
                .withGlobalEndpoint(JHttpEndpoint.copyOf(this.globalEndpoint))
                .withGlobalHeaders(CopyUtil.copyOf(this.globalHeaders));
    }

    /**
     * Sets endpoint for all steps.
     * Endpoint still can be overridden in {@link JHttpUserScenarioStep.Builder#withPreProcessFunction}
     *
     * @param globalEndpoint global endpoint to set
     */
    public JHttpScenarioGlobalContext withGlobalEndpoint(JHttpEndpoint globalEndpoint) {
        this.globalEndpoint = globalEndpoint;
        return this;
    }

    /**
     * Sets basic auth credentials for all steps.
     */
    public JHttpScenarioGlobalContext withBasicAuth(String userName, String password) {
        this.userName = userName;
        this.password = password;
        return this;
    }

    /**
     * Sets headers for all steps.
     *
     * @param globalHeaders global headers to set
     */
    public JHttpScenarioGlobalContext withGlobalHeaders(HttpHeaders globalHeaders) {
        this.globalHeaders = globalHeaders;
        return this;
    }

    /**
     * Sets headers for all steps.
     *
     * @param globalHeaders global headers to set
     */
    public JHttpScenarioGlobalContext withGlobalHeaders(Map<String, List<String>> globalHeaders) {
        initHeadersIfNull();
        this.globalHeaders.putAll(globalHeaders);
        return this;
    }

    /**
     * Sets header for all steps.
     * Endpoint still can be overridden in {@link JHttpUserScenarioStep.Builder#withPreProcessFunction}
     */
    public JHttpScenarioGlobalContext withGlobalHeader(String key, String value) {
        initHeadersIfNull();
        globalHeaders.put(key, newArrayList(value));
        return this;
    }

    /**
     * Sets cookies for all steps.
     *
     * @param cookies global cookies to set
     */
    public JHttpScenarioGlobalContext withGlobalCookies(Map<String, String> cookies) {
        initHeadersIfNull();
        cookies.entrySet().forEach(entry -> this.globalHeaders.add("Cookie", entry.getKey() + "=" + entry.getValue()));
        return this;
    }

    /**
     * Sets cookie for all steps.
     */
    public JHttpScenarioGlobalContext withGlobalCookie(String name, String value) {
        initHeadersIfNull();
        this.globalHeaders.add("Cookie", name + "=" + value);
        return this;
    }

    private void initHeadersIfNull() {
        if (this.globalHeaders == null)
            this.globalHeaders = new HttpHeaders();
    }

    public JHttpEndpoint getGlobalEndpoint() {
        return globalEndpoint;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public HttpHeaders getGlobalHeaders() {
        return globalHeaders;
    }

    public List<String> getGlobalCookies() {
        return globalHeaders.get("Cookie");
    }
}
