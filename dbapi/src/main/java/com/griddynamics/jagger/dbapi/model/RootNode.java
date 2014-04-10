package com.griddynamics.jagger.dbapi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Have no view representation.
 * User: amikryukov
 * Date: 11/26/13
 */
public class RootNode extends AbstractIdentifyNode {

    SummaryNode summary;

    DetailsNode detailsNode;

    public SummaryNode getSummary() {
        return summary;
    }

    public void setSummary(SummaryNode summary) {
        this.summary = summary;
    }

    public DetailsNode getDetailsNode() {
        return detailsNode;
    }

    public void setDetailsNode(DetailsNode detailsNode) {
        this.detailsNode = detailsNode;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        List<AbstractIdentifyNode> result = new ArrayList<AbstractIdentifyNode>();
        result.add(summary);
        if (detailsNode != null) result.add(detailsNode);
        return result;
    }
}
