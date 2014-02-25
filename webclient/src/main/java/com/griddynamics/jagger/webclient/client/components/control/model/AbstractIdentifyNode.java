package com.griddynamics.jagger.webclient.client.components.control.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public abstract class AbstractIdentifyNode implements Serializable {

    /**
     * id in tree - unique for all nodes
     */
    protected String id;

    /**
     * representation of the node in control tree
     */
    protected String displayName;

    public AbstractIdentifyNode() {}
    public AbstractIdentifyNode(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
    public AbstractIdentifyNode(AbstractIdentifyNode that) {
        this(that.getId(),that.getDisplayName());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return List of children. empty list if has no children.
     */
    public abstract List<? extends AbstractIdentifyNode> getChildren();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractIdentifyNode that = (AbstractIdentifyNode) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
