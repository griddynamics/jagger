package com.griddynamics.jagger.dbapi.model;

import java.util.Collections;
import java.util.List;

/**
 * User: amikryukov
 * Date: 11/26/13
 */
public class TestInfoNode extends AbstractIdentifyNode {

    @Deprecated
    public TestInfoNode() {}

    public TestInfoNode(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return Collections.EMPTY_LIST;
    }
}
