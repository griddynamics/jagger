package com.griddynamics.jagger.dbapi.model.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Rule {

    Logger log = LoggerFactory.getLogger(Rule.class);

    protected String id;
    protected String displayName;
    protected String rule = null;
    protected By filterBy = By.ID;

    public Rule() {}
    public Rule(String id, String displayName, String rule) {
        this.id = id;
        this.displayName = displayName;
        this.rule = rule;
    }
    public Rule(By filterBy, String id, String displayName, String rule) {
        this.filterBy = filterBy;
        this.id = id;
        this.displayName = displayName;
        this.rule = rule;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRule() {
        return rule;
    }

    public By getFilterBy() {
        return filterBy;
    }

    protected <M extends Rule> List<M> sort(final By by, List<M> inputList) {
        Collections.sort(inputList, new Comparator<M>() {
            @Override
            public int compare(M o1, M o2) {
                String param1 = "";
                String param2 = "";
                switch (by) {
                    case ID:
                        param1 = o1.getId();
                        param2 = o2.getId();
                        break;
                    case DISPLAY_NAME:
                        param1 = o1.getDisplayName();
                        param2 = o2.getDisplayName();
                        break;
                }
                int res = String.CASE_INSENSITIVE_ORDER.compare(param1,param2);
                return (res != 0) ? res : param1.compareTo(param2);
            }
        });

        return inputList;
    }

    protected <M extends Rule> List<M> removeDuplicates(final By by, List<M> inputList) {
        Set<String> params = new HashSet<String>();
        String param = "";
        List<M> duplicates = new ArrayList<M>();

        for(M o : inputList) {
            switch (by) {
                case ID:
                    param = o.getId();
                    break;
                case DISPLAY_NAME:
                    param = o.getDisplayName();
                    break;
            }

            if(params.contains(param)) {
                duplicates.add(o);
                log.error("Rule with \" + by + \" '\" + param + \"' already exists. New rule will be ignored");
            }
            params.add(param);
        }

        inputList.removeAll(duplicates);

        return inputList;
    }

    public static enum By {
        ID,
        DISPLAY_NAME
    }
}
