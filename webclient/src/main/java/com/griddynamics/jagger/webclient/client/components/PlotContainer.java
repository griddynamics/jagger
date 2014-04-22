package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Only for demo - container with Label
 * plot container will be instead of label in future
 */
public class PlotContainer extends SimplePanel {

    private Label label;
    private String id;

    public PlotContainer(String id, String labelText, String style) {
        super();
        this.id = id;
        this.label = new Label(labelText);
        label.getElement().getStyle().setFontSize(20, Style.Unit.PX);
        add(label);
        setStyleName(style);
    }

    public Label getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }

    public void setLabel(Label label) {
        remove(this.label);
        this.label = label;
        add(this.label);
    }
}
