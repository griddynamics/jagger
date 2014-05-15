package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.googlecode.gflot.client.Pan;
import com.googlecode.gflot.client.SimplePlot;
import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * Class that hold widgets of type PlotContainer with dynamic layout feature.
 */
public class PlotsPanel extends Composite {

    interface PlotsPanelUiBinder extends UiBinder<Widget, PlotsPanel> {
    }

    private static PlotsPanelUiBinder ourUiBinder = GWT.create(PlotsPanelUiBinder.class);

    @UiField
    /* Main layout panel where all children will be */
    protected DynamicLayoutPanel<PlotContainer> layoutPanel;

    @UiField
    // temporary layout control panel todo: decide and implement final view of layout control.
    protected HorizontalPanel buttonPanel;

    /**
     * default value for container height */
    private Integer plotContainerHeight = 150;

    public PlotsPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));

        setUpButtonPanel();
    }

    /**
     * Temporary solution of question how to change layout.
     */
    private void setUpButtonPanel() {

        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

        TextButton changeLayout = new TextButton("Change layout");
        changeLayout.addSelectHandler(new ChangeLayoutHandler());

        TextButton oneColumnLButton = new TextButton("One column");
        oneColumnLButton.addSelectHandler(new OneColumnLHandler());

        TextButton twoColumnsLButton = new TextButton("Two columns");
        twoColumnsLButton.addSelectHandler(new TwoColumnsHandler());

        Slider heightSlider = new Slider();
        heightSlider.setValue(plotContainerHeight);
        heightSlider.setMaxValue(500);
        heightSlider.setMinValue(100);
        heightSlider.addValueChangeHandler(new HeightSliderValueChangeHandler());

        buttonPanel.add(changeLayout);
        buttonPanel.add(oneColumnLButton);
        buttonPanel.add(twoColumnsLButton);
        buttonPanel.add(heightSlider);

    }

    /**
     * Remove widget from layoutPanel by element id
     * @param elementId Id of widget element (Widget.getElement.getId())*/
    public void removeElementById(String elementId) {
        layoutPanel.removeChild(elementId);
    }

    /**
     * Remove all widgets from layoutPanel */
    public void clear() {
        layoutPanel.clear();
    }

    /**
     * Add widget to layoutPanel
     * @param plotContainer child widget */
    public void addElement(PlotContainer plotContainer) {
        plotContainer.setHeight(plotContainerHeight + "px");
        layoutPanel.addChild(plotContainer);
    }

    /**
     * Check if PlotsPanel contains element with certain id
     * @param plotId id of element to identify
     * @return true if element found with given plotId, false otherwise */
    public boolean containsElementWithId(String plotId) {
        return layoutPanel.containsElementWithId(plotId);
    }


    /**
     * Pan All plots that contains in PlotsPanel
     * @param pan amount to pan left */
    public void panAllPlots(int pan) {
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot plot = pc.getPlotRepresentation().getSimplePlot();
            plot.pan(Pan.create().setLeft(pan));
        }
    }


    /**
     * Zoom all plots in PlotsPanel */
    public void zoomIn() {
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot plot = pc.getPlotRepresentation().getSimplePlot();
            plot.zoom();
        }
    }

    /**
     * Zoom out all plots in PlotsPanel */
    public void zoomOut() {
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot plot = pc.getPlotRepresentation().getSimplePlot();
            plot.zoomOut();
        }
    }

    /**
     * Check if PlotsPanel contains any plots.
     * @return true if it is empty, false otherwise */
    public boolean isEmpty() {
        return layoutPanel.getWidgetCount() == 0;
    }

    /**
     * @return minimum X axis value */
    public double getMinXAxisValue() {
        // no widgets in panel
        assert layoutPanel.getWidgetCount() > 0;

        SimplePlot plot = layoutPanel.getFirstChild().getPlotRepresentation().getSimplePlot();

        return plot.getAxes().getX().getMinimumValue();
    }

    /**
     * @return maximum X axis value */
    public double getMaxXAxisValue() {
        // no widgets in panel
        assert layoutPanel.getWidgetCount() > 0;

        SimplePlot plot = layoutPanel.getFirstChild().getPlotRepresentation().getSimplePlot();

        return plot.getAxes().getX().getMaximumValue();
    }


    private class ChangeLayoutHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            layoutPanel.changeLayout(layoutPanel.getLayout().getNext());
        }
    }

    private class OneColumnLHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            layoutPanel.changeLayout(DynamicLayoutPanel.Layout.ONE_COLUMN);
        }
    }

    private class TwoColumnsHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            layoutPanel.changeLayout(DynamicLayoutPanel.Layout.TWO_COLUMNS);
        }
    }

    private class HeightSliderValueChangeHandler implements ValueChangeHandler<Integer> {
        @Override
        public void onValueChange(ValueChangeEvent<Integer> integerValueChangeEvent) {
            plotContainerHeight = integerValueChangeEvent.getValue();
            layoutPanel.changeChildrenHeight(plotContainerHeight + "px");
        }
    }
}
