package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.dnd.core.client.*;


/**
 * Draggable container that holds plot representation */
public class PlotContainer extends VerticalPanel {

    private PlotRepresentation plotRepresentation;

    private Label plotHeader;

    private HorizontalPanel dragPanel;

    public PlotContainer(String id, Label plotHeader, PlotRepresentation chart) {
        super();
        this.getElement().setId(id);
        this.plotRepresentation = chart;
        this.plotHeader = plotHeader;
        initContainer();
    }

    private void initDragAndDropBehavior() {

        DropTarget target = new DropTarget(this) {
            @Override
            protected void onDragDrop(DndDropEvent event) {
                super.onDragDrop(event);
                PlotContainer incoming = (PlotContainer) event.getData();
                PlotContainer current = (PlotContainer) component;
                swap(incoming, current);
            }
        };

        target.setFeedback(DND.Feedback.BOTH);


        new DragSource(dragPanel) {
            @Override
            protected void onDragStart(DndDragStartEvent event) {
                super.onDragStart(event);
                // by default drag is allowed
                event.setData(dragPanel.getParent());
            }
        };
    }

    // swap data of containers
    private void swap(PlotContainer c1, PlotContainer c2) {
        String id1 = c1.getElement().getId();
        String id2 = c2.getElement().getId();

        Label header1 = c1.getPlotHeader();
        Label header2 = c2.getPlotHeader();

        PlotRepresentation ch1 = c1.getPlotRepresentation();
        PlotRepresentation ch2 = c2.getPlotRepresentation();

        c1.getElement().setId(id2);
        c2.getElement().setId(id1);
        c1.setPlotRepresentation(ch2);
        c2.setPlotRepresentation(ch1);
        c1.setPlotHeader(header2);
        c2.setPlotHeader(header1);
    }

    private void initContainer() {
        this.setWidth("100%");
        dragPanel = new HorizontalPanel();
        dragPanel.setVerticalAlignment(ALIGN_MIDDLE);
        dragPanel.setSize("100%", "20px");
        dragPanel.addStyleName(JaggerResources.INSTANCE.css().dragLabel());
        dragPanel.add(plotHeader);
        plotHeader.setHorizontalAlignment(ALIGN_LEFT);
        dragPanel.setSpacing(3);
        add(dragPanel);
        add(plotRepresentation);

        initDragAndDropBehavior();
    }

    public void setPlotRepresentation(PlotRepresentation plotRepresentation) {
        remove(this.plotRepresentation);
        this.plotRepresentation = plotRepresentation;
        add(this.plotRepresentation);
    }

    public PlotRepresentation getPlotRepresentation() {
        return plotRepresentation;
    }

    public Label getPlotHeader() {
        return plotHeader;
    }

    public void setPlotHeader(Label plotHeader) {
        dragPanel.remove(this.plotHeader);
        this.plotHeader = plotHeader;
        dragPanel.add(this.plotHeader);
    }

    @Override
    public void setHeight(String height) {
        plotRepresentation.getSimplePlot().setHeight(height);
    }
}