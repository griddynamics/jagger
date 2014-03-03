package com.griddynamics.jagger.webclient.client.trends;

import com.griddynamics.jagger.webclient.client.mvp.AbstractPlaceHistoryMapper;
import com.griddynamics.jagger.webclient.client.mvp.PlaceWithParameters;

import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class TrendsPlace extends PlaceWithParameters {

    private static final String FRAGMENT = "fragment";

    private String token;
    private String url;

    private List<LinkFragment> linkFragments = Collections.EMPTY_LIST;

    public TrendsPlace(String token){
        this.token = token;
    }

    public Set<String> getSessionTrends() {
        if (linkFragments.size() == 1)
            return linkFragments.iterator().next().getSessionTrends();
        return Collections.EMPTY_SET;
    }

    public Set<String> getSelectedSessionIds() {
        // get all sessionIds
        Set<String> sessionIds = new HashSet<String>();
        for (LinkFragment fragment : linkFragments) {
            sessionIds.addAll(fragment.getSelectedSessionIds());
        }
        return sessionIds;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setLinkFragments(List<LinkFragment> linkFragments) {
        this.linkFragments = linkFragments;
    }

    public List<LinkFragment> getLinkFragments() {
        return linkFragments;
    }

    @Override
    public Map<String, Set<String>> getParameters() {

        Map<String, Set<String>> parameters = new LinkedHashMap<String, Set<String>>();
        if (linkFragments.isEmpty()) {
            return Collections.EMPTY_MAP;
        } else if (linkFragments.size() == 1) {
            return linkFragments.iterator().next().getParameters();
        }

        int index = 1;
        for (LinkFragment linkFragment : linkFragments) {

            Set<String> set = new HashSet<String>(1);
            set.add('(' + linkFragment.getParametersAsString() + ')');
            parameters.put(FRAGMENT + index++, set);
        }
        return parameters;
    }


    @Override
    public void setParameters(Map<String, Set<String>> parameters) {

        if (parameters != null && !parameters.isEmpty()) {
            if (parameters.keySet().iterator().next().contains(FRAGMENT)) {
                linkFragments = new ArrayList<LinkFragment>(parameters.size());
                for (Map.Entry<String, Set<String>> entry : parameters.entrySet()) {
                    LinkFragment linkFragment = new LinkFragment();
                    String value = entry.getValue().iterator().next();
                    value = value.substring(1, value.length() - 1);
                    Map<String, Set<String>> params = AbstractPlaceHistoryMapper.getParameters(value);
                    linkFragment.setParameters(params);
                    linkFragments.add(linkFragment);
                }
            } else {
                linkFragments = new ArrayList<LinkFragment>(1);
                LinkFragment linkFragment = new LinkFragment();
                linkFragment.setParameters(parameters);
                linkFragments.add(linkFragment);
            }
        }

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
